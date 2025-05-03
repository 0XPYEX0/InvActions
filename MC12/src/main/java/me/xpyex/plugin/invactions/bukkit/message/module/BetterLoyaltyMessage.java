package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class BetterLoyaltyMessage {
    private String called = "&a三叉戟即将超出距离，已强制其开始返航.";
    private MenuInfo menu = MenuInfo.of("&a更好的&5&l忠诚&a附魔", "&f当扔出附魔了“忠诚”的三叉戟", "&f超出视距自动收回");
}
