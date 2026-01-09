package dev.doctor4t.trainmurdermystery.api.replay.serializers;

import com.google.gson.*;
import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.KeyUsedDetails;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.UUID;

public class KeyUsedDetailsSerializer implements JsonSerializer<KeyUsedDetails>, JsonDeserializer<KeyUsedDetails> {
    @Override
    public JsonElement serialize(KeyUsedDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerUuid", src.playerUuid().toString());
        jsonObject.addProperty("keyItemId", src.keyItemId().toString());
        jsonObject.addProperty("doorPosX", src.doorPos().getX());
        jsonObject.addProperty("doorPosY", src.doorPos().getY());
        jsonObject.addProperty("doorPosZ", src.doorPos().getZ());
        return jsonObject;
    }

    @Override
    public KeyUsedDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        UUID playerUuid = UUID.fromString(jsonObject.get("playerUuid").getAsString());
        ResourceLocation keyItemId = ResourceLocation.parse(jsonObject.get("keyItemId").getAsString());
        int doorPosX = jsonObject.get("doorPosX").getAsInt();
        int doorPosY = jsonObject.get("doorPosY").getAsInt();
        int doorPosZ = jsonObject.get("doorPosZ").getAsInt();
        BlockPos doorPos = new BlockPos(doorPosX, doorPosY, doorPosZ);
        return new KeyUsedDetails(playerUuid, keyItemId, doorPos);
    }
}