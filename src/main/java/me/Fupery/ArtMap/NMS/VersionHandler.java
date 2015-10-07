package me.Fupery.ArtMap.NMS;

import me.Fupery.ArtMap.ArtMap;

public class VersionHandler {
    private ArtMap plugin;
    private String version;

    public VersionHandler(ArtMap plugin) {
        this.plugin = plugin;
        String serverPackage = plugin.getServer().getClass().getPackage().getName();
        version = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
    }
    public NMSInterface getNMSInterface() {
        String classPath = this.getClass().getPackage().getName() + "." + version;
        plugin.getLogger().fine("Searching for interfaces supporting " + version);

        try {
            Class<?> clazz = Class.forName(classPath);
            return (NMSInterface) clazz.newInstance();

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return new InvalidVersion(version);
        }
    }
}
