package dev.doctor4t.trainmurdermystery.mixin;

import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.DrawableGet;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Screen.class)
public class ScreenDrawableGetMixin implements DrawableGet {

    @Shadow @Final private List<Drawable> drawables;

    @Override
    public List<Drawable> getDrawable() {
        return this.drawables;
    }
}
