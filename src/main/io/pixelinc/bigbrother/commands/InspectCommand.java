package io.pixelinc.bigbrother.commands;

import io.pixelinc.bigbrother.BigBrother;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class InspectCommand implements CommandExecutor {

    private BigBrother plugin;
    public InspectCommand(BigBrother plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;

            if (!plugin.activeInspectors.contains(player.getUniqueId())) {
                plugin.activeInspectors.add(player.getUniqueId());
                player.sendMessage(Text.of(TextColors.GREEN, " Toggled inspect mode ON"));
            } else {
                plugin.activeInspectors.remove(player.getUniqueId());
                player.sendMessage(Text.of(TextColors.RED, " Toggled inspect mode OFF"));
            }
        } else
            src.sendMessage(Text.of("You can't inspect since you don't have hands, silly!"));
        return CommandResult.success();
    }
}
