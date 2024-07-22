package com.enthusiasm.plureutils.data.spawn;

import net.minecraft.nbt.NbtCompound;

public class SpawnData {
    public String world;
    public double x, y, z;
    public float yaw, pitch;

    public SpawnData(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public NbtCompound toNBT() {
        NbtCompound tag = new NbtCompound();

        tag.putString("world", this.world);
        tag.putDouble("x", this.x);
        tag.putDouble("y", this.y);
        tag.putDouble("z", this.z);
        tag.putFloat("yaw", this.yaw);
        tag.putFloat("pitch", this.pitch);

        return tag;
    }

    public static SpawnData fromNBT(NbtCompound tag) {
        return new SpawnData(
                tag.getString("world"),
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z"),
                tag.getFloat("yaw"),
                tag.getFloat("pitch")
        );
    }
}
