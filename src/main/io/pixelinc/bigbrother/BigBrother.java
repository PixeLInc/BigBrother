package io.pixelinc.bigbrother;

import com.google.inject.Inject;
import io.pixelinc.bigbrother.commands.CommandRegister;
import io.pixelinc.bigbrother.database.MySQL;
import io.pixelinc.bigbrother.events.BlockEvent;
import io.pixelinc.bigbrother.events.InteractEvent;
import io.pixelinc.bigbrother.services.AsyncService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Plugin(id = "bigbrother", name = "BigBrother", version = "1.0", description = "I am always watching")
public class BigBrother {
    @Inject
    public Logger logger;

    @Inject
    public Game game;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File configDir;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path configPath;

    public ConfigurationManager configManager;
    public MySQL database;
    public CommandRegister commandRegister;
    public AsyncService asyncService;
    public UserStorageService storageService;

    public Set<UUID> activeInspectors = new HashSet<>();


    @Listener
    public void onServerPreInit(GamePreInitializationEvent event) {
        this.configManager = new ConfigurationManager(this);
        this.configManager.loadCore();
        this.storageService = Sponge.getServiceManager().provide(UserStorageService.class).orElse(null);
        if (this.storageService == null)
            logger.error("UserStorageService is null, woops.");

        this.asyncService = new AsyncService(this);

        this.commandRegister = new CommandRegister(this);
        this.commandRegister.register();

        database = new MySQL(this);
        // its not necessarily expensive to open a connection, could keep it closed but with a bunch of block breaks/places and other events might as well.
        database.openConnection();

        // load events
        Sponge.getEventManager().registerListeners(this, new BlockEvent(this));
        Sponge.getEventManager().registerListeners(this, new InteractEvent(this));
    }

    @Listener
    public void onServerClose(GameStoppingServerEvent event) {
        if (!database.isClosed()) {
            try {
                database.activeConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        configManager.reloadAllConfigs();
        logger.info("[ConfigManager] Reloaded all configs");
    }

}