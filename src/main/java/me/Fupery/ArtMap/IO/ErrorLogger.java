package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLogger {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String log = "error.log";
    private static WeakReference<File> dataFolder = null;

    public static void log(Throwable throwable) {
        File dataFolder = getDataFolder();
        ArtMap.getTaskManager().ASYNC.run(() -> {
            File file = new File(dataFolder, log);
            if (!file.exists()) {
                try {
                    if (!(file.createNewFile())) {
                        throwable.printStackTrace();
                        return;
                    }
                } catch (IOException e) {
                    throwable.printStackTrace();
                    return;
                }
            }
            FileWriter fileWriter;
            PrintWriter logger;
            try {
                fileWriter = new FileWriter(file, true);
                logger = new PrintWriter(fileWriter);
                logger.println(dateFormat.format(new Date()));
                logger.println("[VERSION]:" + Bukkit.getServer().getVersion() + ", " + ArtMap.instance().toString());
                logger.println("---------------------[SERVER]---------------------");
                String loadedPlugins = "Loaded Plugins: [";
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    loadedPlugins += plugin.toString() + ", ";
                }
                loadedPlugins += "]";
                logger.println(loadedPlugins);
                logger.println(ArtMap.getCompatManager().toString());
                logger.println("--------------------[STACKTRACE]---------------------");
                logger.println(throwable.getMessage());
                for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                    logger.println(stackTraceElement.toString());
                }
                logger.println();
                logger.println();
                logger.println();
                logger.flush();
                logger.close();
            } catch (IOException e) {
                throwable.printStackTrace();
                return;
            }
        });
    }

    private static File getDataFolder() {
        if (dataFolder == null || dataFolder.get() == null) {
            dataFolder = new WeakReference<>(ArtMap.instance().getDataFolder());
        }
        return dataFolder.get();
    }
}
