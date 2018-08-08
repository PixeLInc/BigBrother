package io.pixelinc.bigbrother.events;

import io.pixelinc.bigbrother.BigBrother;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class InteractEvent extends BasicEvent{

    public InteractEvent(BigBrother plugin) {
        super(plugin);
    }

    @Listener
    public void onInteract(InteractBlockEvent.Secondary.MainHand event) {
        Player player = event.getCause().first(Player.class).orElse(null);
        if (player == null)
            return;

        String blockName = event.getTargetBlock().getState().getType().getName();
        Location<World> location = event.getTargetBlock().getLocation().orElse(null);
        if (!blockName.equalsIgnoreCase("minecraft:air") && location == null) {
            plugin.logger.error("Location is null of " + blockName);
            return;
        }

        if (plugin.activeInspectors.contains(player.getUniqueId())) {
            // make sure they're not holding anything so they can place and inspect things nd such.
            // maybe limit to a tool? :<
            Optional<ItemStack> heldItem = player.getItemInHand(HandTypes.MAIN_HAND);
            if (!heldItem.isPresent() || heldItem.get().getType().equals(ItemTypes.NONE)) {
                // send it off to our async service
                plugin.asyncService.lookup(player, location);
                event.setCancelled(true);
            }
        }
    }
}
