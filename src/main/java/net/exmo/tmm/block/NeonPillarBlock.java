package net.exmo.tmm.block;

import dev.doctor4t.trainmurdermystery.index.TMMProperties;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
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
import net.minecraft.Level.Level;
import net.minecraft.Level.WorldAccess;

public class NeonPillarBlock extends PillarBlock {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty ACTIVE = TMMProperties.ACTIVE;

    public NeonPillarBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState().with(LIT, false).with(ACTIVE, true));
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
        if (!state.get(ACTIVE)) {
            Level.playSound(player, pos, TMMSounds.BLOCK_BUTTON_TOGGLE_NO_POWER, SoundCategory.BLOCKS, 0.1f, 1f);
        }
        return ActionResult.success(Level.isClient);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess Level, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = state.get(AXIS);
        if (direction.getAxis() == axis && neighborState.isOf(this) && neighborState.get(AXIS) == axis) {
            return state.with(ACTIVE, neighborState.get(ACTIVE));
        }
        return state;
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
