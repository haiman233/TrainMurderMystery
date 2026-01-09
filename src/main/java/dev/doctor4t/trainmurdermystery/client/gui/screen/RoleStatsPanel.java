
package dev.doctor4t.trainmurdermystery.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.PlayerStatsComponent;
import dev.doctor4t.trainmurdermystery.util.ReplayDisplayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class RoleStatsPanel implements Renderable, GuiEventListener, NarratableEntry {
    private final PlayerStatsComponent stats;
    private Role selectedRole;
    private PlayerStatsComponent.RoleStats selectedRoleStats;
    private ScrollableRoleListComponent roleListComponent;
    private RoleDetailsComponent roleDetailsComponent;
    private EditBox searchBox;

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private boolean visible = true;
    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();

    public RoleStatsPanel(int x, int y, int width, int height, PlayerStatsComponent stats) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.stats = stats;
        setupComponents();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void addRenderableWidget(Renderable renderable) {
        if (renderable instanceof GuiEventListener) {
            children.add((GuiEventListener) renderable);
        }
        renderables.add(renderable);
    }

    private void addWidget(GuiEventListener widget) {
        children.add(widget);
        if (widget instanceof Renderable) {
            renderables.add((Renderable) widget);
        }
    }

    private void addComponent(Renderable component) {
        renderables.add(component);
    }

    private void setupComponents() {
        int rightPanelContentX = getX();
        int rightPanelContentWidth = getWidth();
        int currentY = getY();

        titleY = currentY;
        currentY += Minecraft.getInstance().font.lineHeight + 15;

        searchBox = new EditBox(
                Minecraft.getInstance().font,
                rightPanelContentX,
                currentY,
                rightPanelContentWidth,
                20,
                Component.translatable("screen." + TMM.MOD_ID + ".player_stats.search_role")
        );
        searchBox.setHint(Component.translatable("screen." + TMM.MOD_ID + ".player_stats.search_role"));
        searchBox.setMaxLength(50);
        searchBox.setResponder(this::onSearchTextChanged);
        this.addRenderableWidget(searchBox);
        currentY += searchBox.getHeight() + 10;

        int availableHeight = getHeight() - (currentY - getY()) - 15;
        int roleListHeight = Math.max(50, availableHeight / 2);
        roleListComponent = new ScrollableRoleListComponent(
                rightPanelContentX,
                currentY,
                rightPanelContentWidth,
                roleListHeight,
                this::onRoleSelected
        );

        List<Role> roles = new ArrayList<>();
        Map<ResourceLocation, PlayerStatsComponent.RoleStats> roleStatsMap = stats.getRoleStats();
        for (Role role : TMMRoles.ROLES) {
            if (roleStatsMap.containsKey(role.identifier())) {
                roles.add(role);
            }
        }
        roles.sort(Comparator.comparing(role -> role.identifier().getPath()));
        roleListComponent.setRoles(roles, roleStatsMap);
        this.addRenderableWidget(roleListComponent);
        currentY += roleListHeight + 15;

        roleDetailsComponent = new RoleDetailsComponent(
                rightPanelContentX,
                currentY,
                rightPanelContentWidth,
                getHeight() - (currentY - getY())
        );
        this.addRenderableWidget(roleDetailsComponent);

        if (!roles.isEmpty()) {
            onRoleSelected(roles.getFirst(), roleStatsMap.get(roles.getFirst().identifier()));
        }
        searchBox.setFocused(true);
    }

    private int titleY;

    private void onSearchTextChanged(String searchText) {
        roleListComponent.filterRoles(searchText);
    }

    private void onRoleSelected(Role role, PlayerStatsComponent.RoleStats roleStats) {
        selectedRole = role;
        selectedRoleStats = roleStats;
        roleDetailsComponent.setRole(role, roleStats);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        graphics.drawString(Minecraft.getInstance().font,
                Component.translatable("screen." + TMM.MOD_ID + ".player_stats.role_stats").withStyle(style -> style.withBold(true)),
                getX(), titleY, 0xFFFFFFFF);

        for (Renderable renderable : renderables) {
            renderable.render(graphics, mouseX, mouseY, delta);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused() && searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        for (GuiEventListener child : children) {
            if (child != searchBox && child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchBox.isFocused()) {
            return searchBox.charTyped(chr, modifiers);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (GuiEventListener child : children) {
            if (child != searchBox && child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (roleListComponent.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        for (GuiEventListener child : children) {
            if (child != roleListComponent && child.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (roleListComponent.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        for (GuiEventListener child : children) {
            if (child != roleListComponent && child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFocused() {
        return searchBox.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        searchBox.setFocused(focused);
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }

    public Role getSelectedRole() {
        return selectedRole;
    }

    public PlayerStatsComponent.RoleStats getSelectedRoleStats() {
        return selectedRoleStats;
    }

    private static class ScrollableRoleListComponent extends AbstractWidget {
        private final BiConsumer<Role, PlayerStatsComponent.RoleStats> onRoleSelected;
        private List<Role> allRoles;
        private Map<ResourceLocation, PlayerStatsComponent.RoleStats> roleStatsMap;
        private List<Role> filteredRoles;
        private double scrollAmount = 0.0; // Changed from int scrollOffset to double scrollAmount
        private static final int SCROLLBAR_WIDTH = 6;
        private final int itemHeight = 30;
        private Role selectedRole;
        private double initialMouseY = -1;
        private double initialScrollAmount; // Renamed from initialScrollOffset

        public ScrollableRoleListComponent(int x, int y, int width, int height, BiConsumer<Role, PlayerStatsComponent.RoleStats> onRoleSelected) {
            super(x, y, width, height, Component.empty());
            this.onRoleSelected = onRoleSelected;
            this.allRoles = new ArrayList<>();
            this.filteredRoles = new ArrayList<>();
        }

        public void setRoles(List<Role> roles, Map<ResourceLocation, PlayerStatsComponent.RoleStats> roleStatsMap) {
            this.allRoles = roles;
            this.roleStatsMap = roleStatsMap;
            this.filteredRoles = new ArrayList<>(roles);
            if (!filteredRoles.isEmpty()) {
                selectedRole = filteredRoles.getFirst();
            }
        }

        public void filterRoles(String searchText) {
            if (searchText == null || searchText.isEmpty()) {
                filteredRoles = new ArrayList<>(allRoles);
            } else {
                filteredRoles = allRoles.stream()
                        .filter(role -> ReplayDisplayUtils.getRoleDisplayName(role.identifier().toString()).getString().toLowerCase().contains(searchText.toLowerCase()))
                        .toList();
            }
            setScrollAmount(0.0); // Use setScrollAmount
            if (!filteredRoles.isEmpty() && (selectedRole == null || !filteredRoles.contains(selectedRole))) {
                selectedRole = filteredRoles.getFirst();
                onRoleSelected.accept(selectedRole, roleStatsMap.get(selectedRole.identifier()));
            } else if (filteredRoles.isEmpty()) {
                selectedRole = null;
                onRoleSelected.accept(null, null);
            }
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x80000000);
            graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFFAAAAAA);

            int visibleItemCount = getHeight() / itemHeight;
            int startIndex = (int) (getScrollAmount() / itemHeight);
            int endIndex = Math.min(startIndex + visibleItemCount + 1, filteredRoles.size()); // +1 to ensure partial items are rendered

            for (int i = startIndex; i < endIndex; i++) {
                Role role = filteredRoles.get(i);
                PlayerStatsComponent.RoleStats stats = roleStatsMap.get(role.identifier());
                int itemY = getY() + (i * itemHeight) - (int) getScrollAmount();
                boolean isSelected = role.equals(selectedRole);
                graphics.fill(getX(), itemY, getX() + getWidth(), itemY + itemHeight, isSelected ? 0x60FFFFFF : 0x20FFFFFF);

                Component displayName = ReplayDisplayUtils.getRoleDisplayName(role.identifier().toString());
                int nameWidth = Minecraft.getInstance().font.width(displayName);
                int maxNameWidth = getWidth() - 25;
                if (nameWidth > maxNameWidth) {
                    String truncatedText = Minecraft.getInstance().font.plainSubstrByWidth(displayName.getString(), maxNameWidth - 3) + "...";
                    graphics.drawString(Minecraft.getInstance().font, truncatedText, getX() + 5, itemY + 5, role.getColor());
                } else {
                    graphics.drawString(Minecraft.getInstance().font, displayName, getX() + 5, itemY + 5, role.getColor());
                }

                if (stats != null) {
                    String briefStats = String.format("Played: %d, Wins: %d", stats.getTimesPlayed(), stats.getWinsAsRole());
                    int statsWidth = Minecraft.getInstance().font.width(briefStats);
                    int maxStatsWidth = getWidth() - 25;
                    if (statsWidth > maxStatsWidth) {
                        String truncatedStats = Minecraft.getInstance().font.plainSubstrByWidth(briefStats, maxStatsWidth - 3) + "...";
                        graphics.drawString(Minecraft.getInstance().font, truncatedStats, getX() + 5, itemY + 15, 0xFFCCCCCC);
                    } else {
                        graphics.drawString(Minecraft.getInstance().font, briefStats, getX() + 5, itemY + 15, 0xFFCCCCCC);
                    }
                }
            }

            if (scrollbarVisible()) {
                int scrollbarX = getX() + getWidth() - SCROLLBAR_WIDTH;
                int scrollbarHeight = (int) ((float) getHeight() * getHeight() / getMaxPosition());
                scrollbarHeight = Math.max(32, scrollbarHeight); // Minimum scrollbar height
                scrollbarHeight = Math.min(getHeight() - 8, scrollbarHeight); // Maximum scrollbar height

                int scrollbarY = getY() + (int) (getScrollAmount() * (getHeight() - scrollbarHeight) / getMaxScroll());
                if (scrollbarY < getY()) {
                    scrollbarY = getY();
                }

                graphics.fill(scrollbarX, getY(), scrollbarX + SCROLLBAR_WIDTH, getY() + getHeight(), 0x80000000); // Scrollbar background
                graphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollbarHeight, 0xFFAAAAAA); // Scrollbar handle
            }
        }

        private boolean scrollbarVisible() {
            return getMaxScroll() > 0;
        }

        private int getMaxScroll() {
            return Math.max(0, filteredRoles.size() * itemHeight - getHeight());
        }

        private double getScrollAmount() {
            return scrollAmount;
        }

        private void setScrollAmount(double amount) {
            scrollAmount = Math.max(0, Math.min(amount, getMaxScroll()));
        }

        private int getMaxPosition() {
            return filteredRoles.size() * itemHeight;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY)) return false;

            boolean isScrollbarClicked = mouseX >= getX() + getWidth() - SCROLLBAR_WIDTH && mouseX <= getX() + getWidth();
            if (isScrollbarClicked && scrollbarVisible()) {
                initialMouseY = mouseY;
                initialScrollAmount = getScrollAmount();
                return true;
            }

            int relativeY = (int) (mouseY - getY() + getScrollAmount());
            int clickedIndex = relativeY / itemHeight;

            if (clickedIndex >= 0 && clickedIndex < filteredRoles.size()) {
                selectedRole = filteredRoles.get(clickedIndex);
                onRoleSelected.accept(selectedRole, roleStatsMap.get(selectedRole.identifier()));
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            if (!isMouseOver(mouseX, mouseY)) return false;
            setScrollAmount(getScrollAmount() - scrollY * itemHeight / 2.0);
            return true;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (button == 0 && isMouseOver(mouseX, mouseY)) {
                boolean isDraggingScrollbar = mouseX >= getX() + getWidth() - SCROLLBAR_WIDTH && mouseX <= getX() + getWidth();

                if (isDraggingScrollbar && scrollbarVisible()) {
                    int scrollbarHeight = (int) ((float) getHeight() * getHeight() / getMaxPosition());
                    scrollbarHeight = Math.max(32, scrollbarHeight);
                    scrollbarHeight = Math.min(getHeight() - 8, scrollbarHeight);

                    double scrollRange = getHeight() - scrollbarHeight;
                    double scrollPerPixel = (double) getMaxScroll() / scrollRange;

                    double newScrollAmount = initialScrollAmount + (mouseY - initialMouseY) * scrollPerPixel;
                    setScrollAmount(newScrollAmount);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    private static class RoleDetailsComponent extends AbstractWidget {
        private Role role;
        private PlayerStatsComponent.RoleStats roleStats;

        public RoleDetailsComponent(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty());
        }

        public void setRole(Role role, PlayerStatsComponent.RoleStats roleStats) {
            this.role = role;
            this.roleStats = roleStats;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            if (role == null || roleStats == null) {
                graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x80000000);
                graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFFAAAAAA);
                return;
            }

            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x80000000);
            graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFFAAAAAA);

            int currentY = getY() + 10;
            Component roleName = ReplayDisplayUtils.getRoleDisplayName(role.identifier().toString());
            graphics.drawString(Minecraft.getInstance().font,
                    roleName.copy().withStyle(style -> style.withBold(true)),
                    getX() + 5, currentY, role.getColor());
            currentY += Minecraft.getInstance().font.lineHeight + 10;

            drawStatLine(graphics, currentY, "screen." + TMM.MOD_ID + ".player_stats.times_played", roleStats.getTimesPlayed());
            currentY += 10;
            drawStatLine(graphics, currentY, "screen." + TMM.MOD_ID + ".player_stats.wins", roleStats.getWinsAsRole());
            currentY += 10;
            drawStatLine(graphics, currentY, "screen." + TMM.MOD_ID + ".player_stats.kills", roleStats.getKillsAsRole());
            currentY += 10;
            drawStatLine(graphics, currentY, "screen." + TMM.MOD_ID + ".player_stats.deaths", roleStats.getDeathsAsRole());
            currentY += 10;
            
            int gamesPlayed = roleStats.getTimesPlayed();
            int wins = roleStats.getWinsAsRole();
            double winRate = gamesPlayed > 0 ? (double) wins / gamesPlayed * 100 : 0.0;
            drawStatLine(graphics, currentY, "screen." + TMM.MOD_ID + ".player_stats.win_rate", String.format("%.2f%%", winRate));
            currentY += 10;
            
            int kills = roleStats.getKillsAsRole();
            int deaths = roleStats.getDeathsAsRole();
            double kdRatio = deaths > 0 ? (double) kills / deaths : kills;
            drawStatLine(graphics, currentY, "screen." + TMM.MOD_ID + ".player_stats.kd_ratio", String.format("%.2f", kdRatio));
        }

        private void drawStatLine(GuiGraphics graphics, int y, String translationKey, Object value) {
            Component text = Component.translatable(translationKey, value);
            graphics.drawString(Minecraft.getInstance().font, text, getX() + 10, y, 0xFFCCCCCC);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            // 无需 narration
        }
    }
}