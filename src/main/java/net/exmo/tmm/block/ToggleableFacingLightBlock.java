package net.exmo.tmm.block;

import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.Level.Level;

public abstract class ToggleableFacingLightBlock extends FacingLightBlock {
    public static final BooleanProperty LIT = Properties.LIT;

    public ToggleableFacingLightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState()
                .with(LIT, false));
    }

    @Override
    public ActionResult onUse(BlockState state, Level Level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.shouldCancelInteraction()) {
            boolean lit = state.get(LIT);
            Level.setBlockState(pos, state.with(LIT, !lit), Block.NOTIFY_ALL);
            Level.playSound(null, pos, TMMSounds.BLOCK_LIGHT_TOGGLE, SoundCategory.BLOCKS, 0.5f, lit ? 1f : 1.2f);
            if (!state.get(ACTIVE)) {
                Level.playSound(player, pos, TMMSounds.BLOCK_BUTTON_TOGGLE_NO_POWER, SoundCategory.BLOCKS, 0.1f, 1f);
            }
            return ActionResult.success(Level.isClient);
        }
        return super.onUse(state, Level, pos, player, hit);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        super.appendProperties(builder);
    }
}
