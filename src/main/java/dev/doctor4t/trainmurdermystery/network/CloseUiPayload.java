package dev.doctor4t.trainmurdermystery.network;

import dev.doctor4t.trainmurdermystery.TMM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class CloseUiPayload implements CustomPacketPayload {
    public static final Type<CloseUiPayload> ID = new Type<>(TMM.id("close_ui"));
    public static final StreamCodec<FriendlyByteBuf, CloseUiPayload> CODEC = CustomPacketPayload.codec(CloseUiPayload::write, CloseUiPayload::new);

    public CloseUiPayload(FriendlyByteBuf friendlyByteBuf) {

    }
    public CloseUiPayload() {

    }


    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
