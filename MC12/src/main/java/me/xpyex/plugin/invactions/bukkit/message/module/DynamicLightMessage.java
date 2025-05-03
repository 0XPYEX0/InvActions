package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class DynamicLightMessage {
    private String light = "&a你目前手持光源，动态光源启用.";
    private MenuInfo menu = MenuInfo.of("&a动态光源", "&f当手持光源道具时", "&f在玩家位置模拟光源");
}
