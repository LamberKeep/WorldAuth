package com.myvnc.exo.worldauth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

import static java.lang.Math.round;

public final class WorldAuth extends JavaPlugin implements Listener {
    public DataManager data;
    public LocaleFile locale;

    HashMap<String, Long> session  = new HashMap<>(); // sessions timer
    HashMap<Player, Long> register  = new HashMap<>(); // register timer
    HashMap<Player, Integer>  attempts = new HashMap<>(); // auth attempts counter

    @Override
    public void onEnable() {
        data = new DataManager(this);
        locale = new LocaleFile(this);
        getServer().getPluginManager().registerEvents(this, this);
        new WorldCreator(Objects.requireNonNull(getConfig().getString("worlds.auth"))).createWorld();
        saveDefaultConfig();

        AuthMessages();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String nick = player.getName();
        String ip = Objects.requireNonNull(player.getAddress()).getAddress().toString();

        e.setJoinMessage(null);

        if (!session.containsKey(ip) || System.currentTimeMillis() >= session.get(ip)) {
            player.teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))).getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            player.setExp(1);

            register.put(player, System.currentTimeMillis() + getConfig().getLong("timer.register") * 1000);

            if (data.getConfig().getString(nick + ".password") == null)
                player.sendTitle(locale.getLocale(player, "titles.register"), locale.getLocale(player,"titles.sub-title"), 0, (int) (getConfig().getLong("timer.register") * 1000), 0);
            else
                player.sendTitle(locale.getLocale(player, "titles.auth"), locale.getLocale(player,"titles.sub-title"), 0, (int) (getConfig().getLong("timer.register") * 1000), 0);

        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW + nick + " joined to the server.");
            data.loadInventory(player);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (player.getWorld() != Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))) {
            session.put(Objects.requireNonNull(player.getAddress()).getAddress().toString(), System.currentTimeMillis() + getConfig().getLong("timer.session") * 60000);
            register.remove(player);
            attempts.remove(player);

            data.getConfig().set(player.getName() + ".gamemode", player.getGameMode().toString());
            data.saveLocation(player);
            data.saveExp(player);
            data.saveInventory(player);

            e.setQuitMessage(locale.getLocale(player, "system.leave"));
        }
        player.resetTitle();
    }

    @EventHandler
    public void OnChat(PlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        String nick = player.getName();
        if (player.getWorld() == Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))) {
            if (data.getConfig().getString(nick + ".password") == null) { // register
                data.getConfig().set(nick + ".password", message);
                data.saveConfig();
                player.sendMessage(locale.getLocale(player, "registed"));
                player.resetTitle();
                Objects.requireNonNull(player.getPlayer()).teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.hub-or-survival")))).getSpawnLocation());
                player.setGameMode(GameMode.SURVIVAL);
                Bukkit.broadcastMessage(locale.getLocale(player, "system.join"));
            } else { // login
                if (message.equals(data.getConfig().getString(nick + ".password"))) {
                    player.sendMessage(locale.getLocale(player, "auth.success"));

                    register.remove(player);
                    attempts.remove(player);
                    player.resetTitle();

                    data.loadLocation(player);
                    data.loadInventory(player);
                    data.loadExp(player);
                    player.setGameMode(GameMode.valueOf(data.getConfig().getString(nick + ".gamemode")));

                    Bukkit.broadcastMessage(locale.getLocale(player, "system.join"));
                } else {
                    attempts.put(player, attempts.getOrDefault(player, 0) + 1);
                    if (attempts.get(player) < getConfig().getInt("join-attempts"))
                        player.sendMessage(locale.getLocale(player, "auth.wrong"));
                    else
                        player.kickPlayer(locale.getLocale(player, "auth.kick"));
                }
            }
            event.setCancelled(true);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Objects.requireNonNull(locale.getConfig().getString("plugin.console")));
            return false;
        }

        Player player = (Player) sender;
        String nick = player.getName();

        if (label.equalsIgnoreCase("worldauth") || label.equalsIgnoreCase("wa")) {
            if (!player.hasPermission("doublejump.admin")) {
                player.sendMessage(locale.getLocale(player, "plugin.permission"));
                return true;
            }

            if (args.length == 0)
                player.sendMessage(locale.getLocale(player, "plugin.usage"));
            else {
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    data.reloadConfig();
                    locale.reloadConfig();
                    player.sendMessage(locale.getLocale(player, "plugin.reloaded"));
                }
            }
        }

        if (label.equalsIgnoreCase("logout") || label.equalsIgnoreCase("q")) {
            player.kickPlayer(locale.getLocale(player,"logout"));
            session.remove(Objects.requireNonNull(player.getAddress()).getAddress().toString());
        }

        if (label.equalsIgnoreCase("changepassword") || label.equalsIgnoreCase("changepass")) {
            if (!(player.getWorld() == Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth"))))) {
                if (args.length == 2) {
                    if (args[0].equals(data.getConfig().getString(nick + ".password"))) {
                        data.getConfig().set(nick + ".password", args[1]);
                        data.saveConfig();
                        sender.sendMessage(locale.getLocale(player, "changepass.success"));
                    } else
                        sender.sendMessage(locale.getLocale(player, "changepass.wrong"));
                } else
                    sender.sendMessage(locale.getLocale(player, "changepass.usage"));
            }
        }
        return false;
    }

    public void AuthMessages() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player: Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(getConfig().getString("worlds.auth")))).getPlayers()) {
                    float timeLeft = (float) ((register.get(player)) - System.currentTimeMillis()) / 1000; // in sec

                    if (timeLeft > 0) {
                        player.setLevel(round(timeLeft));
                        player.setExp(timeLeft / getConfig().getLong("timer.register"));
                    } else
                        player.kickPlayer(locale.getLocale(player, "system.time-is-up"));
                }
            }
        }.runTaskTimer(this, 0, 2); // 20 ticks = 1 second
    }
}