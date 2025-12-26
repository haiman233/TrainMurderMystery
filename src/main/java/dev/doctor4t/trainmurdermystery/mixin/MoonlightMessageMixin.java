package dev.doctor4t.trainmurdermystery.mixin;

import net.mehvahdjukaar.moonlight.api.misc.WorldSavedData;
import net.mehvahdjukaar.moonlight.api.misc.WorldSavedDataType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldSavedDataType.class)
public abstract class MoonlightMessageMixin<D extends WorldSavedData>

{
//    @Inject(method = "isSyncable", at = @At("HEAD"), cancellable = true)
//    public void isSyncable(CallbackInfoReturnable<Boolean> cir) {
//            cir.setReturnValue(false);
//
//    }
}
