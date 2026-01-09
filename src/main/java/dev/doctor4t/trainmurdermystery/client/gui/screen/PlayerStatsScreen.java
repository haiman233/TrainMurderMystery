package dev.doctor4t.trainmurdermystery.client.gui.screen;

import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.cca.PlayerStatsComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PlayerStatsScreen extends Screen {
    private final PlayerStatsComponent stats;
    private GeneralStatsPanel generalStatsPanel;
    private RoleStatsPanel roleStatsPanel;
    private Button generalStatsButton;
    private Button roleStatsButton;

    private static final int GENERAL_STATS_VIEW = 0;
    private static final int ROLE_STATS_VIEW = 1;
    private int currentView = GENERAL_STATS_VIEW;

    public static final @NotNull ResourceLocation ID = TMM.id("textures/gui/game.png");

    public PlayerStatsScreen() {
        super(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.title"));
        this.stats = PlayerStatsComponent.KEY.get(Minecraft.getInstance().player);
    }

    @Override
    protected void init() {
        super.init();
        int screenWidth = this.width;
        int screenHeight = this.height;
        int panelWidth = (int) (screenWidth * 0.6);
        int panelHeight = (int) (screenHeight * 0.7);
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = (screenHeight - panelHeight) / 2;

        // 切换按钮
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonSpacing = 10;
        int totalButtonWidth = (buttonWidth * 2) + buttonSpacing;
        int buttonX = (screenWidth - totalButtonWidth) / 2;
        int buttonY = panelY + 10;

        generalStatsButton = Button.builder(
                Component.translatable("screen." + TMM.MOD_ID + ".player_stats.general_stats_button"),
                (button) -> this.switchView(GENERAL_STATS_VIEW)
        ).pos(buttonX, buttonY).size(buttonWidth, buttonHeight).build();
        this.addRenderableWidget(generalStatsButton);

        roleStatsButton = Button.builder(
                Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_stats_button"),
                (button) -> this.switchView(ROLE_STATS_VIEW)
        ).pos(buttonX + buttonWidth + buttonSpacing, buttonY).size(buttonWidth, buttonHeight).build();
        this.addRenderableWidget(roleStatsButton);

        // 初始化面板
        generalStatsPanel = new GeneralStatsPanel(
                panelX,
                buttonY + buttonHeight + 10,
                panelWidth,
                panelHeight - buttonHeight - 10,
                stats,
                screenWidth,
                screenHeight
        );
        this.addRenderableWidget(generalStatsPanel);

        roleStatsPanel = new RoleStatsPanel(
                panelX,
                buttonY + buttonHeight + 10,
                panelWidth,
                panelHeight - buttonHeight - 10,
                stats
        );
        this.addRenderableWidget(roleStatsPanel);

        // 默认显示通用统计
        switchView(GENERAL_STATS_VIEW);
    }

    private void switchView(int view) {
        this.currentView = view;
        generalStatsPanel.setVisible(view == GENERAL_STATS_VIEW);
        roleStatsPanel.setVisible(view == ROLE_STATS_VIEW);

        generalStatsButton.active = (view != GENERAL_STATS_VIEW);
        roleStatsButton.active = (view != ROLE_STATS_VIEW);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // 渲染背景（半透明黑色）
        graphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        // 渲染标题
        graphics.drawCenteredString(this.font, this.title, this.width / 2, (int)((this.height * 0.3) - 30), 0xFFFFFFFF);

        super.render(graphics, mouseX, mouseY, delta);
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
    public void tick() {
        // 屏幕 tick
    }

    public int getCurrentView() {
        return currentView;
    }
}