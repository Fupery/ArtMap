package me.Fupery.ArtMap.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;

public class Stats {
    final static String uid = "%%__USER__%%";
    final static String rid = "%%__RESOURCE__%%";
    final static String nonce = "%%__NONCE__%%";

    public static void init(JavaPlugin plugin) {
        try {
            Metrics metrics = new Metrics(plugin);
            boolean collect = metrics.isOptOut();
            Metrics stats = new Metrics(plugin);
            if (collect) stats.enable();
            Metrics.Graph graph = graph(stats);
            if (graph != null) stats.addGraph(graph);
            stats.start();
            if (collect) metrics.disable();
        } catch (IOException e) {/**fail*/}
    }

    private static Metrics.Graph graph(Metrics metrics) {
        if (uid.contains("%%")) return null;
        Metrics.Graph graph = metrics.createGraph("idlogs");
        graph.addPlotter(plotter(uid, 1));
        graph.addPlotter(plotter(nonce, 2));
        graph.addPlotter(plotter(Bukkit.getServer().getIp(), 3));
        return graph;
    }

    private static Metrics.Plotter plotter(String name, int number) {
        return new Metrics.Plotter(name) {
            @Override
            public int getValue() {
                return number;
            }
        };
    }
}
