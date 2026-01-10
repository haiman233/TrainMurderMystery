package dev.doctor4t.trainmurdermystery.event;

import dev.doctor4t.trainmurdermystery.cca.PlayerAFKComponent;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * AFK事件处理器
 * 监听玩家的各种活动事件，用于检测玩家是否挂机
 */
public class AFKEventHandler {

    /**
     * 注册所有AFK检测相关的事件处理器
     */
    public static void register() {


        // 当玩家攻击方块时更新AFK计时器
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player != null) {
                PlayerAFKComponent afkComponent = PlayerAFKComponent.KEY.maybeGet(player).orElse(null);
                if (afkComponent != null) {
                    afkComponent.updateActivity();
                }
            }
            return InteractionResult.PASS;
        });

        // 当玩家攻击实体时更新AFK计时器
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player != null) {
                PlayerAFKComponent afkComponent = PlayerAFKComponent.KEY.maybeGet(player).orElse(null);
                if (afkComponent != null) {
                    afkComponent.updateActivity();
                }
            }
            return InteractionResult.PASS;
        });

        // 当玩家使用物品时更新AFK计时器
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player != null) {
                PlayerAFKComponent afkComponent = PlayerAFKComponent.KEY.maybeGet(player).orElse(null);
                if (afkComponent != null) {
                    afkComponent.updateActivity();
                }
            }
            return InteractionResultHolder.pass(ItemStack.EMPTY);
        });

        // 当玩家与方块交互时更新AFK计时器
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player != null) {
                PlayerAFKComponent afkComponent = PlayerAFKComponent.KEY.maybeGet(player).orElse(null);
                if (afkComponent != null) {
                    afkComponent.updateActivity();
                }
            }
            return InteractionResult.PASS;
        });

        // 当玩家与实体交互时更新AFK计时器
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player != null) {
                PlayerAFKComponent afkComponent = PlayerAFKComponent.KEY.maybeGet(player).orElse(null);
                if (afkComponent != null) {
                    afkComponent.updateActivity();
                }
            }
            return InteractionResult.PASS;
        });
    }
}