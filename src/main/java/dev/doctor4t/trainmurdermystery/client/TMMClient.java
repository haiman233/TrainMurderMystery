package dev.doctor4t.trainmurdermystery.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.api.TMMGameModes;
import dev.doctor4t.trainmurdermystery.cca.*;

import dev.doctor4t.trainmurdermystery.client.gui.RoundTextRenderer;
import dev.doctor4t.trainmurdermystery.client.gui.SecurityCameraHUD;
import dev.doctor4t.trainmurdermystery.client.gui.screen.MapSelectorScreen;
import dev.doctor4t.trainmurdermystery.client.gui.screen.PlayerStatsScreen;
import dev.doctor4t.trainmurdermystery.client.gui.screen.WaypointHUD;
import dev.doctor4t.trainmurdermystery.entity.FirecrackerEntity;
import dev.doctor4t.trainmurdermystery.entity.NoteEntity;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.item.GrenadeItem;
import dev.doctor4t.trainmurdermystery.item.KnifeItem;
import dev.doctor4t.trainmurdermystery.network.*;

import dev.doctor4t.trainmurdermystery.network.packet.SyncSpecificWaypointVisibilityPacket;
import dev.doctor4t.trainmurdermystery.network.packet.SyncWaypointVisibilityPacket;
import dev.doctor4t.trainmurdermystery.network.packet.SyncWaypointsPacket;
import dev.doctor4t.trainmurdermystery.ui.TMMCommandUI;
import dev.doctor4t.trainmurdermystery.ui.event.KeyPressHandler;
import dev.doctor4t.trainmurdermystery.util.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Predicate;

public class TMMClient implements ClientModInitializer {
    private static float soundLevel = 0f;
    public static HandParticleManager handParticleManager;
    public static Map<Player, Vec3> particleMap;
    private static boolean prevGameRunning;
    public static GameWorldComponent gameComponent;
    public static TrainWorldComponent trainComponent;
    public static PlayerMoodComponent moodComponent;

    public static final Map<UUID, PlayerInfo> PLAYER_ENTRIES_CACHE = Maps.newHashMap();

    public static KeyMapping instinctKeybind;
    public static KeyMapping statsKeybind; // 新增统计面板热键
    public static boolean isInstinctToggleEnabled = false; // 新增变量用于跟踪切换状态
    public static boolean prevInstinctKeyDown = false; // 用于检测按键按下事件
    public static float prevInstinctLightLevel = -.04f;
    public static float instinctLightLevel = -.04f;

    public static boolean shouldDisableHudAndDebug() {
        Minecraft client = Minecraft.getInstance();
        return (client == null || (client.player != null && !client.player.isCreative() && !client.player.isSpectator()));
    }

    public static boolean isPlayerCreative() {
        return GameFunctions.isPlayerCreative(Minecraft.getInstance().player);
    }

    @Override
    public void onInitializeClient() {
        TMM.LOGGER.info("Initializing Train Murder Mystery Client...");

        handParticleManager = new HandParticleManager();
        particleMap = Maps.newHashMap();

        instinctKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.trainmurdermystery.instinct",
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.trainmurdermystery.keys"
        ));

        PayloadTypeRegistry.playS2C().register(SyncMapConfigPayload.ID, SyncMapConfigPayload.CODEC);
        SyncMapConfigPayload.registerReceiver();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            gameComponent = GameWorldComponent.KEY.get(client.level);
            trainComponent = TrainWorldComponent.KEY.get(client.level);
            moodComponent = PlayerMoodComponent.KEY.get(Minecraft.getInstance().player);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            gameComponent = null;
            trainComponent = null;
            moodComponent = null;
            PLAYER_ENTRIES_CACHE.clear();
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            TMMClient.handParticleManager.tick();
            RoundTextRenderer.tick();
            
            // 调用角色的客户端tick方法
            LocalPlayer player = client.player;
            if (player != null && player.level() != null) {
                dev.doctor4t.trainmurdermystery.api.RoleMethodDispatcher.callClientTick(player);
            }
        });
        SyncMapConfigPayload.registerReceiver();
        ClientPlayNetworking.registerGlobalReceiver(ShootMuzzleS2CPayload.ID, new ShootMuzzleS2CPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(PoisonUtils.PoisonOverlayPayload.ID, new PoisonUtils.PoisonOverlayPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(GunDropPayload.ID, new GunDropPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AnnounceWelcomePayload.ID, new AnnounceWelcomePayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AnnounceEndingPayload.ID, new AnnounceEndingPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(TaskCompletePayload.ID, new TaskCompletePayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(ShowStatsPayload.ID, (payload, context) -> {
            UUID targetPlayerUuid = payload.targetPlayerUuid();
            context.client().execute(() -> {
                context.client().setScreen(new PlayerStatsScreen(targetPlayerUuid));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ShowSelectedMapUIPayload.ID, (payload, context) -> {

            context.client().execute(() -> {
                context.client().setScreen(new MapSelectorScreen());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(CloseUiPayload.ID, (payload, context) -> {

            context.client().execute(() -> {
                context.client().setScreen(null);
            });
        });

        // Instinct keybind
        instinctKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key." + TMM.MOD_ID + ".instinct",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category." + TMM.MOD_ID + ".keybinds"
        ));

        // Register stats keybind
        statsKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key." + TMM.MOD_ID + ".stats",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O, // 默认热键 'O'
                "category." + TMM.MOD_ID + ".keybinds"
        ));
        
        // Initialize Command UI system
        TMMCommandUI.init();
        KeyPressHandler.register();
        InputHandler.initialize();
        

        
        // Register HUD rendering for security camera
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((guiGraphics, deltaTick) -> {
            SecurityCameraHUD.render(guiGraphics, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
            SecurityCameraHUD.renderCameraFeed(guiGraphics, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
            WaypointHUD.renderHUD(guiGraphics,deltaTick.getRealtimeDeltaTicks());
            AFKRenderer.renderAFKEffects(guiGraphics, deltaTick.getRealtimeDeltaTicks());
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncWaypointsPacket.ID, SyncWaypointsPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncWaypointVisibilityPacket.ID, SyncWaypointVisibilityPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncSpecificWaypointVisibilityPacket.ID, SyncSpecificWaypointVisibilityPacket::handle);

        // Register client tick event for stats keybind
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (statsKeybind.consumeClick()) {
                if (client.screen instanceof PlayerStatsScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new PlayerStatsScreen(client.player.getUUID()));
                }
            }
        });
    }

    public static TrainWorldComponent getTrainComponent() {
        return trainComponent;
    }

    public static float getTrainSpeed() {
        return trainComponent.getSpeed();
    }

    public static boolean isTrainMoving() {
        return trainComponent != null && trainComponent.getSpeed() > 0;
    }

    public static class CustomModelProvider implements ModelLoadingPlugin {

        private final Map<ResourceLocation, UnbakedModel> modelIdToBlock = new Object2ObjectOpenHashMap<>();
        private final Set<ResourceLocation> withInventoryVariant = new HashSet<>();

        public void register(Block block, UnbakedModel model) {
            this.register(BuiltInRegistries.BLOCK.getKey(block), model);
        }

        public void register(ResourceLocation id, UnbakedModel model) {
            this.modelIdToBlock.put(id, model);
        }

        public void markInventoryVariant(Block block) {
            this.markInventoryVariant(BuiltInRegistries.BLOCK.getKey(block));
        }

        public void markInventoryVariant(ResourceLocation id) {
            this.withInventoryVariant.add(id);
        }

        @Override
        public void onInitializeModelLoader(Context ctx) {
            ctx.modifyModelOnLoad().register((model, context) -> {
                ModelResourceLocation topLevelId = context.topLevelId();
                if (topLevelId == null) {
                    return model;
                }
                ResourceLocation id = topLevelId.id();
                if (topLevelId.getVariant().equals("inventory") && !this.withInventoryVariant.contains(id)) {
                    return model;
                }
                if (this.modelIdToBlock.containsKey(id)) {
                    return this.modelIdToBlock.get(id);
                }
                return model;
            });
        }
    }

    public static boolean isPlayerAliveAndInSurvival() {
        return GameFunctions.isPlayerAliveAndSurvival(Minecraft.getInstance().player);
    }

    public static boolean isPlayerSpectatingOrCreative() {
        return GameFunctions.isPlayerSpectatingOrCreative(Minecraft.getInstance().player);
    }

    public static boolean isKiller() {
        return gameComponent != null && gameComponent.canUseKillerFeatures(Minecraft.getInstance().player);
    }

    public static int getInstinctHighlight(Entity target) {
        if (!isInstinctEnabled()) return -1;
//        if (target instanceof PlayerBodyEntity) return 0x606060;
        if (target instanceof ItemEntity || target instanceof NoteEntity || target instanceof FirecrackerEntity)
            return 0xDB9D00;
        if (target instanceof Player player) {
            if (GameFunctions.isPlayerSpectatingOrCreative(player)) return -1;
            if (isKiller() && gameComponent.canUseKillerFeatures(player)) return Mth.hsvToRgb(0F, 1.0F, 0.6F);
            if (gameComponent.isInnocent(player)) {
                float mood = PlayerMoodComponent.KEY.get(target).getMood();
                if (mood < GameConstants.DEPRESSIVE_MOOD_THRESHOLD) {
                    return 0x171DC6;
                } else if (mood < GameConstants.MID_MOOD_THRESHOLD) {
                    return 0x1FAFAF;
                } else {
                    return 0x4EDD35;
                }
            }
            if (isPlayerSpectatingOrCreative()) return 0xFFFFFF;
        }
        return -1;
    }

    static Predicate<Player> isHoldSpecialItem = (player) -> {
        if (player.getMainHandItem().getItem() instanceof KnifeItem) return true;
        if (player.getMainHandItem().getItem() instanceof GrenadeItem) return true;
        return false;
    };
    public static boolean isInstinctEnabled() {
        final var player = Minecraft.getInstance().player;
        return (isInstinctToggleEnabled && ((isKiller() && isPlayerAliveAndInSurvival()) || isPlayerSpectatingOrCreative())) || (isKiller() && isHoldSpecialItem.test(player));
    }

    public static Object getLockedRenderDistance(boolean ultraPerfMode) {
        return null;
    }
}
