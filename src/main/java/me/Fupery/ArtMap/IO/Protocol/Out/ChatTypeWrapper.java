package me.Fupery.ArtMap.IO.Protocol.Out;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.Utils.Reflection;

import java.lang.reflect.Field;

import static me.Fupery.ArtMap.Utils.VersionHandler.BukkitVersion.v1_12;

public class ChatTypeWrapper {
    boolean legacy;
    private Class chatTypeClass;
    private Object chatType;

    public ChatTypeWrapper() {
        legacy = ArtMap.getBukkitVersion().getVersion().isLessThan(v1_12);
        if (legacy) {
            chatTypeClass = byte.class;
            chatType = (byte) 2;
        } else {
            String chatTypeClassName = Reflection.NMS + ".ChatMessageType";
            try {
                chatTypeClass = Class.forName(chatTypeClassName);
                Field chatTypeField = chatTypeClass.getDeclaredField("GAME_INFO");
                chatType = chatTypeField.get(null);
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
                ErrorLogger.log(e);
            }
        }
    }

    public Class getChatTypeClass() {
        return chatTypeClass;
    }

    public Object getChatType() {
        return chatType;
    }
}
