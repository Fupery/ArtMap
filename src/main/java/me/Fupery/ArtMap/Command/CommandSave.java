package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class CommandSave extends AsyncCommand {

    private TitleFilter filter;

    CommandSave() {
        super("artmap.artist", "/artmap save <title>", false);
        this.filter = new TitleFilter(Lang.Filter.ILLEGAL_EXPRESSIONS.get());
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        final String title = args[1];

        final Player player = (Player) sender;

        if (!filter.check(title)) {
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


        ArtMap.getScheduler().SYNC.run(() -> {
            Easel easel = null;
            easel = ArtMap.getArtistHandler().getEasel(player);

            if (easel == null) {
                Lang.NOT_RIDING_EASEL.send(player);
                return;
            }
            ArtMap.getArtistHandler().removePlayer(player);

            MapArt art1 = new MapArt(easel.getItem().getDurability(), title, player);
            ArtMap.getArtDatabase().saveArtwork(art1);

            easel.setItem(new ItemStack(Material.AIR));
            ItemUtils.giveItem(player, art1.getMapItem());
            SoundCompat.ENTITY_EXPERIENCE_ORB_TOUCH.play(player, 1, 0);
            easel.playEffect(Effect.HAPPY_VILLAGER);
            player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
        });
    }
}
