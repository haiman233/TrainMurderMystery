package dev.doctor4t.trainmurdermystery.mixin;


import dev.doctor4t.trainmurdermystery.cca.PlayerAFKComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntiityMixin {

    @Inject(
            method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;vibrationAndSoundEffectsFromBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZZLnet/minecraft/world/phys/Vec3;)Z",ordinal = 0)
    )
    public void moving(MoverType p_19973_, Vec3 p_19974_, CallbackInfo ci){
        Entity self = (Entity) (Object)this;
        if (self instanceof ServerPlayer serverPlayer){
            // 更新该玩家的最后移动时间
                PlayerAFKComponent.KEY.maybeGet(serverPlayer).ifPresent(PlayerAFKComponent::updateActivity);

        }
    }
}