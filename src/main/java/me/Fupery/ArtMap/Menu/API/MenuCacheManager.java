package me.Fupery.ArtMap.Menu.API;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

final class MenuCacheManager {

    private final ConcurrentHashMap<MenuTemplate, CacheableMenu> cacheMap = new ConcurrentHashMap<>();
    private final Object cleanupLock = new Object();
    private CleanupThread cleanupThread = null;

    CacheableMenu cacheMenu(MenuTemplate template) {
        CacheableMenu cachedMenu; //30 seconds
        switch (template.getPattern()) {
            case JUST_IN_TIME:
                cachedMenu = new CacheableMenu(template, 0); //Not cached
                break;
            case CACHED_WEAKLY:
                cachedMenu = new CacheableMenu(template, 20000);//20 sec
                break;
            case CACHED_STRONGLY:
                cachedMenu = new CacheableMenu(template, 120000);//2 min
                break;
            case STATIC:
                cachedMenu = new CacheableMenu(template); //Never expires
                cacheMap.put(template, cachedMenu);
                return cachedMenu;
            default:
                return new CacheableMenu(template);
        }
        cacheMap.put(template, cachedMenu);
        synchronized (cleanupLock) {
            if (cleanupThread == null) cleanupThread = new CleanupThread();
        }
        return cachedMenu;
    }

    CacheableMenu getMenu(MenuTemplate template) {
        CacheableMenu cachedMenu = cacheMap.get(template);
        if (cachedMenu == null) return null;
        if (cachedMenu.isExpired()) cacheMap.remove(template);
        return cachedMenu;
    }

    void invalidate(MenuTemplate template) {
        if (cacheMap.containsKey(template)) cacheMap.get(template).invalidate();
    }

    void empty() {
        synchronized (cleanupLock) {
            cleanupThread.cancel();
        }
        cacheMap.clear();
    }

    boolean contains(MenuTemplate template) {
        return cacheMap.containsKey(template);
    }

    private class CleanupThread extends BukkitRunnable {
        CleanupThread() {
            this.runTaskTimerAsynchronously(ArtMap.instance(), 300, 300); //15 seconds
        }// TODO: 5/08/2016 new task scheduling

        @Override
        public void run() {
            if (cacheMap.isEmpty()) {
                synchronized (cleanupLock) {
                    cancel();
                    cleanupThread = null;
                }
            }
            for (MenuTemplate key : cacheMap.keySet()) {
                CacheableMenu value = cacheMap.get(key);
                if (value.isExpired()) {
                    cacheMap.remove(key);
                }
            }
        }
    }
}
