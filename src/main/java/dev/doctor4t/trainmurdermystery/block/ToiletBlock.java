package dev.doctor4t.trainmurdermystery.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ToiletBlock extends CouchBlock {
    public ToiletBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Vec3d getNorthFacingSitPos(World world, BlockState state, BlockPos pos) {
        return new Vec3d(0.5f, 1, 0.6f);
    }
}