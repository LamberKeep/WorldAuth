// CodedRed's class.

package com.myvnc.exo.worldauth;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class DataManager {

    private final WorldAuth plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager(WorldAuth plugin){
        this.plugin = plugin;
        // saves/initializes the config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null) this.configFile = new File(this.plugin.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource("data.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null) reloadConfig();
        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null) return;
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to" + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null) this.configFile = new File(this.plugin.getDataFolder(), "data.yml");
        if (!this.configFile.exists()) {
            this.plugin.saveResource("data.yml", false);
        }
    }

    public void saveInventory(Player p) throws IOException {
        File f = new File(plugin.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory.armor", p.getInventory().getArmorContents());
        c.set("inventory.content", p.getInventory().getContents());
        c.save(f);
    }

    @SuppressWarnings("unchecked")
    public void restoreInventory(Player p) {
        File f = new File(plugin.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        ItemStack[] content = ((List<ItemStack>) Objects.requireNonNull(c.get("inventory.armor"))).toArray(new ItemStack[0]);
        p.getInventory().setArmorContents(content);
        content = ((List<ItemStack>) Objects.requireNonNull(c.get("inventory.content"))).toArray(new ItemStack[0]);
        p.getInventory().setContents(content);
    }

}
