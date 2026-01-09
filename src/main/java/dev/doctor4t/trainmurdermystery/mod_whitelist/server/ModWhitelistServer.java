package dev.doctor4t.trainmurdermystery.mod_whitelist.server;

import dev.doctor4t.trainmurdermystery.mod_whitelist.server.config.MWServerConfig;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ModWhitelistServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		MWServerConfig.hello();
	}
}
