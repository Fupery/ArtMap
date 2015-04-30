package me.Fupery.test;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.Command.AbstractCommand;

public class Debug {

	AbstractCommand cmd;
	Logger log;
	Field[] fields;

	public Debug(CommandListener listener) {
		
		cmd = listener.getCmd();
		log = Bukkit.getLogger();

		fields = cmd.getClass().getDeclaredFields();
	}

	public void getStatus() {
		Object o;

		for (Field f : fields) {

			try {

				o = f.get(cmd);

				log.info(o.toString());

			} catch (IllegalArgumentException | IllegalAccessException e) {

				log.info(e.getMessage());
			}
		}
	}
}
