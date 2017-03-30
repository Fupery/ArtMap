package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.Bukkit.isPrimaryThread;

public class TaskManager {
    private final ArtMap plugin;
    public final TaskScheduler SYNC = new TaskScheduler() {
        @Override
        public BukkitTask run(Runnable runnable) {
            return getScheduler().runTask(plugin, runnable);
        }

        @Override
        public BukkitTask runLater(Runnable runnable, int delay) {
            return getScheduler().runTaskLater(plugin, runnable, delay);
        }

        @Override
        public BukkitTask runTimer(Runnable runnable, int startDelay, int period) {
            return getScheduler().runTaskTimer(plugin, runnable, startDelay, period);
        }
    };
    public final TaskScheduler ASYNC = new TaskScheduler() {
        @Override
        public BukkitTask run(Runnable runnable) {
            return getScheduler().runTaskAsynchronously(plugin, runnable);
        }

        @Override
        public BukkitTask runLater(Runnable runnable, int delay) {
            return getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        }

        @Override
        public BukkitTask runTimer(Runnable runnable, int startDelay, int period) {
            return getScheduler().runTaskTimerAsynchronously(plugin, runnable, startDelay, period);
        }
    };

    public TaskManager(ArtMap plugin) {
        this.plugin = plugin;
    }

    public TaskHandler getTaskHandler(BukkitRunnable runnable) {
        return new TaskHandler(runnable);
    }

    public void runSafely(Runnable runnable) {
        if (!isPrimaryThread()) {
            SYNC.run(runnable);
        } else {
            runnable.run();
        }
    }

    public interface TaskScheduler {
        BukkitTask run(Runnable runnable);

        BukkitTask runLater(Runnable runnable, int delay);

        BukkitTask runTimer(Runnable runnable, int startDelay, int period);
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
