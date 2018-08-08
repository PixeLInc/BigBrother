package io.pixelinc.bigbrother.events;

import io.pixelinc.bigbrother.BigBrother;
import io.pixelinc.bigbrother.database.SerializableLocation;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class BlockEvent extends BasicEvent {

    public BlockEvent(BigBrother plugin) {
        super(plugin);
    }

    // transaction system is odd, way different than I expected.
    // getFinal() returns the block _AFTER_ it was broken/placed, so
    // on break, it will return air since that's after it was broken
    // but if you use getOriginal() that returns the block before it was broken
    // meaning we can log that and such.

    private void logEvent(String eventType, Player player, BlockSnapshot snapshot) {
        UUID playerUUID = player.getUniqueId();

        String blockName = snapshot.getState().getType().getName();
        String sLocation = SerializableLocation.serialize(snapshot.getLocation().orElse(null));
        if (sLocation == null)
            plugin.logger.error("Failed to grab world location of broken block (" + player.getName() + " | " + blockName + ")");

        try{
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("INSERT INTO block_logs(uuid, location, event, block) VALUES(?, ?, ?, ?)");
            statement.setString(1, playerUUID.toString());
            statement.setString(2, sLocation);
            statement.setString(3, eventType);
            statement.setString(4, blockName);

            // we dont need a ResultSet back, so just send it
            statement.execute();
            plugin.logger.info("Block logged by " + playerUUID.toString() + "(" + player.getName() + "): " + blockName + " at " + sLocation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event) {
        Player player = event.getCause().first(Player.class).orElse(null);
        if (player == null)
            return;

        event.getTransactions().forEach((trans) -> logEvent("Place", player, trans.getFinal()));
    }

    @Listener
    public void onBlockModify(ChangeBlockEvent.Modify event) {
        Player player = event.getCause().first(Player.class).orElse(null);
        if (player == null)
            return;

        event.getTransactions().forEach((trans) -> logEvent("Modify", player, trans.getFinal()));
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        Player player = event.getCause().first(Player.class).orElse(null);
        if (player == null)
            return;

        event.getTransactions().forEach((trans) -> logEvent("Break", player, trans.getOriginal()));
    }

    @Listener
    public void onInventoryOpen(InteractInventoryEvent.Open event) {
        Player player = event.getCause().first(Player.class).orElse(null);
        if (player == null) {
            plugin.logger.error("Player is null in inventory open event!");
            return;
        }

        Optional<BlockSnapshot> block = event.getContext().get(EventContextKeys.BLOCK_HIT);
        if (!block.isPresent()) return;

        logEvent("InvOpen", player, block.get());
    }
}
