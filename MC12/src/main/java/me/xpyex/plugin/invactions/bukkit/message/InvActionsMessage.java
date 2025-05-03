package me.xpyex.plugin.invactions.bukkit.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.xpyex.plugin.invactions.bukkit.message.module.AutoFarmerMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.AutoToolMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.BetterInfinityMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.BetterLoyaltyMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.CraftDropMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.DefaultFMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.DynamicLightMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.EggCatcherMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.QuickDropMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.QuickMoveMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.QuickShulkerBoxMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.ReloadMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.ReplaceBrokenArmorMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.ReplaceBrokenToolMessage;
import me.xpyex.plugin.invactions.bukkit.message.module.StateMessage;

@Data
public class InvActionsMessage {
    @Getter
    private static final InvActionsMessage ChineseDefault = new InvActionsMessage();
    @Getter
    @Setter
    private static InvActionsMessage current;
    private String ActionBarSuffix = "&e该功能在 &f/InvActions &e中调整";
    private AutoFarmerMessage AutoFarmer = new AutoFarmerMessage();
    private AutoToolMessage AutoTool = new AutoToolMessage();
    private BetterInfinityMessage BetterInfinity = new BetterInfinityMessage();
    private BetterLoyaltyMessage BetterLoyalty = new BetterLoyaltyMessage();
    private CraftDropMessage CraftDrop = new CraftDropMessage();
    private DefaultFMessage DefaultF = new DefaultFMessage();
    private DynamicLightMessage DynamicLight = new DynamicLightMessage();
    private EggCatcherMessage EggCatcher = new EggCatcherMessage();
    private QuickDropMessage QuickDrop = new QuickDropMessage();
    private QuickMoveMessage QuickMove = new QuickMoveMessage();
    private QuickShulkerBoxMessage QuickShulkerBox = new QuickShulkerBoxMessage();
    private ReloadMessage Reload = new ReloadMessage();
    private ReplaceBrokenArmorMessage ReplaceBrokenArmor = new ReplaceBrokenArmorMessage();
    private ReplaceBrokenToolMessage ReplaceBrokenTool = new ReplaceBrokenToolMessage();
    private StateMessage State = new StateMessage();

    public InvActionsMessage() {
        setCurrent(this);
        //
    }
}
