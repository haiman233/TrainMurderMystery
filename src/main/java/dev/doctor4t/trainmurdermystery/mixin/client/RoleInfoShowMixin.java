package dev.doctor4t.trainmurdermystery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.trainmurdermystery.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(LimitedInventoryScreen.class)
public class RoleInfoShowMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        final var gameWorldComponent = TMMClient.gameComponent;
        if (gameWorldComponent == null) {
            return;
        }

        final var role = gameWorldComponent.getRole(player);
        if (role == null) return;

        String roleName = role.getIdentifier().getPath();
        if (roleName == null) return;

        // 获取字体渲染器
        Font font = Minecraft.getInstance().font;

        // 左上角位置
        int x = 10;
        int y = 10;

        // 缩放因子（二分之一大小）
        float scale = 0.8f;

        // 创建文本组件
        Component roleNameComponent = Component.translatable("announcement.role." + roleName)
                .withStyle(ChatFormatting.BOLD);
        Component roleInfoComponent = Component.translatable("info.screen.roleid." + roleName);

        // 保存当前的变换矩阵
        PoseStack poseStack = context.pose();
        poseStack.pushPose();

        // 应用缩放
        poseStack.scale(scale, scale, 1.0f);

        // 计算缩放后的坐标
        float scaledX = x / scale;
        float scaledY = y / scale;

        // 绘制职业名称（带阴影）
        renderScaledTextWithShadow(context, font, roleNameComponent, scaledX, scaledY, scale, 0xFFFFFF, 0x404040);

        // 计算职业名称的高度（考虑缩放）
        int roleNameHeight = (int) (font.lineHeight * scale);
        int currentY = y + roleNameHeight + 2;

        // 绘制职业信息（多行）
        java.util.List<Component> infoLines = TooltipUtil.sprit(roleInfoComponent);
        for (Component line : infoLines) {
            // 计算每行的缩放后位置
            float lineY = (currentY / scale);
            context.drawString(font, line, (int) scaledX, (int) lineY, 0xAAAAAA);
            currentY += (int) (font.lineHeight * scale) + 2;
        }

        // 恢复变换矩阵
        poseStack.popPose();

        // 计算背景框尺寸（使用缩放后的实际尺寸）
        int infoLineCount = infoLines.size();
        int totalHeight = (int) (font.lineHeight * scale) + // 职业名称高度
                (infoLineCount * (int) (font.lineHeight * scale)) + // 职业信息总高度
                (infoLineCount * 2) + // 行间距
                2; // 名称和信息之间的间距

        // 计算最大宽度（考虑缩放）
        int scaledNameWidth = (int) (font.width(roleNameComponent) * scale);
        int maxInfoWidth = infoLines.stream()
                .mapToInt(component -> (int) (font.width(component) * scale))
                .max()
                .orElse(0);
        int maxWidth = Math.max(scaledNameWidth, maxInfoWidth);

        // 绘制背景
        drawScaledBackground(context, x, y, maxWidth, totalHeight);
    }

    /**
     * 绘制带阴影的缩放文本
     */
    private void renderScaledTextWithShadow(GuiGraphics context, Font font, Component text,
                                            float x, float y, float scale, int textColor, int shadowColor) {
        PoseStack poseStack = context.pose();

        // 保存当前变换
        poseStack.pushPose();

        // 绘制阴影
        context.drawString(font, text, (int) (x + 1 / scale), (int) (y + 1 / scale), shadowColor);

        // 绘制主文本
        context.drawString(font, text, (int) x, (int) y, textColor);

        // 恢复变换
        poseStack.popPose();
    }

    /**
     * 绘制缩放后的背景
     */
    private void drawScaledBackground(GuiGraphics context, int x, int y, int width, int height) {
        int padding = 3; // 内边距
        int borderThickness = 1; // 边框厚度

        // 背景框位置和大小
        int bgX = x - padding;
        int bgY = y - padding;
        int bgWidth = width + padding * 2;
        int bgHeight = height + padding * 2;

        // 绘制半透明背景
        int backgroundColor = 0x80000000; // 50%透明度黑色
        context.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, backgroundColor);

        // 绘制边框（更细的边框适合小尺寸）
        int borderColor = 0xFF666666; // 灰色边框

        // 上边框
        context.fill(bgX, bgY, bgX + bgWidth, bgY + borderThickness, borderColor);
        // 下边框
        context.fill(bgX, bgY + bgHeight - borderThickness, bgX + bgWidth, bgY + bgHeight, borderColor);
        // 左边框
        context.fill(bgX, bgY, bgX + borderThickness, bgY + bgHeight, borderColor);
        // 右边框
        context.fill(bgX + bgWidth - borderThickness, bgY, bgX + bgWidth, bgY + bgHeight, borderColor);
    }

    /**
     * 可选：更简洁的背景样式（无边框）
     */
    private void drawSimpleBackground(GuiGraphics context, int x, int y, int width, int height) {
        int padding = 2; // 更小的内边距
        int cornerRadius = 2; // 圆角半径

        int bgX = x - padding;
        int bgY = y - padding;
        int bgWidth = width + padding * 2;
        int bgHeight = height + padding * 2;

        // 绘制圆角矩形背景（需要稍微复杂一点的绘制）
        int color = 0x60000000; // 37.5%透明度黑色

        // 由于Minecraft没有直接绘制圆角矩形的方法，我们绘制一个简单的矩形
        context.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, color);

        // 可以添加轻微的渐变效果
        int topColor = 0x70000000;
        int bottomColor = 0x40000000;
        context.fillGradient(bgX, bgY, bgX + bgWidth, bgY + bgHeight, topColor, bottomColor);
    }

    /**
     * 可选：极简主义背景
     */
    private void drawMinimalBackground(GuiGraphics context, int x, int y, int width, int height) {
        // 只在文本下方添加轻微的背景
        int bgHeight = height;
        int bgWidth = width;

        // 轻微的半透明背景，不遮挡太多内容
        int color = 0x30000000; // 18.75%透明度

        context.fill(x, y, x + bgWidth, y + bgHeight, color);

        // 添加一条细线作为装饰
        int lineColor = 0x80FFFFFF; // 半透明白色
        int lineY = y + bgHeight - 1;
        context.fill(x, lineY, x + bgWidth, lineY + 1, lineColor);
    }
}