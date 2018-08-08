package io.pixelinc.bigbrother;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.IOException;
import java.nio.file.Path;

@ConfigSerializable
public class CoreConfig {

    public final BigBrother plugin;

    public ConfigurationLoader<CommentedConfigurationNode> loader;
    public CommentedConfigurationNode config;
    public Path fileLoc;

    public CoreConfig(BigBrother plugin) throws IOException {
        this.plugin = plugin;

        this.fileLoc = plugin.configPath.resolve("core.conf");
        this.loader = HoconConfigurationLoader.builder().setPath(fileLoc).build();
        this.config = loader.load();

        setupConfig();
    }

    // config options
    public String mHost = "[::1]";
    public Integer mPort = 3306;
    public String mUsername = "opalbot";
    public String mPassword = "skynet!";
    public String mDbName = "opalcraft";

    private void setupConfig() throws IOException {
        if (!fileLoc.toFile().exists())
            fileLoc.toFile().createNewFile();

        mHost = get(config.getNode("mysql", "host"), mHost).getString();
        mPort = get(config.getNode("mysql" , "port"), mPort).getInt();
        mUsername = get(config.getNode("mysql", "username"), mUsername).getString();
        mPassword = get(config.getNode("mysql", "password"), mPassword).getString();
        mDbName = get(config.getNode("mysql", "dbName"), mDbName).getString();

        loader.save(config);
        plugin.logger.info("[CoreConfig] Setup and Saved.");
    }

    private CommentedConfigurationNode get(CommentedConfigurationNode node, Object val) {
        if (node.isVirtual())
            node.setValue(val);

        return node;
    }
}
