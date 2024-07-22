package com.enthusiasm.plureutils.data.home;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeData {
    public String world;
    public UUID owner;
    public double x, y, z;
    public float yaw, pitch;
    public List<UUID> invited;

    public HomeData(String world, UUID owner, double x, double y, double z, float yaw, float pitch, List<UUID> invited) {
        this.world = world;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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
        compound.put("Invited", invitedList);

        return compound;
    }

    public static HomeData fromNBT(NbtCompound compound) {
        return new HomeData(
                compound.getString("World"),
                compound.getUuid("Owner"),
                compound.getDouble("X"),
                compound.getDouble("Y"),
                compound.getDouble("Z"),
                compound.getFloat("Yaw"),
                compound.getFloat("Pitch"),
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
