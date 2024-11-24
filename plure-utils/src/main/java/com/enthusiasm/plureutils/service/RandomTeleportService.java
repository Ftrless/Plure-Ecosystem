package com.enthusiasm.plureutils.service;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;

import com.enthusiasm.plurecore.utils.ChunkUtils;
import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.config.ConfigManager;

public class RandomTeleportService {
    private static final Object2ObjectOpenHashMap<UUID, RandomTeleportService> LOCATORS = new Object2ObjectOpenHashMap<>();
    private static final ObjectOpenHashSet<UUID> PENDING_REMOVAL = new ObjectOpenHashSet<>();
    private static final ChunkTicketType<ChunkPos> LOCATE = ChunkTicketType.create("locate", Comparator.comparingLong(ChunkPos::toLong), 600);
    private static final Random RANDOM = Random.createLocal();
    private static final int MAX_ATTEMPTS = 256;
    private final ServerWorld world;
    private final UUID uuid;
    private final int minRadius;
    private final int radius;
    private final int centerX;
    private final int centerZ;
    private Consumer<Vec3d> callback;
    private ChunkPos queuedPos;
    private long stopTime;
    private int attempts;
    private int x;
    private int z;

    public static boolean isLocating(ServerPlayerEntity player) {
        return LOCATORS.containsKey(player.getUuid());
    }

    public static void update() {
        if (!LOCATORS.isEmpty()) {
            try {
                LOCATORS.values().forEach(RandomTeleportService::tick);
                LOCATORS.keySet().removeAll(PENDING_REMOVAL);
                PENDING_REMOVAL.clear();
            } catch (Exception e) {
                PlureUtilsEntrypoint.LOGGER.error("Error while updating random teleport service ", e);
            }
        }
    }

    public RandomTeleportService(ServerWorld world, UUID uuid) {
        this.world = world;
        this.uuid = uuid;
        this.minRadius = ConfigManager.getConfig().minRadiusRTP >> 4;
        this.radius = getRadius(world) >> 4;

        WorldBorder border = this.world.getWorldBorder();
        this.centerX = (int) border.getCenterX() >> 4;
        this.centerZ = (int) border.getCenterZ() >> 4;
    }

    private void tick() {
        if (System.currentTimeMillis() <= this.stopTime) {
            WorldChunk chunk = ChunkUtils.getChunkIfLoaded(this.world, this.queuedPos.x, this.queuedPos.z);

            if (chunk != null) {
                this.onChunkLoaded(chunk);
            }
        } else {
            this.onChunkLoaded(null);
        }
    }

    private void onChunkLoaded(WorldChunk chunk) {
        if (chunk == null) {
            PENDING_REMOVAL.add(this.uuid);
            this.callback.accept(null);
            return;
        }

        Vec3d pos = this.findSafePositionInChunk(chunk, this.x, this.z);

        if (pos != null) {
            PENDING_REMOVAL.add(this.uuid);
            this.callback.accept(pos);
            return;
        }

        PENDING_REMOVAL.add(this.uuid);

        this.newPosition();
    }

    public void findPosition(Consumer<Vec3d> callback) {
        this.callback = callback;
        this.stopTime = System.currentTimeMillis() + 20_000;
        this.newPosition();
    }

    private void newPosition() {
        if (++this.attempts > MAX_ATTEMPTS || System.currentTimeMillis() > this.stopTime) {
            this.callback.accept(null);
            return;
        }

        ChunkPos pos = RANDOM.nextBoolean()
                ? new ChunkPos(this.nextRandomValueWithMinimum(this.centerX), this.nextRandomValue(this.centerZ))
                : new ChunkPos(this.nextRandomValue(this.centerX), this.nextRandomValueWithMinimum(this.centerZ));

        this.x = pos.getCenterX();
        this.z = pos.getCenterZ();

        if (this.isValid(pos)) {
            this.queueChunk(pos);
        } else {
            this.newPosition();
        }
    }

    private void queueChunk(ChunkPos pos) {
        this.world.getChunkManager().addTicket(LOCATE, pos, 0, pos);
        this.queuedPos = pos;
        PENDING_REMOVAL.remove(this.uuid);
        LOCATORS.put(this.uuid, this);
    }

    private Vec3d findSafePositionInChunk(WorldChunk chunk, int centerX, int centerZ) {
        int negativeDiff = 5;
        int positiveDiff = negativeDiff - 1;

        for (int x = centerX - negativeDiff; x <= centerX + positiveDiff; x++) {
            for (int z = centerZ - negativeDiff; z <= centerZ + positiveDiff; z++) {
                int y = this.getY(chunk, x, z);

                if (this.isSafe(chunk, x, y, z)) {
                    return new Vec3d(x + 0.5D, y + 1, z + 0.5D);
                }
            }
        }

        return null;
    }

    private boolean isSafe(WorldChunk chunk, int centerX, int y, int centerZ) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(centerX, y, centerZ);

        if (!this.isSafeBelowPlayer(chunk.getBlockState(mutable)) ||
                !this.isSafeSurroundingPlayer(chunk.getBlockState(mutable.move(Direction.UP))) ||
                !this.world.isSpaceEmpty(EntityType.PLAYER.createSimpleBoundingBox(centerX + 0.5D, y + 1, centerZ + 0.5D))
        ) {
            return false;
        }

        int radius = 3;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                if (x != centerX || z != centerZ) {
                    BlockState state = chunk.getBlockState(mutable.set(x, y, z));
                    if (!this.isSafeSurroundingBelowPlayer(state) || !this.isSafeSurroundingPlayer(chunk.getBlockState(mutable.move(Direction.UP)))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isSafeBelowPlayer(BlockState state) {
        Block block = state.getBlock();
        return this.isSafeSurroundingBelowPlayer(state) && block != Blocks.BAMBOO;
    }

    private boolean isSafeSurroundingBelowPlayer(BlockState state) {
        Block block = state.getBlock();

        return (state.blocksMovement() || block == Blocks.SNOW) &&
                block != Blocks.CACTUS &&
                block != Blocks.MAGMA_BLOCK;
    }

    private boolean isSafeSurroundingPlayer(BlockState state) {
        Block block = state.getBlock();
        return !state.isIn(BlockTags.FIRE) &&
                !state.isIn(BlockTags.CAMPFIRES) &&
                block != Blocks.LAVA &&
                block != Blocks.POWDER_SNOW &&
                block != Blocks.MAGMA_BLOCK &&
                block != Blocks.CACTUS &&
                block != Blocks.SWEET_BERRY_BUSH;
    }

    private int getY(WorldChunk chunk, int x, int z) {
        if (!this.world.getDimension().hasCeiling()) {
            return chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        }

        int bottomY = chunk.getBottomY();
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, this.world.getHeight(), z);

        boolean isAir = false;
        boolean isAirBelow = false;
        while (mutable.getY() >= bottomY && isAirBelow || !isAir) {
            isAir = isAirBelow;
            isAirBelow = chunk.getBlockState(mutable.move(Direction.DOWN)).isAir();
        }

        return mutable.getY();
    }

    private boolean isValid(ChunkPos pos) {
        if (this.world.getWorldBorder().contains(pos)) {
            return this.isBiomeValid(this.world.getBiome(new BlockPos(this.x, this.world.getLogicalHeight(), this.z)));
        }

        return false;
    }

    private boolean isBiomeValid(RegistryEntry<Biome> biome) {
        for (TagKey<Biome> biomeTag : ConfigManager.getConfig().blackListedBiomeTags) {
            if (biome.isIn(biomeTag)) {
                return false;
            }
        }

        RegistryKey<Biome> key = this.world.getRegistryManager().get(RegistryKeys.BIOME).getKey(biome.value()).orElse(null);

        return !ConfigManager.getConfig().blackListedBiomes.contains(key);
    }

    private int nextRandomValue(int center) {
        return MathHelper.nextInt(RANDOM, center - this.radius, center + this.radius);
    }

    private int nextRandomValueWithMinimum(int center) {
        return RANDOM.nextBoolean()
                ? MathHelper.nextInt(RANDOM, center + this.minRadius, center + this.radius)
                : MathHelper.nextInt(RANDOM, center - this.radius, center - this.minRadius);
    }

    private static int getRadius(World world) {
        final int borderRadius = (int) (world.getWorldBorder().getSize() / 2) - 16;
        final int radius = ConfigManager.getConfig().maxRadiusRTP;

        if (radius < 0) {
            return borderRadius;
        }

        return Math.min(borderRadius, radius);
    }
}
