package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class AutoToolMessage {
    private String changed = "&a已自动切换为合适的工具.";
    private MenuInfo menu = MenuInfo.of("&a自动更换工具", "&f当尝试破坏方块", "&f或尝试耕地时", "&f自动从背包内换出适合的工具");
}
