package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class ReloadMessage {
    private String reload = "&aInvActions重载完成";
    private MenuInfo menu = MenuInfo.of("&6重载所有配置文件", "&f包括服务端和玩家");
}
