package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSave extends AsyncCommand {

    CommandSave() {
        super("artmap.artist", "/artmap save <title>", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        final String title = args[1];

        final Player player = (Player) sender;

        if (!new TitleFilter(title).check()) {
            msg.message = Lang.BAD_TITLE.get();
            return;
        }

        MapArt art = ArtMap.getArtDatabase().getArtwork(title);

        if (art != null) {
            msg.message = Lang.TITLE_USED.get();
            return;
        }

        if (!ArtMap.getArtistHandler().containsPlayer(player)) {
            Lang.NOT_RIDING_EASEL.send(player);
            return;
        }


        ArtMap.getTaskManager().SYNC.run(() -> {
            Easel easel = null;
            easel = ArtMap.getArtistHandler().getEasel(player);

            if (easel == null) {
                Lang.NOT_RIDING_EASEL.send(player);
                return;
            }
            ArtMap.getArtistHandler().removePlayer(player);

            MapArt art1 = new MapArt(easel.getItem().getDurability(), title, player);
            art1.saveArtwork();

            easel.getFrame().setItem(new ItemStack(Material.AIR));
            ItemStack leftOver = player.getInventory().addItem(art1.getMapItem()).get(0);

            if (leftOver != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
            }
            SoundCompat.ENTITY_EXPERIENCE_ORB_TOUCH.play(player, 1, 0);
            easel.playEffect(Effect.HAPPY_VILLAGER);
            player.sendMessage(String.format(Lang.SAVE_SUCCESS.get(), title));
        });
    }
}
