package dev.doctor4t.trainmurdermystery.network;

import dev.doctor4t.trainmurdermystery.block.SecurityMonitorBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SecurityCameraModePayload(boolean enable, BlockPos cameraPos) implements CustomPacketPayload {
    public static final Type<SecurityCameraModePayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("trainmurdermystery", "security_camera_mode"));
    public static final StreamCodec<FriendlyByteBuf, SecurityCameraModePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SecurityCameraModePayload::enable,
            BlockPos.STREAM_CODEC, SecurityCameraModePayload::cameraPos,
            SecurityCameraModePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class ClientReceiver implements ClientPlayNetworking.PlayPayloadHandler<SecurityCameraModePayload>  {



        @Override
        public void receive(SecurityCameraModePayload payload, ClientPlayNetworking.Context context) {
            context.client().execute(() -> {
                if (payload.enable()) {
                    // 进入监控模式
                    SecurityMonitorBlock.setCurrentCameraPos(payload.cameraPos());
                    SecurityMonitorBlock.setSecurityMode(true);
                    Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
                } else {
                    // 退出监控模式
                    SecurityMonitorBlock.setSecurityMode(false);
                    Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
                    SecurityMonitorBlock.setCurrentCameraPos(null);
                }
            });
        }
    }
    
    public static class ServerReceiver {
        // 服务端接收来自客户端的包（如果需要的话）
    }
}
