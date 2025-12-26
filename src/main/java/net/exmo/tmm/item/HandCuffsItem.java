package net.exmo.tmm.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class HandCuffsItem extends Item {

    public HandCuffsItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, Player user, LivingEntity entity, Hand hand) {

        return super.useOnEntity(stack, user, entity, hand);
    }
}
