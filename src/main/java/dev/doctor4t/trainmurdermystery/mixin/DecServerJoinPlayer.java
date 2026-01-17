package dev.doctor4t.trainmurdermystery.mixin;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.compat.TrainVoicePlugin;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.game.GameReplayManager;
import net.minecraft.network.Connection;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.ladysnake.cca.api.v3.component.ComponentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class DecServerJoinPlayer {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        GameReplayManager.playerNames.put(serverPlayer.getUUID(), serverPlayer.getScoreboardName());
        final var gameWorldComponent = GameWorldComponent.KEY.get(serverPlayer.level());
        if (gameWorldComponent.isRunning()){
            if (!GameFunctions.isPlayerAliveAndSurvival(serverPlayer)){
                TrainVoicePlugin.addPlayer(serverPlayer.getUUID());

            }
        }
        gameWorldComponent.setSyncRole( true);
        GameWorldComponent.KEY.syncWith(serverPlayer, (ComponentProvider) serverPlayer.level());
        gameWorldComponent.setSyncRole( false);
    }
}
