package me.Fupery.Artiste.Tasks;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.IO.ArtIO;

import org.bukkit.scheduler.BukkitRunnable;

public class Archive extends BukkitRunnable {

	@Override
	public void run() {
		if(StartClass.artList == null)
			return;
		
		new ArtIO().archiveArtList();
	}

}
