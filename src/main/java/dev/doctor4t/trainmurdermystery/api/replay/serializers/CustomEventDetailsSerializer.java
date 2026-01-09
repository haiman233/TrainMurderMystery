package dev.doctor4t.trainmurdermystery.api.replay.serializers;

import com.google.gson.*;
import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.CustomEventDetails;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public class CustomEventDetailsSerializer implements JsonSerializer<CustomEventDetails>, JsonDeserializer<CustomEventDetails> {
    @Override
    public JsonElement serialize(CustomEventDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eventId", src.eventId().toString());
        jsonObject.addProperty("data", src.data());
        return jsonObject;
    }

    @Override
    public CustomEventDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ResourceLocation eventId = ResourceLocation.parse(jsonObject.get("eventId").getAsString());
        String data = jsonObject.get("data").getAsString();
        return new CustomEventDetails(eventId, data);
    }
}