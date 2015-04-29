package me.Fupery.Artiste;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import me.Fupery.Artiste.IO.*;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.Tasks.EasyDraw;
import me.Fupery.Artiste.Command.Error;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class StartClass extends JavaPlugin {

	private Logger log = this.getLogger();
	public static boolean economyOn;
	public static Economy econ = null;
	public static FileConfiguration config;

	public static HashMap<String, AbstractMapArt> artList;
	public static HashMap<UUID, Artist> artistList;
	
	public static Canvas canvas;
	public static String[] idList;
	public static StartClass plugin;
	public static List<Short> recyclingBin;

	@Override
	public void onEnable() {

		plugin = this;

		PluginManager pluginManager = getServer().getPluginManager();
		
		this.getCommand("artmap").setExecutor(new CommandListener());
		
		pluginManager.registerEvents((new EasyDraw()), this);

		Load.setupRegistry(this, log);
		
		this.saveDefaultConfig();
		
		config = getConfig();

		if (!setupEconomy()) {
			
			log.info(Error.noEcon);
			economyOn = false;
			
		} else
			economyOn = true;
	}

	@Override
	public void onDisable() {

		// override();

		if (canvas != null) {
			Canvas c = canvas;

			if (c != null && c.getOwner() != null) {
				
				c.clear(c.getOwner());
				
				Bukkit.getScheduler().cancelTasks(this);
			}
		}
		save(new File(getDataFolder(), "MapArt.dat"), artList);
		
		save(new File(getDataFolder(), "Artist.dat"), artistList);
		
		save(new File(getDataFolder(), "Artiste.dat"), canvas, idList);
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

	// ☠

	// had these suckers in their own class but onDisable couldn't seem to find
	// them
	public static void save(File saveFile, Object object) {
		
		try {
			if (!saveFile.exists())
				saveFile.createNewFile();

			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(saveFile));

			out.writeObject((Object) object);

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void save(File saveFile, Canvas c, String[] id) {

		try {
			if (!saveFile.exists())
				saveFile.createNewFile();

			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(saveFile));

			out.writeObject((Object) c);
			out.writeObject((Object) id);

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
