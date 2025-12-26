package net.exmo.tmm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.Level.BlockView;
import net.minecraft.Level.Level;

public class OttomanBlock extends HorizontalFacingMountableBlock {
    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);

    public OttomanBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView Level, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public Vec3 getNorthFacingSitPos(Level Level, BlockState state, BlockPos pos) {
        return new Vec3(0.5f, -0.5f, 0.5f);
    }
}
