package io.pixelinc.bigbrother.services;

import io.pixelinc.bigbrother.BigBrother;
import io.pixelinc.bigbrother.database.SerializableLocation;
import io.pixelinc.bigbrother.util.LookupRecord;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AsyncService {

    private BigBrother plugin;
    public AsyncService(BigBrother plugin) {
        this.plugin = plugin;
    }

    // we need to search thru all the records in the table for the player and their actions
    public void lookup(CommandSource caller, final User user) {
        try {
            PreparedStatement statement = plugin.database.activeConnection.prepareStatement("SELECT * FROM `block_logs` WHERE uuid=?");
            statement.setString(1, user.getUniqueId().toString());
            async(statement, new  LookupCallback() {
                @Override
                public void success(List<LookupRecord> records) {
                    List<Text> messages = records.stream().map(result -> createText(result)).collect(Collectors.toList());
                    Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
                    if (paginationService.isPresent()) {
                        paginationService.get().builder()
                                .contents(messages)
                                .linesPerPage(10)
                                .sendTo(caller);
                    } else
                        messages.forEach(caller::sendMessage);
                }

                @Override
                public void empty() {
                    caller.sendMessage(Text.of("No results found!"));
                }

                @Override
                public void error(Exception e) {
                    caller.sendMessage(Text.of("An error occurred, consult console."));
                }
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
            caller.sendMessage(Text.of("An error occurred, consult console."));
        }
    }

    public void lookup(CommandSource caller, Location<World> location) {
        try {
            PreparedStatement statement = plugin.database.activeConnection.prepareStatement("SELECT * FROM `block_logs` WHERE location=?");
            statement.setString(1, SerializableLocation.serialize(location));
            async(statement, new  LookupCallback() {
                @Override
                public void success(List<LookupRecord> records) {
                    List<Text> messages = records.stream().map(result -> createText(result)).collect(Collectors.toList());
                    Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
                    if (paginationService.isPresent()) {
                        paginationService.get().builder()
                                .contents(messages)
                                .linesPerPage(10)
                                .sendTo(caller);
                    } else
                        messages.forEach(caller::sendMessage);
                }

                @Override
                public void empty() {
                    caller.sendMessage(Text.of("No results found!"));
                }

                @Override
                public void error(Exception e) {
                    caller.sendMessage(Text.of("An error occurred, consult console."));
                }
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
            caller.sendMessage(Text.of("An error occurred, consult console."));
        }
    }

    private Text createText(LookupRecord record) {
        Text.Builder builder = Text.builder().append(Text.of(
               TextColors.GRAY, "[",  TextColors.YELLOW, record.getEventType(), TextColors.GRAY, "]", " "
        ));

        plugin.storageService.get(record.getUuid()).ifPresent(user -> builder.append(Text.of(
                TextColors.DARK_AQUA, user.getName(), " "
        )));
        builder.append(Text.of(
           TextColors.RED, record.getEventVerb(), TextColors.GRAY
        ));
        builder.append(Text.of(
           TextColors.GRAY, " a ", TextColors.DARK_AQUA, record.getBlockName(), TextColors.GRAY, " "
        ));
        builder.append(Text.of(
           TextColors.GRAY, "(", TextColors.DARK_AQUA, record.getSerializedLocation(), TextColors.GRAY, ")"
        ));

        return builder.build();
    }

    private void async(PreparedStatement statement, LookupCallback callback) {
        plugin.game.getScheduler().createTaskBuilder().async().execute(() -> {
            try {
                List<LookupRecord> records = new ArrayList<>();
                ResultSet set = statement.executeQuery();
                while(set.next()) {
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    String eventType = set.getString("event");
                    String sLocation = set.getString("location");
                    String blockName = set.getString("block");

                    records.add(new LookupRecord(uuid, sLocation, eventType, blockName));
                }

                if (!records.isEmpty())
                    callback.success(records);
                else
                    callback.empty();
            } catch (SQLException e) {
                e.printStackTrace();
                callback.error(e);
            }
        }).submit(plugin);
    }

    public class LookupCallback {
        public void success(List<LookupRecord> records) {}
        public void empty() {}
        public void error(Exception e) {}
    }

}
