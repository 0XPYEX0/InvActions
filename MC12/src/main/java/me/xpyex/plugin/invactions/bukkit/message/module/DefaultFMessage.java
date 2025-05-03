package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class DefaultFMessage {
    private String player = "&a已整理你的背包.";
    private String target = "&a已整理你看向的 &f{0} &a.";
    private MenuInfo menu = MenuInfo.of("&a按下 &e&lF &a整理",
        "&f当没有打开任何界面时",
        "&f不论是否潜行且低头",
        "&f按下 &e&lF &f就整理自身背包",
        "&5&o该设定不影响 &e&lShift+F &5&o整理容器"
    );
}
