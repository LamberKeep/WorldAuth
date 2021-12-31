package com.myvnc.exo.worldauth;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

public final class WorldAuth extends JavaPlugin implements Listener {

    HashMap<String, Long> session  = new HashMap<>(); // Sessions
    public DataManager data;

    @Override
    public void onEnable() {
        this.data = new DataManager(this);
        getServer().getPluginManager().registerEvents(this, this);
        new WorldCreator(Objects.requireNonNull(getConfig().getString("worlds.auth"))).createWorld();
        saveDefaultConfig();

        AuthMessages();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        Player p = e.getPlayer();
        String ip = Objects.requireNonNull(p.getAddress()).getAddress().toString();

        if (!session.containsKey(ip) && System.currentTimeMillis() >= session.getOrDefault(ip, (long) 0)) {
            p.teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))).getSpawnLocation());
            p.getInventory().clear();
            p.setGameMode(GameMode.ADVENTURE);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        String n = p.getName();
        Location loc = p.getLocation();

        e.setQuitMessage(null);

        if (p.getWorld() != Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))) {
            session.put(Objects.requireNonNull(p.getAddress()).getAddress().toString(), System.currentTimeMillis() + 3600000);

            // Save data location and inventory.
            data.getConfig().set("data."+n+".gamemode", p.getGameMode().toString());
            data.getConfig().set("data."+n+".location.World", Objects.requireNonNull(loc.getWorld()).getName());
            data.getConfig().set("data."+n+".location.X", loc.getX());
            data.getConfig().set("data."+n+".location.Y", loc.getY());
            data.getConfig().set("data."+n+".location.Z", loc.getZ());
            data.getConfig().set("data."+n+".location.Yaw", loc.getYaw());
            data.getConfig().set("data."+n+".location.Pitch", loc.getPitch());
            data.getConfig().set("data."+n+".inventory.Armor", p.getInventory().getArmorContents());
            data.getConfig().set("data."+n+".inventory.Content", p.getInventory().getContents());
            data.saveConfig();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        String n = p.getName();
        if (label.equalsIgnoreCase("login") || label.equalsIgnoreCase("l")) {
            if (p.getWorld() == Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))) {
                if (args.length == 0)
                    sender.sendMessage(ChatColor.RED + "Usage: /login <password>");
                else {
                    if (this.data.getConfig().getString("data."+n+".password") == null)
                        sender.sendMessage(ChatColor.RED + "Not registered yet, use /register <password>");
                    else {
                        if (args[0].equals(this.data.getConfig().getString("data."+n+".password"))) {
                            sender.sendMessage(ChatColor.GREEN + "Log in successfully.");

                            // location restore
                            Objects.requireNonNull(p.getPlayer()).teleport(new Location(Bukkit.getServer().getWorld(
                                    Objects.requireNonNull(data.getConfig().getString("data."+n+".location.World"))),
                                    data.getConfig().getDouble("data."+n+".location.X"),
                                    data.getConfig().getDouble("data."+n+".location.Y"),
                                    data.getConfig().getDouble("data."+n+".location.Z"),
                                    (float) data.getConfig().getDouble("data."+n+".location.Yaw"),
                                    (float) data.getConfig().getDouble("data."+n+".location.Pitch")));

                            // item restore
                            p.getInventory().setArmorContents((ItemStack[]) data.getConfig().get("inventory.Armor"));
                            p.getInventory().setContents((ItemStack[]) Objects.requireNonNull(data.getConfig().get("inventory.Content")));

                            Bukkit.broadcastMessage(ChatColor.YELLOW +p.getDisplayName()+ChatColor.YELLOW+" joined to the server");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Bad password, try again!");
                        }
                    }
                }
            } else
                sender.sendMessage(ChatColor.GREEN + "Already logged in!");
        }

        if (label.equalsIgnoreCase("register") || label.equalsIgnoreCase("reg")) {
            if (p.getWorld() == Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))) {
                if (args.length == 0)
                    sender.sendMessage(ChatColor.RED + "Usage: /register <password>");
                else {
                    if (this.data.getConfig().getString("data."+sender.getName()+".password") == null) {
                        data.getConfig().set("data." + sender.getName() + ".password", args[0]);
                        data.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Registered successfully.");
                        Objects.requireNonNull(p.getPlayer()).teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.hub-or-survival")))).getSpawnLocation());
                    } else
                        sender.sendMessage(ChatColor.RED + "Already registered, use /login <password>");
                }
            } else
                sender.sendMessage(ChatColor.GREEN + "Already registered!");
        }

        if (label.equalsIgnoreCase("logout")) {
            p.kickPlayer(Color.YELLOW+"Auth"+Color.GRAY+" | "+ChatColor.WHITE+"Successfully log out.");
            session.remove(Objects.requireNonNull(p.getAddress()).getAddress().toString());
        }

        if (label.equalsIgnoreCase("test") ) {
            p.setFlySpeed(0.0f);
        }

        return false;
    }

    public void AuthMessages() {
        new BukkitRunnable() {
            @Override
            public void run() {
            for(Player p : Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))).getPlayers())
                p.sendMessage(ChatColor.YELLOW + "Auth" + ChatColor.GRAY + " | " + ChatColor.WHITE + "Please log in.");
            }
        }.runTaskTimerAsynchronously(this, 0, 60);
    }


}