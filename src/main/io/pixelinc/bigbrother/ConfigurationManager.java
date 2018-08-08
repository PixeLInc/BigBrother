package io.pixelinc.bigbrother;

import java.io.IOException;

public class ConfigurationManager {

    private BigBrother plugin;
    public ConfigurationManager(BigBrother plugin) {
        this.plugin = plugin;
        if (!plugin.configDir.exists())
            plugin.configDir.mkdir();
    }

    private CoreConfig coreConfig;

    public void loadCore() {
        try {
            coreConfig = new CoreConfig(plugin);
            plugin.logger.info("[CoreConfig] Loaded.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public CoreConfig getCore() {
        return this.coreConfig;
    }

    // if we ever have multiple, just kinda an abstraction for it.
    // array of configs and what not
    public void reloadAllConfigs() {
        try {
            coreConfig.loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
