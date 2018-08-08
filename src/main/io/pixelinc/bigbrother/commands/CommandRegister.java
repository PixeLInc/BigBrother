package io.pixelinc.bigbrother.commands;

import io.pixelinc.bigbrother.BigBrother;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandRegister {

    private BigBrother plugin;
    public CommandRegister(BigBrother plugin) {
        this.plugin = plugin;
    }

    // commands

    public void register() {
        CommandSpec inspectCommand = CommandSpec.builder()
                .description(Text.of("Inspect blocks to see logs of what's happened"))
                .permission("bigbrother.inspect")
                .executor(new InspectCommand(plugin))
                .build();

        CommandSpec lookupCommand = CommandSpec.builder()
                .description(Text.of("Lookup past records for a player"))
                .permission("bigbrother.lookup")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player"))))
                .executor(new LookupCommand(plugin))
                .build();
        Sponge.getCommandManager().register(plugin, inspectCommand, "inspect");
        Sponge.getCommandManager().register(plugin, lookupCommand, "lookup");
    }

}
