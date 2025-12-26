package net.exmo.tmm.item;

import dev.doctor4t.trainmurdermystery.block_entity.DoorBlockEntity;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import dev.doctor4t.trainmurdermystery.util.AdventureUsable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.Level.Level;

public class CrowbarItem extends Item implements AdventureUsable {
    public CrowbarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Level Level = context.getWorld();
        BlockEntity entity = Level.getBlockEntity(context.getBlockPos());
        if (!(entity instanceof DoorBlockEntity)) entity = Level.getBlockEntity(context.getBlockPos().down());
        Player player = context.getPlayer();
        if (entity instanceof DoorBlockEntity door && !door.isBlasted() && player != null) {
            if (!player.isCreative()) player.getItemCooldownManager().set(this, 6000);
            Level.playSound(null, context.getBlockPos(), TMMSounds.ITEM_CROWBAR_PRY, SoundCategory.BLOCKS, 2.5f, 1f);
            player.swingHand(Hand.MAIN_HAND, true);

            if (!player.isCreative()) {
                player.getItemCooldownManager().set(this, GameConstants.ITEM_COOLDOWNS.get(this));
            }

            door.blast();
        }
        return super.useOnBlock(context);
    }
}
