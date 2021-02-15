package com.scarabcoder.gameapi.util.countdown;

import com.scarabcoder.gameapi.util.gui.ClickData;

@FunctionalInterface
public interface CountdownRunnable {

    void run(BossBarCountdown bossBarCountdown);
}
