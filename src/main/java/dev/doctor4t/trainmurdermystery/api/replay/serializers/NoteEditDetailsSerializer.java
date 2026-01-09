package dev.doctor4t.trainmurdermystery.api.replay.serializers;

import com.google.gson.*;
import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.NoteEditDetails;

import java.lang.reflect.Type;
import java.util.UUID;

public class NoteEditDetailsSerializer implements JsonSerializer<NoteEditDetails>, JsonDeserializer<NoteEditDetails> {
    @Override
    public JsonElement serialize(NoteEditDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerUuid", src.playerUuid().toString());
        jsonObject.addProperty("noteContent", src.noteContent());
        return jsonObject;
    }

    @Override
    public NoteEditDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        UUID playerUuid = UUID.fromString(jsonObject.get("playerUuid").getAsString());
        String noteContent = jsonObject.get("noteContent").getAsString();
        return new NoteEditDetails(playerUuid, noteContent);
    }
}