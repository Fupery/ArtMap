package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;

public class TaskManager {
    private final ArtMap plugin;
    public final TaskScheduler SYNC = new TaskScheduler() {
        @Override
        public void run(Runnable runnable) {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }

        @Override
        public void runLater(Runnable runnable, int delay) {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }

        @Override
        public void runTimer(Runnable runnable, int startDelay, int delay) {
            Bukkit.getScheduler().runTaskTimer(plugin, runnable, startDelay, delay);
        }
    };
    public final TaskScheduler ASYNC = new TaskScheduler() {
        @Override
        public void run(Runnable runnable) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }

        @Override
        public void runLater(Runnable runnable, int delay) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        }

        @Override
        public void runTimer(Runnable runnable, int startDelay, int delay) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, startDelay, delay);
        }
    };

    public TaskManager(ArtMap plugin) {
        this.plugin = plugin;
    }

    public interface TaskScheduler {
        void run(Runnable runnable);

        void runLater(Runnable runnable, int delay);

        void runTimer(Runnable runnable, int startDelay, int delay);
    }
}
