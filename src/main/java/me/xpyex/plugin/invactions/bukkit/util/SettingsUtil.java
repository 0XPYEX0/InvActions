package me.xpyex.plugin.invactions.bukkit.util;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import org.bukkit.entity.Player;

public class SettingsUtil {
    public static InvActionsConfig getConfig(Player player) {
        InvActionsConfig config = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), InvActionsConfig.class);
        return config == null ? InvActionsConfig.fuckFakePlayers() : config;
    }
}
