package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class ReplaceBrokenToolMessage {
    private String broken = "&a您的道具已损毁，从背包补全.";
    private String run_out = "&a您的道具已用尽，从背包补全.";
    private MenuInfo menu = MenuInfo.of("&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充");
}
