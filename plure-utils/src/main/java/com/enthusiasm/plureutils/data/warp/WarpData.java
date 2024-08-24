package com.enthusiasm.plureutils.data.warp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class WarpData {
    public final String world;
    public UUID owner;
    public double x, y, z;
    public float yaw, pitch;
    public boolean global;
    public int visits;
    public List<UUID> invited;

    public WarpData(String world, UUID owner, double x, double y, double z, float yaw, float pitch, boolean global, int visits, List<UUID> invited) {
        this.world = world;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.global = global;
        this.visits = visits;
        this.invited = invited;
    }

    public NbtCompound toNBT() {
        NbtCompound compound = new NbtCompound();
        NbtList invitedList = new NbtList();

        for (UUID uuid : invited) {
            invitedList.add(NbtString.of(uuid.toString()));
        }

        compound.putString("World", world);
        compound.putUuid("Owner", owner);
        compound.putDouble("X", x);
        compound.putDouble("Y", y);
        compound.putDouble("Z", z);
        compound.putFloat("Yaw", yaw);
        compound.putFloat("Pitch", pitch);
        compound.putBoolean("Global", global);
        compound.putInt("Visits", visits);
        compound.put("Invited", invitedList);

        return compound;
    }

    public static WarpData fromNBT(NbtCompound compound) {
        return new WarpData(
                compound.getString("World"),
                compound.getUuid("Owner"),
                compound.getDouble("X"),
                compound.getDouble("Y"),
                compound.getDouble("Z"),
                compound.getFloat("Yaw"),
                compound.getFloat("Pitch"),
                compound.getBoolean("Global"),
                compound.getInt("Visits"),
                readInvitedList(compound.getList("Invited", NbtElement.STRING_TYPE)));
    }

    private static List<UUID> readInvitedList(NbtList invitedList) {
        List<UUID> result = new ArrayList<>();

        for (NbtElement element : invitedList) {
            String uuidString = element.asString();

            result.add(UUID.fromString(uuidString));
        }

        return result;
    }
}
