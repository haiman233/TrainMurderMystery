package net.exmo.tmm.item;


public class BodyBagItem extends Item {
    public BodyBagItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, Player user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerBodyEntity body) {
            body.discard();
            if (!user.getWorld().isClient) {
                user.getWorld().playSound(null, body.getX(), body.getY() + .1f, body.getZ(), SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS, 0.5f, 1f + user.getWorld().random.nextFloat() * .1f - .05f);
            }
            if (!user.isCreative()) {
                user.getStackInHand(hand).decrement(1);
                user.getItemCooldownManager().set(this, GameConstants.ITEM_COOLDOWNS.get(this));
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
