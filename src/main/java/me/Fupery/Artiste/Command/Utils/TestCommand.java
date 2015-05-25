package me.Fupery.Artiste.Command.Utils;

import org.bukkit.Bukkit;

public abstract class TestCommand extends AbstractCommand {

	public TestCommand() {
		super();
	}

	public static void eval() {
		Bukkit.getLogger().info("TestCommand conditions called");
	}

}
