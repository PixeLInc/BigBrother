package io.pixelinc.bigbrother.util;

import io.pixelinc.bigbrother.database.SerializableLocation;
import org.spongepowered.api.world.Location;

import javax.annotation.Nullable;
import java.util.UUID;

public class LookupRecord {

    private UUID uuid;

    private String sLocation;

    private String eventType;

    private String block;

    public LookupRecord(UUID uuid, String sLocation, String eventType, String block) {
        this.uuid = uuid;
        this.sLocation = sLocation;
        this.eventType = eventType;
        this.block = block;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventVerb() {
        switch (this.eventType.toLowerCase()) {
            case "place":
                return "placed";
            case "break":
                return "broke";
            case "modify":
                return "modified";
            case "invopen":
                return "opened";
            default:
                return "<error>";
        }
    }

    public String getSerializedLocation() {
        return sLocation;
    }

    public Location getLocation() {
        return SerializableLocation.unserialize(this.sLocation);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getBlockName() {
         return this.block;
    }
}
