package me.niko.kingdom.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {
	//got from the net cuz lazy to write
    public static String fromLocToString(Location loc) {
        StringBuilder builder = new StringBuilder();

        if (loc == null) return "unset";

        builder.append("x:").append(loc.getX()).append("|");
        builder.append("y:").append(loc.getY()).append("|");
        builder.append("z:").append(loc.getZ()).append("|");
        builder.append("world:").append(loc.getWorld().getName()).append("|");
        builder.append("yaw:").append(loc.getYaw()).append("|");
        builder.append("pitch:").append(loc.getPitch());

        return builder.toString();
    }

    public static Location fromStrToLocation(String s) {
        if (s == null || s.equals("unset") || s.equals("")) return null;

        String[] data = s.split("\\|");
        double x = Double.parseDouble(data[0].replace("x:", ""));
        double y = Double.parseDouble(data[1].replace("y:", ""));
        double z = Double.parseDouble(data[2].replace("z:", ""));
        World world = Bukkit.getWorld(data[3].replace("world:", ""));
        Float yaw = Float.parseFloat(data[4].replace("yaw:", ""));
        Float pitch = Float.parseFloat(data[5].replace("pitch:", ""));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static boolean isSameLocation(Location loc1, Location loc2) {
        return loc1 != null && loc2 != null && loc1.equals(loc2);
    }
}
