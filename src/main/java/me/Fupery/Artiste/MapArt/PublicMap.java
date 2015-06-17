package me.Fupery.Artiste.MapArt;


import me.Fupery.Artiste.Artiste;

public class PublicMap extends Artwork{

	private static final long serialVersionUID = 6321856941144893696L;
	private int buys;

	public PublicMap(PrivateMap pm) {
			mapSize = pm.getMapSize();
			artist = pm.getArtist();
			mapId = pm.getMapId();
			title = pm.getTitle();
			category = pm.getCategory();
			buys = 0;
			Artiste.artList.put(title.toLowerCase(), this);
	}
	public int getBuys() {
		return buys;
	}
	public void incrementBuys() {
		buys ++;
	}
}
