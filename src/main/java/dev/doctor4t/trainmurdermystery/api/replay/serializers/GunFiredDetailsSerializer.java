package dev.doctor4t.trainmurdermystery.api.replay.serializers;

import com.google.gson.*;
import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.GunFiredDetails;

import java.lang.reflect.Type;
import java.util.UUID;

public class GunFiredDetailsSerializer implements JsonSerializer<GunFiredDetails>, JsonDeserializer<GunFiredDetails> {
    @Override
    public JsonElement serialize(GunFiredDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerUuid", src.playerUuid().toString());
        jsonObject.addProperty("hit", src.hit());
        if (src.targetUuid() != null) {
            jsonObject.addProperty("targetUuid", src.targetUuid().toString());
        }
        return jsonObject;
    }

    @Override
    public GunFiredDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        UUID playerUuid = UUID.fromString(jsonObject.get("playerUuid").getAsString());
        boolean hit = jsonObject.get("hit").getAsBoolean();
        UUID targetUuid = null;
        if (jsonObject.has("targetUuid")) {
            targetUuid = UUID.fromString(jsonObject.get("targetUuid").getAsString());
        }
        return new GunFiredDetails(playerUuid, hit, targetUuid);
    }
}