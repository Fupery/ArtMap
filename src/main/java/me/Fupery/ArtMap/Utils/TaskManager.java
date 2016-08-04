package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
        public void runTimer(Runnable runnable, int startDelay, int period) {
            Bukkit.getScheduler().runTaskTimer(plugin, runnable, startDelay, period);
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
        public void runTimer(Runnable runnable, int startDelay, int period) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, startDelay, period);
        }
    };

    public TaskManager(ArtMap plugin) {
        this.plugin = plugin;
    }

    public TaskHandler getTaskHandler(BukkitRunnable runnable) {
        return new TaskHandler(runnable);
    }

    public interface TaskScheduler {
        void run(Runnable runnable);

        void runLater(Runnable runnable, int delay);

        void runTimer(Runnable runnable, int startDelay, int period);
    }

    public class TaskHandler {
        private BukkitRunnable runnable;

        private TaskHandler(BukkitRunnable runnable) {
            this.runnable = runnable;
        }

        public void run(boolean async) {
            if (async) runnable.runTaskAsynchronously(plugin);
            else runnable.runTask(plugin);
        }

        public void runLater(boolean async, int delay) {
            if (async) runnable.runTaskLaterAsynchronously(plugin, delay);
            else runnable.runTaskLater(plugin, delay);
        }

        public void runTimer(boolean async, int startDelay, int period) {
            if (async) runnable.runTaskTimerAsynchronously(plugin, startDelay, period);
            else runnable.runTaskTimer(plugin, startDelay, period);
        }
    }
}
