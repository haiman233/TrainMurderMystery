package net.exmo.tmm.block;

import dev.doctor4t.trainmurdermystery.index.TMMProperties;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.Level.BlockView;
import net.minecraft.Level.Level;
import net.minecraft.Level.WorldAccess;

public class NeonTubeBlock extends BarBlock {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty ACTIVE = TMMProperties.ACTIVE;

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0, 4, 4, 16, 12, 12);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(4, 0, 4, 12, 16, 12);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(4, 4, 0, 12, 12, 16);

    public NeonTubeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState().with(LIT, false).with(ACTIVE, true));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView Level, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess Level, BlockPos pos, BlockPos neighborPos) {
        BlockState updatedState = super.getStateForNeighborUpdate(state, direction, neighborState, Level, pos, neighborPos);
        if (updatedState == null) {
            return null;
        }
        Direction.Axis axis = state.get(AXIS);
        if (direction.getAxis() == axis && neighborState.isOf(this) && neighborState.get(AXIS) == axis) {
            return updatedState.with(ACTIVE, neighborState.get(ACTIVE));
        }
        return updatedState;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, Level Level, BlockPos pos, Player player, Hand hand, BlockHitResult hit) {
        if (!stack.isEmpty()) {
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.onUseWithItem(stack, state, Level, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, Level Level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        boolean lit = state.get(LIT);
        boolean active = state.get(ACTIVE);
        Direction.Axis axis = state.get(AXIS);
        Direction direction = switch (axis) {
            case X -> Direction.EAST;
            case Y -> Direction.UP;
            case Z -> Direction.SOUTH;
        };
        BlockPos.Mutable mutable = pos.mutableCopy();
        while (this.toggle(Level, mutable, axis, lit)) {
            mutable.move(direction);
        }
        mutable.set(pos).move(direction.getOpposite());
        while (this.toggle(Level, mutable, axis, lit)) {
            mutable.move(direction.getOpposite());
        }
        Level.playSound(null, pos, TMMSounds.BLOCK_LIGHT_TOGGLE, SoundCategory.BLOCKS, 0.5f, lit ? 1f : 1.2f);
        if (!active) {
            Level.playSound(player, pos, TMMSounds.BLOCK_BUTTON_TOGGLE_NO_POWER, SoundCategory.BLOCKS, 0.1f, 1f);
        }
        return ActionResult.success(Level.isClient);
    }

    private boolean toggle(Level Level, BlockPos pos, Direction.Axis axis, boolean lit) {
        BlockState state = Level.getBlockState(pos);
        if (state.isOf(this) && state.get(AXIS) == axis && state.get(LIT) == lit) {
            Level.setBlockState(pos, state.with(LIT, !lit));
            return true;
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT, ACTIVE);
    }
}
