package dev.doctor4t.trainmurdermystery.event;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerInteractionHandler {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClientSide) {
                GameWorldComponent game = GameWorldComponent.KEY.get(world);

                if (game.isRunning() && !player.isCreative() && !player.isSpectator()) {
                    BlockState state = world.getBlockState(hitResult.getBlockPos());
                    Block block = state.getBlock();

                    if (isVanillaWorkstation(block)) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            return InteractionResult.PASS; // 继续正常处理
        });
    }

    private static boolean isVanillaWorkstation(Block block) {
        return block instanceof CraftingTableBlock ||
               block instanceof FurnaceBlock ||
               block instanceof AnvilBlock ||
               block instanceof EnchantingTableBlock ||
               block instanceof LoomBlock ||
               block instanceof CartographyTableBlock ||
               block instanceof SmithingTableBlock ||
               block instanceof GrindstoneBlock ||
               block instanceof StonecutterBlock ||
               block instanceof BrewingStandBlock;
    }
}
