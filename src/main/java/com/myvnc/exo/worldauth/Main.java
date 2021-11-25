package com.myvnc.exo.worldauth;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Objects;

public final class Main extends JavaPlugin implements Listener {

    HashMap<InetAddress, Long> LoginSession  = new HashMap<>(); // Login session timer
    // get 1: LoginSession.getOrDefault(name, param)
    // get 2: LoginSession.get(name)
    // put: LoginSession.put(name, param);

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
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        e.setJoinMessage(null);
        p.teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))).getSpawnLocation());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(null);
        if (!Objects.equals(p.getWorld().toString(), getConfig().getString("worlds.auth"))){

            Location loc = e.getPlayer().getLocation();
            data.getConfig().set("data."+e.getPlayer().getName()+".location.World" , Objects.requireNonNull(loc.getWorld()).getName());
            data.getConfig().set("data."+e.getPlayer().getName()+".location.X" , loc.getX());
            data.getConfig().set("data."+e.getPlayer().getName()+".location.Y" , loc.getY());
            data.getConfig().set("data."+e.getPlayer().getName()+".location.Z" , loc.getZ());
            data.getConfig().set("data."+e.getPlayer().getName()+".location.Yaw" , loc.getYaw());
            data.getConfig().set("data."+e.getPlayer().getName()+".location.Pitch" , loc.getPitch());
            data.saveConfig();

        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String loc = "data."+sender.getName()+".location";
        InetAddress ip = Objects.requireNonNull(((Player) sender).getAddress()).getAddress();

        if (label.equalsIgnoreCase("login")) {
            if (args.length == 0) sender.sendMessage(ChatColor.RED + "Usage: /login <password>"); else {
                if (this.data.getConfig().getString("data." + sender.getName() + ".password") == null)
                    sender.sendMessage(ChatColor.RED + "Not registered yet, use /register <password>");
                else {
                    if (args[0].equals(this.data.getConfig().getString("data." + sender.getName() + ".password"))) {
                        sender.sendMessage(ChatColor.GREEN + "Log in successfully.");
                        LoginSession.put(ip,System.currentTimeMillis() + 30*60000);
                        if (this.data.getConfig().getString(loc) != null) Objects.requireNonNull(((Player) sender).getPlayer()).teleport(new Location(Bukkit.getServer().getWorld(Objects.requireNonNull(data.getConfig().getString("data." + Objects.requireNonNull(((Player) sender).getPlayer()).getName() + ".location.World"))), data.getConfig().getDouble("data."+ Objects.requireNonNull(((Player) sender).getPlayer()).getName()+".location.X"), data.getConfig().getDouble(".location.Y"), data.getConfig().getDouble("data."+ Objects.requireNonNull(((Player) sender).getPlayer()).getName()+".location.Z"), (float) data.getConfig().getDouble("data."+ Objects.requireNonNull(((Player) sender).getPlayer()).getName()+".location.Yaw"), (float) data.getConfig().getDouble("data."+ Objects.requireNonNull(((Player) sender).getPlayer()).getName()+".location.Pitch")));
                        else Objects.requireNonNull(((Player) sender).getPlayer()).teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.hub-or-survival")))).getSpawnLocation());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Bad password, try again!");
                    }
                }
            }
        }

        if (label.equalsIgnoreCase("register")) {
            if (args.length == 0) sender.sendMessage(ChatColor.RED + "Usage: /register <password>");
            else {
                if (this.data.getConfig().getString("data."+sender.getName()+".password") == null) {
                    // player password isn't exists yet //
                    data.getConfig().set("data." + sender.getName() + ".password", args[0]); // set new password //
                    data.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Registered successfully.");
                } else sender.sendMessage(ChatColor.RED + "Already registered, use /login <password>");
            }
        }

        return false;
    }

    /* something will be there in future *\
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
        if (sender instanceof Player) {
            List<String> list = new ArrayList<String>();
            if (args.length == 0) list.add("l");
            return list;
        return null;
    }
    \* something will be there in future */


    public void AuthMessages() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers())
                    if (Objects.equals(p.getWorld().getName(), getConfig().getString("worlds.auth")))
                        p.sendMessage(ChatColor.YELLOW + "Auth" + ChatColor.GRAY + " | " + ChatColor.WHITE + "Please log in.");
            }

        }.runTaskTimerAsynchronously(this, 0, 3*20);
    }
}