package me.Fupery.ArtMap.Preview;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

abstract class TimedPreview implements Preview {
    private Timeout timeout;

    @Override
    public boolean start(Player player) {
        ArtMap.getScheduler().getTaskHandler(timeout = new Timeout(player.getUniqueId())).runLater(false, 300);
        return true;
    }

    @Override
    public boolean end(Player player) {
        timeout.cancel();
        return true;
    }

    private class Timeout extends BukkitRunnable {

        private final UUID player;

        public Timeout(UUID player) {
            this.player = player;
        }

        @Override
        public void run() {
            ArtMap.getPreviewManager().endPreview(player);
        }
    }
}
