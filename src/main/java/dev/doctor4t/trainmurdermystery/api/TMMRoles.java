package dev.doctor4t.trainmurdermystery.api;

import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TMMRoles {
    public static final Map<ResourceLocation,Role> ROLES = new HashMap<>();

    public static final Role DISCOVERY_CIVILIAN = registerRole(new NoramlRole(TMM.id("discovery_civilian"), 0x36E51B, true, false, Role.MoodType.NONE, -1, true));
    public static final Role CIVILIAN = registerRole(new NoramlRole(TMM.id("civilian"), 0x36E51B, true, false, Role.MoodType.REAL, GameConstants.getInTicks(0, 10), false));
    public static final Role VIGILANTE = registerRole(new NoramlRole(TMM.id("vigilante"), 0x1B8AE5, true, false, Role.MoodType.REAL, GameConstants.getInTicks(0, 10), false));
    public static final Role KILLER = registerRole(new NoramlRole(TMM.id("killer"), 0xC13838, false, true, Role.MoodType.FAKE, -1, true));
    public static final Role LOOSE_END = registerRole(new NoramlRole(TMM.id("loose_end"), 0x9F0000, false, false, Role.MoodType.NONE, -1, false));

    public static Role registerRole(Role role) {
        ROLES.put(role.identifier(), role);
        return role;
    }
}
