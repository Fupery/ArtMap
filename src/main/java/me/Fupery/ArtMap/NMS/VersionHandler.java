package me.Fupery.ArtMap.NMS;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;

public class VersionHandler {
    private final String version;

    public VersionHandler() {
        String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
        version = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
    }

    public NMSInterface getNMSInterface() {
        String classPath = this.getClass().getPackage().getName() + "." + version;
        Bukkit.getLogger().fine(ArtMap.Lang.prefix + "Searching for interfaces supporting " + version);

        try {
            Class<?> clazz = Class.forName(classPath);
            return (NMSInterface) clazz.newInstance();

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return new InvalidVersion(version);
        }
    }
}
