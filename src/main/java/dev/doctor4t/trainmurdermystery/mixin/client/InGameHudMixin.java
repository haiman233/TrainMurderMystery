package dev.doctor4t.trainmurdermystery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.voting.MapVotingManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderVotingReminder(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // 检查是否处于游戏中且投票活跃
        if (TMMClient.isPlayerAliveAndInSurvival() && MapVotingManager.getInstance().isVotingActive()) {
            Font font = minecraft.font;
            
            // 获取提示文本
            String keyBindName = dev.doctor4t.trainmurdermystery.client.InputHandler.getOpenVotingScreenKeybind().getTranslatedKeyMessage().getString();
            String hintText = Component.translatable("gui.tmm.hud.vote_reminder", keyBindName).getString();
            
            // 计算文本尺寸和位置
            int textWidth = font.width(hintText);
            int x = 10; // 左侧边距
            int y = 10; // 顶部边距
            
            // 绘制半透明背景
            int bgColor = 0x80000000; // 半透明黑色
            guiGraphics.fill(x - 5, y - 3, x + textWidth + 5, y + font.lineHeight + 3, bgColor);
            
            // 绘制文本
            guiGraphics.drawString(font, hintText, x, y, 0xFFFF00); // 黄色文本
        }
    }
}