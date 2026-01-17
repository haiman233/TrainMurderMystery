package dev.doctor4t.trainmurdermystery.network;

import dev.doctor4t.trainmurdermystery.TMM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public class ShowSelectedMapUIPayload implements CustomPacketPayload {
    public static final Type<ShowSelectedMapUIPayload> ID = new Type<>(TMM.id("show_selected_map_ui"));
    public static final StreamCodec<FriendlyByteBuf, ShowSelectedMapUIPayload> CODEC = CustomPacketPayload.codec(ShowSelectedMapUIPayload::write, ShowSelectedMapUIPayload::new);

    public ShowSelectedMapUIPayload(FriendlyByteBuf friendlyByteBuf) {

    }
    public ShowSelectedMapUIPayload() {

    }


    public void write(FriendlyByteBuf buf) {

    }
 
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
