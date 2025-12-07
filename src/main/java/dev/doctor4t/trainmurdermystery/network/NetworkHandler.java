package dev.doctor4t.trainmurdermystery.network;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class NetworkHandler {
    public static void sendToNearBy(World world, BlockPos pos, CustomPayload toSend) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;

            serverWorld.getServer().getPlayerManager().getPlayerList().stream()
                    .filter(p -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 96 * 96)
                    .forEach(p -> ServerPlayNetworking.send(p, toSend));
        }
    }

    public static void sendToClientPlayer(CustomPayload toSend, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, toSend);
    }
    public static void sendToServer(CustomPayload toSend) {
        ClientPlayNetworking.send(toSend);
    }
}