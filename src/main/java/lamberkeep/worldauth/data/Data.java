package lamberkeep.worldauth.data;

import lamberkeep.worldauth.WorldAuth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

public class Data {

    private final WorldAuth plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public Data(WorldAuth plugin) {
        this.plugin = plugin;
        // saves/initializes the config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) configFile = new File(plugin.getDataFolder(), "data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = plugin.getResource("data.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null) reloadConfig();
        return dataConfig;
    }

    public void saveConfig() {
        if (dataConfig == null || configFile == null) return;
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to" + configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null) configFile = new File(plugin.getDataFolder(), "data.yml");
        if (!configFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
    }

    // # DATA MANAGER
    public void saveData(Player p) {
        Location loc = p.getLocation();

        String config = p.getName() + ".location.";

        getConfig().set(config + "World", Objects.requireNonNull(loc.getWorld()).getName());
        getConfig().set(config + "X", loc.getX());
        getConfig().set(config + "Y", loc.getY());
        getConfig().set(config + "Z", loc.getZ());
        getConfig().set(config + "Yaw", loc.getYaw());
        getConfig().set(config + "Pitch", loc.getPitch());

        config = p.getName() + ".inventory.";

        getConfig().set(config + "Health", p.getHealth());
        getConfig().set(config + "Food", p.getFoodLevel());

        for (int i = 0; i < p.getInventory().getArmorContents().length; i++) {
            getConfig().set(config + "Armor." + i, p.getInventory().getArmorContents()[i]);
        }

        for (int i = 0; i < p.getInventory().getContents().length; i++) {
            getConfig().set(config + "Content." + i, p.getInventory().getContents()[i]);
        }

        // drop
        p.getInventory().clear();
        p.setHealth(20);
        p.setFoodLevel(20);

        config = p.getName() + ".exp.";

        getConfig().set(config + "Level", p.getLevel());
        getConfig().set(config + "Progress", p.getExp());

        saveConfig();
    }

    public void loadData(Player p) {
        String config = p.getName() + ".location.";

        Objects.requireNonNull(p.getPlayer()).teleport(new Location(Bukkit.getServer().getWorld(
                Objects.requireNonNull(getConfig().getString(config + "World"))),
                getConfig().getDouble(config + "X"),
                getConfig().getDouble(config + "Y"),
                getConfig().getDouble(config + "Z"),
                (float) getConfig().getDouble(config + "Yaw"),
                (float) getConfig().getDouble(config + "Pitch")));

        config = p.getName() + ".inventory.";

        p.setHealth(getConfig().getDouble(config + "Health"));
        p.setFoodLevel(getConfig().getInt(config + "Food"));

        p.getInventory().clear();

        ItemStack[] content = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            if(getConfig().getItemStack(config + "Armor." + i) != null) {
                content[i] = getConfig().getItemStack(config + "Armor." + i);
            }
        }
        p.getInventory().setArmorContents(content);

        content = new ItemStack[41];
        for (int i = 0; i < 41; i++) {
            content[i] = getConfig().getItemStack(config + "Content." + i);
        }
        p.getInventory().setContents(content);

        config = p.getName() + ".exp.";

        p.setLevel(getConfig().getInt(config + "Level"));
        p.setExp((float) getConfig().getDouble(config + "Progress"));
    }

}