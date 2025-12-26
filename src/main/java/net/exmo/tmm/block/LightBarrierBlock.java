package net.exmo.tmm.block;

import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.Level.BlockView;

public class LightBarrierBlock extends BarrierBlock {
    public LightBarrierBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected int getOpacity(BlockState state, BlockView Level, BlockPos pos) {
        return 15;
    }
}