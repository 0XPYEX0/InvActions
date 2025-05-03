package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class CraftDropMessage {
    private String drop = "&a已自动合成所有物品，并自动丢出.";
    private MenuInfo menu = MenuInfo.of("&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果");
}
