package net.exmo.tmm.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3;
import net.minecraft.Level.Level;

public class ToiletBlock extends CouchBlock {
    public ToiletBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Vec3 getNorthFacingSitPos(Level Level, BlockState state, BlockPos pos) {
        return new Vec3(0.5f, 1, 0.6f);
    }
}