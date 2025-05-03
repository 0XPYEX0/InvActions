package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;

@Data
public class StateMessage {
    private String current = "&f当前状态";
    private String server_disabled = "&4服务端禁用";
    private String player_disabled = "&c禁用";
    private String player_enabled = "&a启用";
    private String perm_deny = "&c权限不足";
}
