package dev.doctor4t.trainmurdermystery.api;

import dev.doctor4t.trainmurdermystery.cca.AbilityPlayerComponent;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedInventoryScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import dev.doctor4t.trainmurdermystery.util.ShopEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

public abstract class Role {
    private ResourceLocation identifier;

    public Role setColor(int color) {
        this.color = color;
        return this;
    }

    public Role setIdentifier(ResourceLocation identifier) {
        this.identifier = identifier;
        return this;
    }

    public Role setInnocent(boolean innocent) {
        isInnocent = innocent;
        return this;
    }

    public Role setCanUseKiller(boolean canUseKiller) {
        this.canUseKiller = canUseKiller;
        return this;
    }

    public Role setMoodType(MoodType moodType) {
        this.moodType = moodType;
        return this;
    }

    public Role setMaxSprintTime(int maxSprintTime) {
        this.maxSprintTime = maxSprintTime;
        return this;
    }

    public Role setCanSeeTime(boolean canSeeTime) {
        this.canSeeTime = canSeeTime;
        return this;
    }

    private int color;
    private boolean isInnocent;
    private boolean canUseKiller;
    private MoodType moodType;

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public int getColor() {
        return color;
    }

    public boolean isCanUseKiller() {
        return canUseKiller;
    }

    public boolean isCanSeeTime() {
        return canSeeTime;
    }

    public Role setAddChild(Consumer<LimitedInventoryScreen> addChild) {
        this.addChild = addChild;
        return this;
    }

    public void addChild(LimitedInventoryScreen screen) {
        if (addChild != null) {
            addChild.accept(screen);
        }
    }

    public boolean onDeath(Player victim, boolean spawnBody, @Nullable Player killer, ResourceLocation deathReason) {
        return true;
    }

    public boolean onKill(Player victim, boolean spawnBody, @Nullable Player killer, ResourceLocation deathReason) {
        return true;
    }

    public void onFinishQuest(Player player, String quest) {
    }

    public Predicate<Item> cantPickupItem(Player player) {
        return a -> false;
    }

//    public boolean onPickupItem(Player player, Item item) {
//        return true;
//    }

    public void serverTick(ServerPlayer player) {
    }
    
    public void clientTick(Player player) {
    }
    
    public void rightClickEntity(Player player, Entity victim) {
    }
    
    public void leftClickEntity(Player player, Entity victim) {
    }

    public List<ShopEntry> getShopEntries() {
        return new ArrayList<>();
    }

    public List<ItemStack> getDefaultItems() {
        return new ArrayList<>();
    }

    public static AbilityPlayerComponent getCooldownComponent(Player player){
         return AbilityPlayerComponent.KEY.get(player);
    }

    public void onAbilityUse(Player player) {
    }

    private ComponentKey<? extends RoleComponent> componentKey;
    private int maxSprintTime;
    private boolean canSeeTime;

    public Consumer<LimitedInventoryScreen> getAddChild() {
        return addChild;
    }

    private Consumer<LimitedInventoryScreen> addChild;

    public enum MoodType {
        NONE, REAL, FAKE
    }


    /**
     * @param identifier    the mod id and name of the role
     * @param color         the role announcement color
     * @param isInnocent    whether the gun drops when a person with this role is shot and is considered a civilian to the win conditions
     * @param canUseKiller  can see and use the killer features
     * @param moodType      the mood type a role has
     * @param maxSprintTime the maximum sprint time in ticks
     * @param canSeeTime    if the role can see the game timer
     */
    public Role(ResourceLocation identifier, int color, boolean isInnocent, boolean canUseKiller, MoodType moodType, int maxSprintTime, boolean canSeeTime) {
        this.identifier = identifier;
        this.color = color;
        this.isInnocent = isInnocent;
        this.canUseKiller = canUseKiller;
        this.moodType = moodType;
        this.maxSprintTime = maxSprintTime;
        this.canSeeTime = canSeeTime;
    }

    public Role addChild(Consumer<LimitedInventoryScreen> addChild) {
        this.addChild = addChild;
        return this;
    }

    public ResourceLocation identifier() {
        return identifier;
    }

    public int color() {
        return color;
    }

    public boolean isInnocent() {
        return isInnocent;
    }

    public boolean canUseKiller() {
        return canUseKiller;
    }

    public MoodType getMoodType() {
        return moodType;
    }

    public int getMaxSprintTime() {
        return maxSprintTime;
    }

    public boolean canSeeTime() {
        return canSeeTime;
    }

    public boolean isGambler() {
        return false; // 暂时没有赌徒角色
    }
}