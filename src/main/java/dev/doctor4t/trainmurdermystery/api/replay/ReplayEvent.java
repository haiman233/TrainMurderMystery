package dev.doctor4t.trainmurdermystery.api.replay;

import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.EventDetails;
import dev.doctor4t.trainmurdermystery.api.replay.ReplayEventTypes.EventType;

public record ReplayEvent(EventType eventType, long timestamp, EventDetails details) {
}