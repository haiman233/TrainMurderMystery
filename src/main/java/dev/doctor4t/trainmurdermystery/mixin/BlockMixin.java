package dev.doctor4t.trainmurdermystery.mixin;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class BlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient) {
            GameWorldComponent game = GameWorldComponent.KEY.get(world);
            if (game.isRunning()) {
                Block block = state.getBlock();

                if (block instanceof CraftingTableBlock) {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            }
        }
    }
}