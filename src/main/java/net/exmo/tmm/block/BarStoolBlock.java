package net.exmo.tmm.block;


import net.minecraft.Level.phys.shapes.VoxelShape;

public class BarStoolBlock extends MountableBlock {
    private static final Vec3 SIT_POS = new Vec3(0.5f, -0.2f, 0.5f);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(6, 0, 6, 10, 1, 10),
            Block.createCuboidShape(7, 1, 7, 9, 9, 9),
            Block.createCuboidShape(4, 4, 4, 12, 5, 12),
            Block.createCuboidShape(3, 9, 3, 13, 12, 13)
    );



    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView Level, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public Vec3 getSitPos(Level Level, BlockState state, BlockPos pos) {
        return SIT_POS;
    }
}
