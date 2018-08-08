package io.pixelinc.bigbrother.commands;

import io.pixelinc.bigbrother.BigBrother;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class LookupCommand implements CommandExecutor {

    private BigBrother plugin;

    public LookupCommand(BigBrother plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String lookupTarget = args.<String>getOne("player").orElse(null);
        if (lookupTarget == null) {
            src.sendMessage(Text.of("Invalid argument <player>"));
            return null;
        }

        Optional<User> optUser = this.plugin.storageService.get(lookupTarget);
        if (!optUser.isPresent()) {
            src.sendMessage(Text.of("Invalid User, Is their name right?"));
            return null;
        }

        User user = optUser.get();
        src.sendMessage(Text.of(TextColors.GREEN, " Looking up player..."));

        // send it over to our async task
        plugin.asyncService.lookup(src, user);

        return CommandResult.success();
    }
}
