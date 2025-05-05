package plugin.slowRanks;

import Command.RangShopCommand;
import Listeners.RankShopListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class SlowRanks extends JavaPlugin {

    private static SlowRanks instance;
    private FileConfiguration config;



    @Override
    public void onEnable() {
        try {
            File logFile = new File(getDataFolder(), "debug.log");
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            FileWriter writer = new FileWriter(logFile, true);

            if (!VaultHook.setupEconomy()) {
                writer.write("Vault oder ein unterstütztes Wirtschaftssystem wurde nicht gefunden! Plugin wird deaktiviert.\n");
                writer.close();
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            writer.write("Vault erfolgreich verbunden!\n");

            instance = this;
            this.saveDefaultConfig();
            config = this.getConfig();

            if (config.contains("ranks")) {
                writer.write("Konfiguration erfolgreich geladen. Ränge gefunden: " + config.getConfigurationSection("ranks").getKeys(false) + "\n");
            } else {
                writer.write("Die Konfiguration enthält keine Ränge!\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getPluginManager().registerEvents(new RankShopListener(), this);
        this.getCommand("rangshop").setExecutor(new RangShopCommand());
    }

    @Override
    public void onDisable() {

    }

    public static SlowRanks getInstance() {
        return instance;
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }
}