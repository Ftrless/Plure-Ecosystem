package com.enthusiasm.plurelogger.helper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VectorHelper {
    public static Vec3d getRoundedPos(Vec3d pos) {
        return new Vec3d(
                Math.round(pos.x),
                Math.round(pos.y),
                Math.round(pos.z)
        );
    }

    public static Vec3d toVec(BlockPos blockPos) {
        return new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
