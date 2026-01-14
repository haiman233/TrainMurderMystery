package dev.doctor4t.trainmurdermystery.cca;

import dev.doctor4t.trainmurdermystery.TMM;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatsComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<PlayerStatsComponent> KEY = ComponentRegistry.getOrCreate(TMM.id("player_stats"), PlayerStatsComponent.class);
    private final Player player;
    private long totalPlayTime = 0;
    private int totalGamesPlayed = 0;
    private int totalKills = 0;
    private int totalDeaths = 0;
    private int totalWins = 0;
    private int totalLosses = 0;
    private int totalTeamKills = 0;
    private final Map<ResourceLocation, RoleStats> roleStats = new HashMap<>();

    public PlayerStatsComponent(Player player) {
        this.player = player;
    }

    public void sync() {

    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag, HolderLookup.Provider wrapperLookup) {
        totalPlayTime = tag.getLong("TotalPlayTime");
        totalGamesPlayed = tag.getInt("TotalGamesPlayed");
        totalKills = tag.getInt("TotalKills");
        totalDeaths = tag.getInt("TotalDeaths");
        totalWins = tag.getInt("TotalWins");
        totalLosses = tag.getInt("TotalLosses");
        if (tag.contains("TotalTeamKills")) {
            totalTeamKills = tag.getInt("TotalTeamKills");
        }

        ListTag roleStatsList = tag.getList("RoleStats", Tag.TAG_COMPOUND);
        roleStats.clear();
        for (Tag element : roleStatsList) {
            CompoundTag roleTag = (CompoundTag) element;
            ResourceLocation roleId = ResourceLocation.parse(roleTag.getString("RoleId"));
            RoleStats stats = new RoleStats();
            stats.readFromNbt(roleTag, wrapperLookup);
            roleStats.put(roleId, stats);
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag, HolderLookup.Provider wrapperLookup) {
        tag.putLong("TotalPlayTime", totalPlayTime);
        tag.putInt("TotalGamesPlayed", totalGamesPlayed);
        tag.putInt("TotalKills", totalKills);
        tag.putInt("TotalDeaths", totalDeaths);
        tag.putInt("TotalWins", totalWins);
        tag.putInt("TotalLosses", totalLosses);
        tag.putInt("TotalTeamKills", totalTeamKills);

        ListTag roleStatsList = new ListTag();
        for (Map.Entry<ResourceLocation, RoleStats> entry : roleStats.entrySet()) {
            CompoundTag roleTag = new CompoundTag();
            roleTag.putString("RoleId", entry.getKey().toString());
            entry.getValue().writeToNbt(roleTag, wrapperLookup);
            roleStatsList.add(roleTag);
        }
        tag.put("RoleStats", roleStatsList);
    }

    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public void addPlayTime(long ticks) {
        this.totalPlayTime += ticks;
        this.sync();
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void incrementTotalGamesPlayed() {
        this.totalGamesPlayed++;
        this.sync();
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void incrementTotalKills() {
        this.totalKills++;
        this.sync();
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void incrementTotalDeaths() {
        this.totalDeaths++;
        this.sync();
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void incrementTotalWins() {
        this.totalWins++;
        this.sync();
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void incrementTotalLosses() {
        this.totalLosses++;
        this.sync();
    }

    public int getTotalTeamKills() {
        return totalTeamKills;
    }

    public void incrementTotalTeamKills() {
        this.totalTeamKills++;
        this.sync();
    }

    public Map<ResourceLocation, RoleStats> getRoleStats() {
        return roleStats;
    }

    public RoleStats getOrCreateRoleStats(ResourceLocation roleId) {
        return roleStats.computeIfAbsent(roleId, k -> new RoleStats());
    }

    @Override
    public void clientTick() {

    }

    @Override
    public void serverTick() {

    }

    public class RoleStats {
        private int timesPlayed = 0;
        private int killsAsRole = 0;
        private int deathsAsRole = 0;
        private int winsAsRole = 0;
        private int lossesAsRole = 0;
        private int teamKillsAsRole = 0;

        public RoleStats() {
        }

        public void readFromNbt(CompoundTag tag, HolderLookup.Provider wrapperLookup) {
            timesPlayed = tag.getInt("TimesPlayed");
            killsAsRole = tag.getInt("KillsAsRole");
            deathsAsRole = tag.getInt("DeathsAsRole");
            winsAsRole = tag.getInt("WinsAsRole");
            lossesAsRole = tag.getInt("LossesAsRole");
            if (tag.contains("TeamKillsAsRole")) {
                teamKillsAsRole = tag.getInt("TeamKillsAsRole");
            }
        }

        public void writeToNbt(CompoundTag tag, HolderLookup.Provider wrapperLookup) {
            tag.putInt("TimesPlayed", timesPlayed);
            tag.putInt("KillsAsRole", killsAsRole);
            tag.putInt("DeathsAsRole", deathsAsRole);
            tag.putInt("WinsAsRole", winsAsRole);
            tag.putInt("LossesAsRole", lossesAsRole);
            tag.putInt("TeamKillsAsRole", teamKillsAsRole);
        }

        public int getTimesPlayed() {
            return timesPlayed;
        }

        public void incrementTimesPlayed() {
            this.timesPlayed++;
            PlayerStatsComponent.this.sync();
        }

        public int getKillsAsRole() {
            return killsAsRole;
        }

        public void incrementKillsAsRole() {
            this.killsAsRole++;
            PlayerStatsComponent.this.sync();
        }

        public int getDeathsAsRole() {
            return deathsAsRole;
        }

        public void incrementDeathsAsRole() {
            this.deathsAsRole++;
            PlayerStatsComponent.this.sync();
        }

        public int getWinsAsRole() {
            return winsAsRole;
        }

        public void incrementWinsAsRole() {
            this.winsAsRole++;
            PlayerStatsComponent.this.sync();
        }

        public int getLossesAsRole() {
            return lossesAsRole;
        }

        public void incrementLossesAsRole() {
            this.lossesAsRole++;
            PlayerStatsComponent.this.sync();
        }

        public int getTeamKillsAsRole() {
            return teamKillsAsRole;
        }

        public void incrementTeamKillsAsRole() {
            this.teamKillsAsRole++;
            PlayerStatsComponent.this.sync();
        }
    }
}