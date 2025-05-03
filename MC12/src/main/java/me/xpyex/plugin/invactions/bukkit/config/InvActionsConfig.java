package me.xpyex.plugin.invactions.bukkit.config;

import lombok.Data;

@Data
public class InvActionsConfig {
    private static final InvActionsConfig DEFAULT = new InvActionsConfig();
    private static final InvActionsConfig FUCK_FAKE_PLAYER = new InvActionsConfig();

    static {
        FUCK_FAKE_PLAYER.AutoFarmer = false;
        FUCK_FAKE_PLAYER.AutoTool = false;
        FUCK_FAKE_PLAYER.BetterInfinity = false;
        FUCK_FAKE_PLAYER.BetterLoyalty = false;
        FUCK_FAKE_PLAYER.CraftDrop = false;
        FUCK_FAKE_PLAYER.DefaultF = false;
        FUCK_FAKE_PLAYER.DynamicLight = false;
        FUCK_FAKE_PLAYER.EggCatcher = false;
        FUCK_FAKE_PLAYER.QuickDrop = false;
        FUCK_FAKE_PLAYER.QuickMove = false;
        FUCK_FAKE_PLAYER.QuickShulkerBox = false;
        FUCK_FAKE_PLAYER.ReplaceBrokenArmor = false;
        FUCK_FAKE_PLAYER.ReplaceBrokenTool = false;
    }

    private boolean AutoFarmer = true;
    private boolean AutoTool = false;
    private boolean BetterInfinity = true;
    private boolean BetterLoyalty = true;
    private boolean CraftDrop = true;
    private boolean DefaultF = false;
    private boolean DynamicLight = true;
    private boolean EggCatcher = false;
    private boolean QuickDrop = true;
    private boolean QuickMove = true;
    private boolean QuickShulkerBox = false;
    private boolean ReplaceBrokenArmor = true;
    private boolean ReplaceBrokenTool = true;

    public static InvActionsConfig getDefault() {
        return DEFAULT;
        //
    }

    public static InvActionsConfig fuckFakePlayers() {
        return FUCK_FAKE_PLAYER;
        //Fuck you ALL!
    }
}
