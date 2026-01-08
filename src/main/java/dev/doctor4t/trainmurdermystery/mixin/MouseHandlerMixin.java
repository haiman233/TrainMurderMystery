package dev.doctor4t.trainmurdermystery.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.doctor4t.trainmurdermystery.block.SecurityMonitorBlock;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
//    @WrapWithCondition(method = "turnPlayer",
//            at = @At(target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V", value = "INVOKE")
//    )
//    public boolean TMM$turnPlayer(LocalPlayer instance, double yRot, double xRot) {
//        return !SecurityMonitorBlock.onPlayerRotated(yRot, xRot);
//    }
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    public void TMM$turnPlayer(double d, CallbackInfo ci) {
        if (SecurityMonitorBlock.isInSecurityMode()) {
            ci.cancel();
        }
    }
}
