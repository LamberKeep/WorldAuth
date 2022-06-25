package lamberkeep.worldauth.data;

import lamberkeep.worldauth.task.Auth;

import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.config;

public class Status {
    private final org.bukkit.entity.Player player;
    private String ip;
    private long session;
    private int attempt;
    private Auth auth;

    public Status(org.bukkit.entity.Player player) {
        this.player = player;
    }

    public void updateIp() {
        this.ip = Objects.requireNonNull(player.getAddress()).getHostName();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void updateSession() {
        this.session = System.currentTimeMillis() + config.getLong("timer.register") * 1000;
    }

    public void setSession(Long session) {
        this.session = session;
    }

    public Long getSession() {
        return session;
    }

    public void addAttempt() {
        this.attempt += 1;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Auth getAuth() {
        return auth;
    }

}