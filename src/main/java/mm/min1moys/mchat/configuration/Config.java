package mm.min1moys.mchat.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.codehaus.plexus.util.FileUtils;
import mm.min1moys.mchat.mChat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Config {
    private static final File DATA_FOLDER = mChat.plugin.getDataFolder();
    public static FileConfiguration settings = mChat.plugin.getConfig();
    public static FileConfiguration messages = null;

    public static void initialize() {
        CodeSource src = mChat.class.getProtectionDomain().getCodeSource();
        try (ZipInputStream zip = new ZipInputStream(src.getLocation().openStream())) {
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) {
                    break;
                }

                if (!e.getName().contains(".yml") || e.getName().contains("plugin.yml") || e.getName().contains("config.yml")) {
                    continue;
                }

                File file = new File(DATA_FOLDER.getPath() + "/" + e.getName());

                if (!file.exists()) {
                    URL url = mChat.plugin.getClass().getResource("/" + e.getName());
                    FileUtils.copyURLToFile(url, file);
                }

                if (messages == null) {
                    if (Objects.equals(settings.getString("localisation"),
                            e.getName().replace(".yml", ""))) {
                        messages = YamlConfiguration.loadConfiguration(file);
                    }
                }
            }
        } catch (IOException e) {
            mChat.plugin.getLogger().warning("IOException on initializing Chatullo.");
        }
    }

    public static void reload() {
        mChat.plugin.reloadConfig();
        settings = mChat.plugin.getConfig();

        for (File file : Objects.requireNonNull(DATA_FOLDER.listFiles())) {
            if (Objects.equals(settings.getString("localisation"),
                    file.getName().replace(".yml", ""))) {
                messages = YamlConfiguration.loadConfiguration(file);
                break;
            }
        }
    }
}
