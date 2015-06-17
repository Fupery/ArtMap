package me.Fupery.Artiste.MapArt;

public enum MapCategory {
	ARTWORK, SIGN, UNCATEGORIZED;

	public static MapCategory getCategory(String category) {
		for (MapCategory type : MapCategory.values()) {
			if (type.name().compareTo(category.toUpperCase()) == 0) {
				return type;
			}
		}
		return null;
	}
}
