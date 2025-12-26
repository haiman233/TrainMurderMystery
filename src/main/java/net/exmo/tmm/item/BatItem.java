package net.exmo.tmm.item;


import net.minecraft.network.chat.Component;
import net.minecraft.Level.InteractionHand;
import net.minecraft.Level.InteractionResultHolder;
import net.minecraft.Level.entity.player.Player;
import net.minecraft.Level.item.Item;
import net.minecraft.Level.item.ItemStack;
import net.minecraft.Level.item.TooltipFlag;
import net.minecraft.Level.level.Level;

import java.util.List;

public class BatItem extends Item {
    public BatItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, TooltipContext p_339594_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_339594_, p_41423_, p_41424_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level Level, Player user, InteractionHand p_41434_) {
        if (user.isCreative()) {
            PlayerPsychoComponent playerPsychoComponent = PlayerPsychoComponent.KEY.get(user);
            if (playerPsychoComponent.getPsychoTicks() > 0) {
                playerPsychoComponent.stopPsycho();
            } else {
                playerPsychoComponent.startPsycho();
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }

        return super.use(Level, user, hand);
    }


}
