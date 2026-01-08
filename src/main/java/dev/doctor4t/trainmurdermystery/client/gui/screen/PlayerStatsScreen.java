package dev.doctor4t.trainmurdermystery.client.gui.screen;

import com.daqem.uilib.client.gui.AbstractScreen;
import com.daqem.uilib.client.gui.component.AbstractComponent;
import com.daqem.uilib.client.gui.component.TextComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.PlayerStatsComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.ui.ComponentFactory;
import dev.doctor4t.trainmurdermystery.ui.components.LinearLayoutComponent;
import dev.doctor4t.trainmurdermystery.ui.util.UIStyleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerStatsScreen extends AbstractScreen {
    private final PlayerStatsComponent stats;

    public static final @NotNull ResourceLocation ID = TMM.id("textures/gui/game.png");

    public PlayerStatsScreen() {
        super(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.title"));
        this.stats = PlayerStatsComponent.KEY.get(Minecraft.getInstance().player);
    }

    @Override
    public void startScreen() {
        this.setPauseScreen(true);
        this.setBackground(ComponentFactory.createFrostedBackground(this.getWidth(), this.getHeight()));

        // 获取玩家皮肤纹理
        ResourceLocation skinTexture = getPlayerSkinTexture();
        int headSize = 32;

        // 当前Y坐标，从顶部开始
        int currentY = 40;
        int contentWidth = 300;
        int screenCenterX = this.getWidth() / 2;

        // 1. 标题
        TextComponent titleComponent = new TextComponent(
                this.font,
                this.title.copy().withStyle(style -> style.withBold(true).withColor(UIStyleHelper.TEXT_COLOR_TITLE))
        );
        titleComponent.centerHorizontally();
        titleComponent.setY(currentY);
        this.addComponent(titleComponent);
        currentY += titleComponent.getHeight() + 20; // 标题后留较大间距

        // 2. 玩家头部和名称
        if (skinTexture != null) {
            // 计算头部和名称的总宽度，以便居中
            int headAndNameWidth = headSize;
            if (Minecraft.getInstance().player != null) {
                String playerName = Minecraft.getInstance().player.getDisplayName().getString();
                headAndNameWidth += this.font.width(playerName) + 12; // 12像素间距
            }
            
            // 头部
            PlayerHeadComponent headComponent = new PlayerHeadComponent(
                screenCenterX - headAndNameWidth / 2,
                currentY,
                headSize,
                skinTexture
            );
            this.addComponent(headComponent);
            
            // 玩家名称
            if (Minecraft.getInstance().player != null) {
                TextComponent playerNameComponent = new TextComponent(
                        this.font,
                        Minecraft.getInstance().player.getDisplayName().copy().withStyle(style -> style.withColor(0xFFFFA0).withBold(true))
                );
                playerNameComponent.setX(headComponent.getX() + headSize + 12);
                playerNameComponent.setY(currentY + (headSize - playerNameComponent.getHeight()) / 2);
                this.addComponent(playerNameComponent);
            }
            currentY += headSize + 20; // 头部高度加间距
        } else {
            // 如果没有皮肤纹理，只显示名称
            if (Minecraft.getInstance().player != null) {
                TextComponent playerNameComponent = new TextComponent(
                        this.font,
                        Minecraft.getInstance().player.getDisplayName().copy().withStyle(style -> style.withColor(0xFFFFA0).withBold(true))
                );
                playerNameComponent.centerHorizontally();
                playerNameComponent.setY(currentY);
                this.addComponent(playerNameComponent);
                currentY += playerNameComponent.getHeight() + 20;
            }
        }

        // 3. 通用统计数据标题
        TextComponent generalStatsTitle = ComponentFactory.createSectionTitle("screen." + TMM.MOD_ID + ".player_stats.general_stats");
        generalStatsTitle.centerHorizontally();
        generalStatsTitle.setY(currentY);
        this.addComponent(generalStatsTitle);
        currentY += generalStatsTitle.getHeight() + 12;
        
        // 4. 通用统计数据（两列布局）
        int columnWidth = contentWidth / 2 - 10;
        int leftColumnX = screenCenterX - contentWidth / 2;
        int rightColumnX = screenCenterX + 10; // 左列右边+20像素间距
        
        // 左列数据
        int columnStartY = currentY;
        
        // 游戏时间
        TextComponent playTimeLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.total_play_time", formatPlayTime(stats.getTotalPlayTime())));
        playTimeLabel.setX(leftColumnX);
        playTimeLabel.setY(columnStartY);
        this.addComponent(playTimeLabel);
        columnStartY += playTimeLabel.getHeight() + 8;
        
        // 游戏次数
        TextComponent gamesPlayedLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.total_games_played", stats.getTotalGamesPlayed()));
        gamesPlayedLabel.setX(leftColumnX);
        gamesPlayedLabel.setY(columnStartY);
        this.addComponent(gamesPlayedLabel);
        columnStartY += gamesPlayedLabel.getHeight() + 8;
        
        // 击杀
        TextComponent killsLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.total_kills", stats.getTotalKills()));
        killsLabel.setX(leftColumnX);
        killsLabel.setY(columnStartY);
        this.addComponent(killsLabel);
        columnStartY += killsLabel.getHeight() + 8;
        
        // 死亡
        TextComponent deathsLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.total_deaths", stats.getTotalDeaths()));
        deathsLabel.setX(leftColumnX);
        deathsLabel.setY(columnStartY);
        this.addComponent(deathsLabel);
        columnStartY += deathsLabel.getHeight() + 8;
        
        // 右列数据
        columnStartY = currentY;
        
        // 胜利
        TextComponent winsLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.total_wins", stats.getTotalWins()));
        winsLabel.setX(rightColumnX);
        winsLabel.setY(columnStartY);
        this.addComponent(winsLabel);
        columnStartY += winsLabel.getHeight() + 8;
        
        // 失败
        TextComponent lossesLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.total_losses", stats.getTotalLosses()));
        lossesLabel.setX(rightColumnX);
        lossesLabel.setY(columnStartY);
        this.addComponent(lossesLabel);
        columnStartY += lossesLabel.getHeight() + 8;
        
        // KD比
        TextComponent kdRatioLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.kd_ratio", String.format("%.2f", getKdRatio(stats.getTotalKills(), stats.getTotalDeaths()))));
        kdRatioLabel.setX(rightColumnX);
        kdRatioLabel.setY(columnStartY);
        this.addComponent(kdRatioLabel);
        columnStartY += kdRatioLabel.getHeight() + 8;
        
        // 胜率
        TextComponent winRateLabel = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.win_rate", String.format("%.2f%%", getWinRate(stats.getTotalWins(), stats.getTotalGamesPlayed()))));
        winRateLabel.setX(rightColumnX);
        winRateLabel.setY(columnStartY);
        this.addComponent(winRateLabel);
        columnStartY += winRateLabel.getHeight() + 8;
        
        // 更新当前Y坐标为两列中较高的那个
        // 实际上我们需要计算两列的最大高度
        // 简化处理，使用右列的Y坐标
        currentY = columnStartY + 20; // 额外间距
        
        // 5. 角色特定统计数据
        if (!stats.getRoleStats().isEmpty()) {
            // 角色统计数据标题
            TextComponent roleStatsTitle = ComponentFactory.createSectionTitle("screen." + TMM.MOD_ID + ".player_stats.role_stats");
            roleStatsTitle.centerHorizontally();
            roleStatsTitle.setY(currentY);
            this.addComponent(roleStatsTitle);
            currentY += roleStatsTitle.getHeight() + 12;
            
            // 使用数组包装currentY以在lambda中修改
            final int[] currentYWrapper = {currentY};
            
            // 角色统计数据
            stats.getRoleStats().entrySet().stream()
                    .sorted(Comparator.comparing(entry -> {
                        Role role = getRoleById(entry.getKey());
                        return role != null ? role.identifier().getPath() : "";
                    }))
                    .forEach(entry -> {
                        Role role = getRoleById(entry.getKey());
                        if (role == null) return;
                        PlayerStatsComponent.RoleStats roleStats = entry.getValue();
                        
                        // 角色名称
                        TextComponent roleNameLabel = new TextComponent(
                                this.font,
                                Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_name", role.identifier().getPath())
                                        .copy().withStyle(style -> style.withColor(role.getColor()))
                        );
                        roleNameLabel.setX(leftColumnX);
                        roleNameLabel.setY(currentYWrapper[0]);
                        this.addComponent(roleNameLabel);
                        currentYWrapper[0] += roleNameLabel.getHeight() + 8;
                        
                        // 角色统计数据
                        TextComponent roleTimesPlayed = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_times_played", roleStats.getTimesPlayed()));
                        roleTimesPlayed.setX(leftColumnX + 10); // 缩进
                        roleTimesPlayed.setY(currentYWrapper[0]);
                        this.addComponent(roleTimesPlayed);
                        currentYWrapper[0] += roleTimesPlayed.getHeight() + 4;
                        
                        TextComponent roleWins = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_wins", roleStats.getWinsAsRole()));
                        roleWins.setX(leftColumnX + 10);
                        roleWins.setY(currentYWrapper[0]);
                        this.addComponent(roleWins);
                        currentYWrapper[0] += roleWins.getHeight() + 4;
                        
                        TextComponent roleLosses = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_losses", roleStats.getLossesAsRole()));
                        roleLosses.setX(leftColumnX + 10);
                        roleLosses.setY(currentYWrapper[0]);
                        this.addComponent(roleLosses);
                        currentYWrapper[0] += roleLosses.getHeight() + 4;
                        
                        TextComponent roleKdRatio = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_kd_ratio", String.format("%.2f", getKdRatio(roleStats.getKillsAsRole(), roleStats.getDeathsAsRole()))));
                        roleKdRatio.setX(leftColumnX + 10);
                        roleKdRatio.setY(currentYWrapper[0]);
                        this.addComponent(roleKdRatio);
                        currentYWrapper[0] += roleKdRatio.getHeight() + 4;
                        
                        TextComponent roleWinRate = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_win_rate", String.format("%.2f%%", getWinRate(roleStats.getWinsAsRole(), roleStats.getTimesPlayed()))));
                        roleWinRate.setX(leftColumnX + 10);
                        roleWinRate.setY(currentYWrapper[0]);
                        this.addComponent(roleWinRate);
                        currentYWrapper[0] += roleWinRate.getHeight() + 12; // 角色间较大间距
                    });
            
            // 更新外部currentY
            currentY = currentYWrapper[0];
        } else {
            TextComponent noRoleStats = createLabel(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.no_role_stats"));
            noRoleStats.centerHorizontally();
            noRoleStats.setY(currentY);
            this.addComponent(noRoleStats);
            currentY += noRoleStats.getHeight() + 20;
        }
        
        // 6. 添加底部贴图
        int bottomTextureHeight = 64;
        BottomTextureComponent bottomTexture = new BottomTextureComponent(
            screenCenterX - 128, // 居中，贴图宽度256
            this.getHeight() - bottomTextureHeight - 10,
            256,
            bottomTextureHeight
        );
        this.addComponent(bottomTexture);
    }

    private Role getRoleById(ResourceLocation id) {
        for (Role role : TMMRoles.ROLES) {
            if (role.identifier().equals(id)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 获取当前玩家的皮肤纹理
     */
    private ResourceLocation getPlayerSkinTexture() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return null;
        }
        PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
        if (playerInfo == null) {
            return null;
        }
        return playerInfo.getSkin().texture();
    }

    private TextComponent createLabel(Component text) {
        return new TextComponent(
                this.font,
                text.copy().withStyle(style -> style.withColor(UIStyleHelper.TEXT_COLOR_LABEL))
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == Minecraft.getInstance().options.keyInventory.getDefaultKey().getValue() || keyCode == TMMClient.statsKeybind.getDefaultKey().getValue()) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public void onTickScreen(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // 可以在此处更新动态数据，但统计数据是静态的，无需每tick更新
        // 无需调用 super，因为 AbstractScreen 没有提供默认实现
    }

    private String formatPlayTime(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return Component.translatable("screen." + TMM.MOD_ID + ".player_stats.time.days_hours_minutes", days, hours % 24, minutes % 60).getString();
        } else if (hours > 0) {
            return Component.translatable("screen." + TMM.MOD_ID + ".player_stats.time.hours_minutes", hours, minutes % 60).getString();
        } else if (minutes > 0) {
            return Component.translatable("screen." + TMM.MOD_ID + ".player_stats.time.minutes_seconds", minutes, seconds % 60).getString();
        } else {
            return Component.translatable("screen." + TMM.MOD_ID + ".player_stats.time.seconds", seconds).getString();
        }
    }

    private double getKdRatio(int kills, int deaths) {
        if (deaths == 0) {
            return kills;
        }
        return (double) kills / deaths;
    }

    private double getWinRate(int wins, int gamesPlayed) {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return (double) wins / gamesPlayed * 100.0;
    }

    /**
     * 渲染玩家头部的自定义组件
     */
    private static class PlayerHeadComponent extends AbstractComponent<PlayerHeadComponent> {
        private final ResourceLocation skinTexture;
        private final int size;

        public PlayerHeadComponent(int x, int y, int size, ResourceLocation skinTexture) {
            super(null, x, y, size, size);
            this.skinTexture = skinTexture;
            this.size = size;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            if (skinTexture == null) return;

            RenderSystem.enableBlend();
            graphics.pose().pushPose();
            graphics.pose().translate(getX(), getY(), 0);
            // 渲染头部（8x8 纹理区域，位于 8,8 到 16,16）
            graphics.blit(skinTexture, 0, 0, size, size, 8, 8, 8, 8, 64, 64);
            // 渲染头盔层（40,8 到 48,16）
            graphics.blit(skinTexture, 0, 0, size, size, 40, 8, 8, 8, 64, 64);
            graphics.pose().popPose();
            RenderSystem.disableBlend();
        }
    }
    
    /**
     * 渲染底部贴图的自定义组件
     */
    private static class BottomTextureComponent extends AbstractComponent<BottomTextureComponent> {
        private static final ResourceLocation TEXTURE = ID; // 使用预定义的贴图
        public BottomTextureComponent(int x, int y, int width, int height) {
            super(null, x, y, width, height);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            if (TEXTURE == null) return; // Safety check

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().pushPose();
            // 将原点移动到组件中心
            graphics.pose().translate(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0);
            // 缩放贴图以适应组件尺寸
            // 假设原始贴图尺寸为 497x254
            int textureOriginalWidth = 497;
            int textureOriginalHeight = 254;
            float scaleX = getWidth() / (float)textureOriginalWidth;
            float scaleY = getHeight() / (float)textureOriginalHeight;
            graphics.pose().scale(scaleX, scaleY, 1f);
            // 渲染整个贴图（497x254），现在使用相对于中心的坐标
            // 使用 blit 方法绘制整个纹理，它将根据 PoseStack 的缩放自动调整
            graphics.blit(TEXTURE,
                    -textureOriginalWidth / 2, // x: 相对中心绘制
                    -textureOriginalHeight / 2, // y: 相对中心绘制
                    textureOriginalWidth, // 目标宽度 (在缩放后会变为组件宽度)
                    textureOriginalHeight, // 目标高度 (在缩放后会变为组件高度)
                    0, 0, // u, v: 纹理的起始像素坐标 (0,0 表示左上角)
                    textureOriginalWidth, textureOriginalHeight, // uWidth, vHeight: 纹理源区域的宽度和高度
                    textureOriginalWidth, textureOriginalHeight // textureWidth, textureHeight: 整个纹理的宽度和高度
            );
            graphics.pose().popPose();
            RenderSystem.disableBlend();
        }
    }
}