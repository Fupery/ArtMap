package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.Bukkit;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BukkitGetter<T> {

    private Callable<T> callable;

    public BukkitGetter(Callable<T> callable) {
        this.callable = callable;
    }

    public T get() {
        if (!Bukkit.isPrimaryThread()) {
            final BukkitFuture<T> future = new BukkitFuture<>(callable);
            future.run();
            synchronized (future.getLock()) {
                while (!future.isReady()) {
                    try {
                        future.getLock().wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            return future.get();

        } else {
            try {
                return callable.call();
            } catch (Exception e) {
                ErrorLogger.log(e, "Error in BukkitGetter:");
                return null;
            }
        }
    }

    private class BukkitFuture<t> {
        private final AtomicBoolean isReady;
        private final AtomicReference<t> reference;
        private final Object lock;
        private final Callable<t> callable;

        BukkitFuture(Callable<t> callable) {
            this.isReady = new AtomicBoolean(false);
            this.reference = new AtomicReference<>(null);
            this.lock = new Object();
            this.callable = callable;
        }

        void run() {
            ArtMap.getTaskManager().SYNC.run(() -> {
                synchronized (lock) {
                    try {
                        reference.set(callable.call());
                    } catch (Exception e) {
                        ErrorLogger.log(e, "Error in BukkitGetter:");
                    }
                    lock.notify();
                }
            });
        }

        t get() {
            return reference.get();
        }

        Object getLock() {
            return lock;
        }

        boolean isReady() {
            return isReady.get();
        }
    }
}
