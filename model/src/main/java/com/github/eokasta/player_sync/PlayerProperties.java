package com.github.eokasta.player_sync;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public record PlayerProperties(
      UUID playerUniqueId,
      String inventory,
      GameMode gameMode,
      double health,
      double maxHealth,
      float exp,
      int totalExp,
      int level,
      int food,
      float exhaustion,
      float saturation
) {

}
