package me.Fupery.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import me.Fupery.Artiste.Canvas;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagShort;

public class IDUpdate {
	
	private File file;
	private short count;
	private CommandSender sender;

	public IDUpdate(CommandSender sender) {
		this.file = getFile();
		this.sender = sender;
		ITag tag;
		try {
			tag = testHelloWorld(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tag = null;
			sender.sendMessage(e.getMessage());
		}
		if(tag != null){
			if(tag instanceof TagShort)
				count = ((TagShort) tag).getValue();
			else sender.sendMessage("cast failed");
		} else count = 0;
		
		Short c = (Short) count;
		
		sender.sendMessage(c.toString());
	}
	private File getFile(){
		
		Canvas c = Canvas.findCanvas();
		File wf = Bukkit.getWorld(c.worldname).getWorldFolder();		
		File dir = new File(wf.getAbsolutePath() + File.separator + "data");
		File f = null;
		for(String s : dir.list()){
			if(s.equalsIgnoreCase("idcounts.dat")){
				f = new File(dir.getAbsolutePath(), s);
				return f;
			}
		}
		return null;		
	}
	public ITag testHelloWorld (File f) throws IOException {
		// create NBT stream
		NbtInputStream inputStream = new NbtInputStream (new FileInputStream(f));
		
		// read NBT
		//ITag tag = inputStream.readTag ();
		TagShort tag = new TagShort(inputStream, false);
		short value = tag.getValue();
		
		inputStream.close();
		// verify result
		sender.sendMessage(tag.toString() + value);
		return tag;
	}
	public void test () throws IOException {
		// create a hello world tag structure
		TagCompound compound = new TagCompound ("");

		// add primitives
		compound.setTag (new TagShort ("shortTag", ((short) 42)));

		// create output stream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
		NbtOutputStream nbtOutputStream = new NbtOutputStream (outputStream);

		// write data
		nbtOutputStream.write (compound);
		nbtOutputStream.flush();
		nbtOutputStream.close();

		// create input stream
		ByteArrayInputStream inputStream = new ByteArrayInputStream (outputStream.toByteArray ());
		NbtInputStream nbtInputStream = new NbtInputStream (inputStream);

		// read data
		ITag tag = nbtInputStream.readTag ();
		nbtInputStream.close();

		// verify output
		sender.sendMessage(tag.toString());
	}
	public void increment(){
		
		sender.sendMessage(file.getAbsolutePath());
	}
	public void decrement(){
	}
}
