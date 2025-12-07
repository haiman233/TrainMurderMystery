package dev.doctor4t.trainmurdermystery.network;
import dev.doctor4t.trainmurdermystery.TMM;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
public class ReadingMessage implements CustomPayload {
    private final static Identifier PACKET_ID = Identifier.of(TMM.MOD_ID, "reading_book");
    public static final CustomPayload.Id<ReadingMessage> TYPE = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<PacketByteBuf, ReadingMessage> STREAM_CODEC = PacketCodec.unit(
            new ReadingMessage()
    );


    public ReadingMessage() {

    }



    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}