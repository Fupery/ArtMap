package me.Fupery.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Commands implements CommandExecutor {

    private Artiste plugin;

    public Commands(Artiste plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && args.length > 0) {

            if (args[0].equalsIgnoreCase("getMap")) {
                ItemStack canvas = new ItemCanvas();
                MapView map = Bukkit.createMap(((Player) sender).getWorld());
                canvas.setDurability(map.getId());
                map.addRenderer(new MapRenderer() {
                    @Override
                    public void render(MapView map, MapCanvas canvas, Player player) {
                        for (int x = 0; x < 64; x += 4) {
                            for (int y = 0; y < 64; y += 4) {
                                canvas.setPixel(x, y, MapPalette.matchColor(255, 0, 0));
                            }
                        }
                    }
                });
                ((Player) sender).setItemInHand(canvas);
            }
        }
        return true;
    }
}
