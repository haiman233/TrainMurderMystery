package net.exmo.tmm.item;

import dev.doctor4t.trainmurdermystery.entity.GrenadeEntity;
import dev.doctor4t.trainmurdermystery.index.TMMEntities;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.Level.Level;
import org.jetbrains.annotations.NotNull;

public class GrenadeItem extends Item {
    public static final int MAX_CHARGE_TIME = 20; // 最大蓄力时间（ticks），对应1秒

    public GrenadeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(@NotNull Level Level, @NotNull Player user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, Level Level, LivingEntity user, int remainingUseTicks) {
        if (!Level.isClient) {
            // 计算蓄力时间
            int chargeTime = this.getMaxUseTime(stack, user) - remainingUseTicks;

            // 确保蓄力时间在合理范围内
            chargeTime = Math.max(0, Math.min(chargeTime, MAX_CHARGE_TIME));

            // 播放投掷声音
            Level.playSound(null, user.getX(), user.getY(), user.getZ(), TMMSounds.ITEM_GRENADE_THROW, SoundCategory.NEUTRAL, 0.5F, 1F + (Level.random.nextFloat() - .5f) / 10f);

            // 创建手榴弹实体
            GrenadeEntity grenade = new GrenadeEntity(TMMEntities.GRENADE, Level);
            grenade.setOwner(user);
            grenade.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());

            // 根据蓄力时间计算投掷速度（最小速度0.3，最大速度1.2）
            float velocity = 0.4F + (0.75F * (float) chargeTime / MAX_CHARGE_TIME);

            // 设置手榴弹的速度和方向
            grenade.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, velocity, 1.0F);
            Level.spawnEntity(grenade);
        }


//		user.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.decrementUnlessCreative(1, user);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}