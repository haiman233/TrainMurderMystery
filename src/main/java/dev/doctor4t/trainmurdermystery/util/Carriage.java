package dev.doctor4t.trainmurdermystery.util;

import net.minecraft.world.phys.AABB;

import java.util.List;

public record Carriage(List<AABB> areas, String name) {
}