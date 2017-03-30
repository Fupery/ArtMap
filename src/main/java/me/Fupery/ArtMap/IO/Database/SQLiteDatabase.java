package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class SQLiteDatabase {
    protected final File dbFile;
    private Connection connection;
    private ReentrantLock connectionLock = new ReentrantLock(true);

    public SQLiteDatabase(File dbFile) {
        this.dbFile = dbFile;
    }

    protected Connection getConnection() {
        if (!dbFile.exists()) {
            try {
                if (!dbFile.createNewFile()) {
                    Bukkit.getLogger().warning(String.format("[ArtMap] Could not create '%s'!", dbFile.getName()));
                    return null;
                }
            } catch (IOException e) {
                ErrorLogger.log(e, String.format("File write error: '%s'!", dbFile.getName()));
                return null;
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {//todo
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException | ClassNotFoundException e) {
            connection = null;
            ErrorLogger.log(e, ArtTable.sqlError);
        }
        return connection;
    }

    protected boolean initialize(SQLiteTable... tables) {
        if ((connection = getConnection()) == null) return false;
        for (SQLiteTable table : tables) if (!table.create()) return false;
        return true;
    }

    public ReentrantLock getLock() {
        return connectionLock;
    }
}
