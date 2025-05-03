package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class QuickShulkerBoxMessage {
    private MenuInfo menu = MenuInfo.of("&a快捷编辑潜影盒",
        "&f在背包中 &e&lShift+右键 &f潜影盒",
        "&f或",
        "&f手持潜影盒 &e&lShift+右键",
        "&f直接打开该潜影盒"
    );
}
