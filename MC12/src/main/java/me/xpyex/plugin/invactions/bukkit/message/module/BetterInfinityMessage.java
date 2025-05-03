package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class BetterInfinityMessage {
    private MenuInfo menu = MenuInfo.of("&a优化弓的&5&l无限&a附魔", "&f当使用附魔了“无限”的弓", "&f无需携带箭矢", "&f也可以射箭");
}
