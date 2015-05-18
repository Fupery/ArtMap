package me.Fupery.Artiste.Command.Utils;

public class Test extends TestCommand {
	
	public void initialize(){
		success = "test";
	}

	@Override
	public boolean run() {
		return false;
	}
}
