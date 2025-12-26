package net.exmo.tmm.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.Level.Level;
import org.jetbrains.annotations.Nullable;

public class CabinetBlock extends HorizontalFacingBlock implements CrosshairEnabling {

    public static final BooleanProperty OPEN = Properties.OPEN;

    public CabinetBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OPEN, false));
    }

    @Override
    public boolean shouldShowCrosshair(Level Level, BlockState state, BlockHitResult hit) {
        return state.get(OPEN) && hit.getSide() == state.get(FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onStateReplaced(BlockState state, Level Level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (Level.getBlockEntity(pos) instanceof Inventory inventory) {
                ItemScatterer.spawn(Level, pos, inventory);
            }

            super.onStateReplaced(state, Level, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }
}
