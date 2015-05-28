package me.Fupery.Artiste.Command.Utils;

public enum CommandType {
	HELP, INFO, DEFINE, REMOVE, APPROVE, DENY, 
	BAN, UNBAN, CLAIM, UNCLAIM, ADDMEMBER, 
	DELMEMBER, RESET, SAVE, DELETE, EDIT, BUY, 
	PUBLISH, LIST, INVALID;
	
	
	public static CommandType getType(String cmd) {
		for (CommandType type : CommandType.values()) {
			if (type.name().compareTo(cmd.toUpperCase()) == 0) {
				return type;
			}
		}
		return CommandType.INVALID;
	}
}


