package io.pixelinc.bigbrother.database;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SerializableLocation {

    public static String serialize(Location<World> loc) {
        if (loc == null) return null;

        return loc.getExtent().getName()
                + ":"
                + loc.getX()
                + ":"
                + loc.getY()
                + ":"
                + loc.getZ();
    }

    public static Location unserialize(String s) {
        if (s == null || s.trim().equals(""))
            return null;

        final String[] data = s.split(":");
        World world = Sponge.getServer().getWorld(data[0]).orElse(null);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);

        return new Location(world, x, y, z);
    }

}
