package me.Fupery.Artiste.Command.Utils;

import org.bukkit.Bukkit;

import me.Fupery.Artiste.Command.AbstractCommand;

public abstract class TestCommand extends AbstractCommand {

	public TestCommand() {
		super();
	}

	public static void eval() {
		Bukkit.getLogger().info("TestCommand conditions called");
	}

}
