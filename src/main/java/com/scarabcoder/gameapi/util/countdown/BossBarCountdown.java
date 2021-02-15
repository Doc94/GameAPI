package com.scarabcoder.gameapi.util.countdown;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public abstract class BossBarCountdown<PI extends JavaPlugin> implements Runnable {

    private final JavaPlugin instancePlugins;
    private int secondsLeft;
    @Getter
    private BossBar bossBar;
    private BukkitTask bukkitTask;
    private boolean pause;

    public BossBarCountdown(PI instancePlugin) {
        instancePlugins = instancePlugin;
        //Bukkit.getServer().getPluginManager().registerEvents(this, instancePlugin);
        Bukkit.getServer().getLogger().info("[BossBarCountdownLib] " + getClass().getSimpleName() + " instance of BossBarCountdown registered.");
    }

    public void build() {
        bossBar = Bukkit.createBossBar(getTitle(),getColor(),getStyle(),getFlags());
        restartTimeLeft();
    }

    public void show() {
        this.bossBar.setVisible(true);
    }

    public void hide() {
        this.bossBar.setVisible(false);
    }

    /**
     * Pause the Countdown
     */
    public void pause() {
        this.pause = true;
    }

    /**
     * Resume the countdown
     */
    public void resume() {
        this.pause = false;
    }

    /**
     * Restart the countdown (time left)
     */
    public void restart() {
        restartTimeLeft();
        start();
    }

    public void restartTimeLeft() {
        this.secondsLeft = getSeconds();
    }

    public void start() {
        if(bukkitTask != null && Bukkit.getScheduler().isCurrentlyRunning(bukkitTask.getTaskId())) {
            return;
        }
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(instancePlugins,this,0L,20L);
    }

    public void stop() {
        bossBar.removeAll();
        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        bukkitTask = null;
    }

    @Override
    public void run() {
        double percent = Math.abs(((double) this.secondsLeft) / ((double) this.getSeconds()));
        bossBar.setProgress(percent);
        bossBar.setTitle(getTitle().concat(": ").concat(ChatColor.RESET.toString()).concat(getFormatTimeLeft()));
        if(secondsLeft <= 0) {
            stop();
            if(this.getFinishCountdownRunnable() != null) {
                this.getFinishCountdownRunnable().run(this);
            }
            return;
        }
        if(pause) {
            return;
        }
        secondsLeft--;
    }

    public void addPlayer(Player player) {
        this.bossBar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        this.bossBar.removePlayer(player);
    }

    public String getFormatTimeLeft() {
        Duration dur = Duration.ofSeconds(this.secondsLeft);
        String result = String.format("%02d:%02d:%02d",
                dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart());

        if(dur.toHours() == 0) {
            result = String.format("%02d:%02d", dur.toMinutesPart(), dur.toSecondsPart());
        }

        if(dur.toHours() == 0 && dur.toMinutesPart() == 0) {
            result = String.format("%02d", dur.toSecondsPart());
        }

        return result;
    }

    public abstract int getSeconds();

    public abstract String getTitle();

    public abstract BarColor getColor();

    public abstract BarStyle getStyle();

    public abstract BarFlag[] getFlags();

    public abstract CountdownRunnable getFinishCountdownRunnable();
}
