package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class AutoFarmerMessage {
    private String harvest = "&a已为您自动收获 &f{0} &a.";
    private String harvest_and_plant = "&a已为您自动收获并种植 &f{0} &a.";
    private MenuInfo menu = MenuInfo.of("&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割");
}
