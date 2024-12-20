package com.enthusiasm.plurelogger.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;

import com.enthusiasm.plurelogger.listener.events.BlockPlaceEvent;
import com.enthusiasm.plurelogger.utils.Sources;

@Mixin(NetherPortal.class)
public abstract class NetherPortalMixin {
    @Shadow
    @Final
    private WorldAccess world;

    @Inject(method = "method_30488", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void logPortalPlacement(BlockState state, BlockPos pos, CallbackInfo ci) {
        if (this.world instanceof ServerWorld world) {
            BlockPlaceEvent.invoke(world, pos.toImmutable(), state, null, Sources.PORTAL.getSource());
        }
    }
}
