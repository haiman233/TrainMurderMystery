package dev.doctor4t.trainmurdermystery.network;

import dev.doctor4t.trainmurdermystery.TMM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;
 
public record ShowStatsPayload(UUID targetPlayerUuid) implements CustomPacketPayload {
    public static final Type<ShowStatsPayload> ID = new Type<>(TMM.id("show_stats"));
    public static final StreamCodec<FriendlyByteBuf, ShowStatsPayload> CODEC = CustomPacketPayload.codec(ShowStatsPayload::write, ShowStatsPayload::new);
 
    public ShowStatsPayload(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }
 
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(targetPlayerUuid);
    }
 
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
