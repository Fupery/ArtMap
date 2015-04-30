package me.Fupery.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagShort;
import com.evilco.mc.nbt.tag.TagString;

import me.Fupery.Artiste.MapArt.*;

public class MapSave {

	private AbstractMapArt art;

	public MapSave(AbstractMapArt art) {
		this.art = art;
	}

	public void privateArtworkSave(File saveFile) throws IOException {
		
		PrivateMap a = (PrivateMap) art;
		byte[] map = serialize(art.getMap());
		byte queued = (a.isQueued()) ? (byte) 1 : (byte) 0;
		byte denied = (a.isQueued()) ? (byte) 1 : (byte) 0;

		TagCompound compound = new TagCompound(a.getTitle());
		
		compound.setTag(new TagString("UUID", a.getTitle()));
		compound.setTag(new TagShort("mapId", a.getMapId()));
		compound.setTag(new TagInteger("mapSize", a.getMapSize()));
		compound.setTag(new TagByteArray("map", map));
		compound.setTag(new TagByte("queued", queued));
		compound.setTag(new TagByte("denied", denied));

		// create output stream
		FileOutputStream outputStream = new FileOutputStream(saveFile);
		NbtOutputStream nbtOutputStream = new NbtOutputStream(outputStream);

		// write data
		nbtOutputStream.write(compound);

		nbtOutputStream.flush();
		nbtOutputStream.close();
	}

	public static void save(File saveFile, Object object) {

		try {
			if (!saveFile.exists())
				saveFile.createNewFile();

			ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
					saveFile)));

			out.writeObject(object);

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	public static Object deserialize(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}
}
