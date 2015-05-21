package me.Fupery.Artiste;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import me.Fupery.Artiste.IO.*;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.Tasks.CraftCancelling;
import me.Fupery.Artiste.Tasks.EasyDraw;
import me.Fupery.Artiste.Tasks.OnLogout;
import me.Fupery.Artiste.Command.Utils.Error;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Artiste extends JavaPlugin {

	public static boolean economyOn;
	public static Economy econ = null;
	public static FileConfiguration config;

	public static HashMap<String, Artwork> artList;
	public static HashMap<UUID, Artist> artistList;

	public static Canvas canvas;
	public static Artiste plugin;

	public static BukkitRunnable claimTimer;

	@Override
	public void onEnable() {

		plugin = this;

		PluginManager pluginManager = getServer().getPluginManager();

		this.getCommand("artmap").setExecutor(new CommandListener());

		pluginManager.registerEvents((new EasyDraw()), this);
		pluginManager.registerEvents(new OnLogout(), this);
		pluginManager.registerEvents(new CraftCancelling(), this);

		Load.setupRegistry(this, getLogger());

		this.saveDefaultConfig();

		config = getConfig();

		if (!setupEconomy()) {

			getLogger().info(Error.noEcon);
			economyOn = false;

		} else
			economyOn = true;
	}

	@Override
	public void onDisable() {

		if (Artiste.claimTimer != null)
			Artiste.claimTimer.cancel();

		if (canvas != null) {

			if (canvas.getOwner() != null) {

				canvas.clear(canvas.getOwner());

			}
		}
		save(new File(getDataFolder(), "Artiste.dat"));

		Bukkit.getScheduler().cancelTasks(this);
	}

	private boolean setupEconomy() {

		if (getServer().getPluginManager().getPlugin("Vault") == null) {

			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {

			return false;
		}
		econ = rsp.getProvider();

		return econ != null;
	}

	public static void save(File saveFile) {

		try {
			if (!saveFile.exists())
				saveFile.createNewFile();

			ObjectOutputStream out = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(saveFile)));

			out.writeObject((Object) Artiste.canvas);
			out.writeObject((Object) Artiste.artistList);
			out.writeObject((Object) Artiste.artList);

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
