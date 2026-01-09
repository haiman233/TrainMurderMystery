package dev.doctor4t.trainmurdermystery.api.replay.serializers;

import com.google.gson.*;
import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.RoundEndDetails;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;

import java.lang.reflect.Type;

public class RoundEndDetailsSerializer implements JsonSerializer<RoundEndDetails>, JsonDeserializer<RoundEndDetails> {
    @Override
    public JsonElement serialize(RoundEndDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roundResult", src.roundResult().name());
        return jsonObject;
    }

    @Override
    public RoundEndDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        GameFunctions.WinStatus roundResult = GameFunctions.WinStatus.valueOf(jsonObject.get("roundResult").getAsString());
        return new RoundEndDetails(roundResult);
    }
}