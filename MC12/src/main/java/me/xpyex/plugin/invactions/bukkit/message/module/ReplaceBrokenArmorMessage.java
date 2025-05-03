package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class ReplaceBrokenArmorMessage {
    private String broken = "&a您的道具已损毁，从背包补全.";
    private MenuInfo menu = MenuInfo.of("&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充");
}
