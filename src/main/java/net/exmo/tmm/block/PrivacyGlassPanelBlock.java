package net.exmo.tmm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.server.Level.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.Level.Level;

public class PrivacyGlassPanelBlock extends GlassPanelBlock implements PrivacyBlock {

    public PrivacyGlassPanelBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState()
                .with(OPAQUE, false)
                .with(INTERACTION_COOLDOWN, false));
    }

    @Override
    public ActionResult onUse(BlockState state, Level Level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.shouldCancelInteraction() && !player.getMainHandStack().isOf(this.asItem()) && this.canInteract(state, pos, Level, player, Hand.MAIN_HAND)) {
            this.toggle(state, Level, pos);

            return ActionResult.success(Level.isClient);
        }

        return super.onUse(state, Level, pos, player, hit);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld Level, BlockPos pos, Random random) {
        this.toggle(state, Level, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPAQUE, INTERACTION_COOLDOWN);
        super.appendProperties(builder);
    }
}
