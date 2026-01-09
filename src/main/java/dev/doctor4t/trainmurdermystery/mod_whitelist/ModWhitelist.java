package dev.doctor4t.trainmurdermystery.mod_whitelist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import static dev.doctor4t.trainmurdermystery.TMM.MOD_ID;

public class ModWhitelist {

	public static final String MOD_NAME = "Mod Whitelist";
	public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
	

}
