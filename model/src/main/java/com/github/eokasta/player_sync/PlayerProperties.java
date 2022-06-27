package com.github.eokasta.player_sync;

import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class PlayerProperties {
    
    private final UUID playerUniqueId;
    private final String inventory;
    private final GameMode gameMode;
    private final double health;
    private final double maxHealth;
    private final float exp;
    private final int totalExp;
    private final int level;
    private final int food;
    private final float exhaustion;
    private final float saturation;
          
}