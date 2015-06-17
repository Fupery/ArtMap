package me.Fupery.Artiste.MapArt;

public enum ValidMapType {
	PRIVATE, QUEUED, PUBLIC, BUFFER, TEMPLATE;
	
	public static ValidMapType getType(String t) {
		for (ValidMapType type : ValidMapType.values()) {
			if (type.name().compareTo(t.toUpperCase()) == 0) {
				return type;
			}
		}
		return null;
	}
}
