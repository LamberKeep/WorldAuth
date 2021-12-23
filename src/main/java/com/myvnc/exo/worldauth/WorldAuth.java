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

    HashMap<String, Long> mySession  = new HashMap<>(); // Session timer

    public DataManager data;
    public InventoryManager inv;


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

        Player p = e.getPlayer();
        String ip = Objects.requireNonNull(p.getAddress()).getAddress().toString();

        if (!mySession.containsKey(ip) && System.currentTimeMillis() >= mySession.getOrDefault(ip, (long) 0)) {
            e.setJoinMessage(null);
            p.teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))).getSpawnLocation());
            inv.saveInventory(p);
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

            mySession.put(Objects.requireNonNull(p.getAddress()).getAddress().toString(), System.currentTimeMillis() + 1800000);

            data.getConfig().set("data."+n+".gamemode" , p.getGameMode().toString());
            data.getConfig().set("data."+n+".location.World" , Objects.requireNonNull(loc.getWorld()).getName());
            data.getConfig().set("data."+n+".location.X" , loc.getX());
            data.getConfig().set("data."+n+".location.Y" , loc.getY());
            data.getConfig().set("data."+n+".location.Z" , loc.getZ());
            data.getConfig().set("data."+n+".location.Yaw" , loc.getYaw());
            data.getConfig().set("data."+n+".location.Pitch" , loc.getPitch());
            data.saveConfig();

        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;

        if (label.equalsIgnoreCase("login") || label.equalsIgnoreCase("l")) {
            String loc = "data."+sender.getName()+".location";
            if (p.getWorld() == Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))) {
                if (args.length == 0)
                    sender.sendMessage(ChatColor.RED + "Usage: /login <password>");
                else {
                    if (this.data.getConfig().getString("data." + p.getName() + ".password") == null)
                        sender.sendMessage(ChatColor.RED + "Not registered yet, use /register <password>");
                    else {
                        if (args[0].equals(this.data.getConfig().getString("data." + p.getName() + ".password"))) {
                            sender.sendMessage(ChatColor.GREEN + "Log in successfully.");
                            if (this.data.getConfig().getString(loc) != null)
                                Objects.requireNonNull(p.getPlayer()).teleport(new Location(Bukkit.getServer().getWorld(Objects.requireNonNull(data.getConfig().getString("data." + Objects.requireNonNull(p.getPlayer()).getName() + ".location.World"))), data.getConfig().getDouble("data."+ Objects.requireNonNull(p.getPlayer()).getName()+".location.X"), data.getConfig().getDouble(".location.Y"), data.getConfig().getDouble("data."+ Objects.requireNonNull(p.getPlayer()).getName()+".location.Z"), (float) data.getConfig().getDouble("data."+ Objects.requireNonNull(p.getPlayer()).getName()+".location.Yaw"), (float) data.getConfig().getDouble("data."+ Objects.requireNonNull(p.getPlayer()).getName()+".location.Pitch")));
                            else
                                Objects.requireNonNull(p.getPlayer()).teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.hub-or-survival")))).getSpawnLocation()); // first join
                            Bukkit.broadcastMessage(ChatColor.YELLOW + Objects.requireNonNull(p.getPlayer()).getDisplayName() + ChatColor.YELLOW + " joined to the server");
                            inv.restoreInventory(p);
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
                    } else
                        sender.sendMessage(ChatColor.RED + "Already registered, use /login <password>");
                }
            } else
                sender.sendMessage(ChatColor.GREEN + "Already registered!");
        }

        if (label.equalsIgnoreCase("logout")) {
            p.kickPlayer(Color.YELLOW+"Auth"+Color.GRAY+" | "+ChatColor.WHITE+"Successfully log out.");
            mySession.remove(Objects.requireNonNull(p.getAddress()).getAddress().toString());
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