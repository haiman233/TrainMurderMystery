package dev.doctor4t.trainmurdermystery.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 角色方法调度器，用于调用角色的各个方法
 */
public class RoleMethodDispatcher {
    
    /**
     * 调用玩家角色的 onDeath 方法
     */
    public static void callOnDeath(Player victim, boolean spawnBody, @Nullable Player killer, ResourceLocation deathReason) {
        Role role = getCurrentRole(victim);
        if (role != null) {
            role.onDeath(victim, spawnBody, killer, deathReason);
        }
    }
    
    /**
     * 调用玩家角色的 onKill 方法
     */
    public static void callOnKill(Player victim, boolean spawnBody, @Nullable Player killer, ResourceLocation deathReason) {
        Role role = getCurrentRole(killer);
        if (role != null) {
            role.onKill(victim, spawnBody, killer, deathReason);
        }
    }
    
    /**
     * 调用玩家角色的 onFinishQuest 方法
     */
    public static void callOnFinishQuest(Player player, String quest) {
        Role role = getCurrentRole(player);
        if (role != null) {
            role.onFinishQuest(player, quest);
        }
    }
    
    /**
     * 调用玩家角色的 cantPickupItem 方法
     */
//    public static boolean callCantPickupItem(Player player, Item item) {
//        Role role = getCurrentRole(player);
//        if (role != null) {
//            return role.cantPickupItem(player).test(item);
//        }
//        return false;
//    }
//
    /**
     * 调用玩家角色的 onPickupItem 方法
     */
    public static boolean callOnPickupItem(Player player, Item item) {
        Role role = getCurrentRole(player);
        if (role != null) {
           return role.cantPickupItem(player).test( item);
        }
        return true;
    }
    
    /**
     * 调用玩家角色的 serverTick 方法
     */
    public static void callServerTick(ServerPlayer player) {
        Role role = getCurrentRole(player);
        if (role != null) {
            role.serverTick(player);
        }
    }
    
    /**
     * 调用玩家角色的 clientTick 方法
     */
    public static void callClientTick(Player player) {
        Role role = getCurrentRole(player);
        if (role != null) {
            role.clientTick(player);
        }
    }
    
    /**
     * 调用玩家角色的 rightClickEntity 方法
     */
    public static void callRightClickEntity(Player player, Entity victim) {
        Role role = getCurrentRole(player);
        if (role != null) {
            role.rightClickEntity(player, victim);
        }
    }
    
    /**
     * 调用玩家角色的 leftClickEntity 方法
     */
    public static void callLeftClickEntity(Player player, Entity victim) {
        Role role = getCurrentRole(player);
        if (role != null) {
            role.leftClickEntity(player, victim);
        }
    }
    
    /**
     * 调用玩家角色的 onAbilityUse 方法
     */
    public static void callOnAbilityUse(Player player) {
        Role role = getCurrentRole(player);
        if (role != null) {
            role.onAbilityUse(player);
        }
    }
    
    /**
     * 获取玩家当前的角色
     */
    private static Role getCurrentRole(Player player) {
        if (player.level() == null) {
            return null;
        }
        
        var gameComponent = dev.doctor4t.trainmurdermystery.cca.GameWorldComponent.KEY.get(player.level());
        return gameComponent.getRole(player);
    }
}