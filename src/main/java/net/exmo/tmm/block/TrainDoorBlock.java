package net.exmo.tmm.block;

import dev.doctor4t.trainmurdermystery.block_entity.SmallDoorBlockEntity;
import dev.doctor4t.trainmurdermystery.cca.TrainWorldComponent;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.Player;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.Level.Level;

import java.util.function.Supplier;

public class TrainDoorBlock extends SmallDoorBlock {
    public TrainDoorBlock(Supplier<BlockEntityType<SmallDoorBlockEntity>> typeSupplier, Settings settings) {
        super(typeSupplier, settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, Level Level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        if (Level.getBlockEntity(lowerPos) instanceof SmallDoorBlockEntity entity) {
            if (entity.isBlasted()) {
                return ActionResult.PASS;
            }

            if (player.isCreative() || TrainWorldComponent.KEY.get(Level).getSpeed() == 0) {
                return open(state, Level, entity, lowerPos);
            } else {
                boolean hasLockpick = player.getMainHandStack().isOf(TMMItems.LOCKPICK);

                if (entity.isOpen()) {
                    return open(state, Level, entity, lowerPos);
                } else {
                    if (hasLockpick) {
                        Level.playSound(null, lowerPos.getX() + .5f, lowerPos.getY() + 1, lowerPos.getZ() + .5f, TMMSounds.ITEM_LOCKPICK_DOOR, SoundCategory.BLOCKS, 1f, 1f);
                        return open(state, Level, entity, lowerPos);
                    } else {
                        if (!Level.isClient) {
                            Level.playSound(null, lowerPos.getX() + .5f, lowerPos.getY() + 1, lowerPos.getZ() + .5f, TMMSounds.BLOCK_DOOR_LOCKED, SoundCategory.BLOCKS, 1f, 1f);
                            player.sendMessage(Text.translatable("tip.door.locked"), true);
                        }
                        return ActionResult.FAIL;
                    }
                }
            }
        }

        return ActionResult.PASS;
    }
}
