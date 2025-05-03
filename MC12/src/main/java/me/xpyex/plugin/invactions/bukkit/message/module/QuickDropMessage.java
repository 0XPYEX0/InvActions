package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class QuickDropMessage {
    private String drop = "&a已丢出背包所有相同道具.";
    private MenuInfo menu = MenuInfo.of("&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具");
}
