package me.xpyex.plugin.invactions.bukkit.config;

public class InvActionsConfig {
    private static final InvActionsConfig DEFAULT = new InvActionsConfig();
    private static final InvActionsConfig FUCK_FAKE_PLAYER = new InvActionsConfig();
    public boolean AutoFarmer = true;
    public boolean AutoTool = true;
    public boolean BetterInfinity = true;
    public boolean CraftDrop = true;
    public boolean DefaultF = false;
    public boolean DynamicLight = true;
    public boolean QuickDrop = false;
    public boolean QuickMove = true;
    public boolean QuickShulkerBox = false;
    public boolean ReplaceBrokenArmor = true;
    public boolean ReplaceBrokenTool = true;

    public static InvActionsConfig getDefault() {
        return DEFAULT;
        //
    }

    public static InvActionsConfig fuckFakePlayers() {
        return FUCK_FAKE_PLAYER;
        //Fuck you ALL!
    }

    static {
        FUCK_FAKE_PLAYER.AutoFarmer = false;
        FUCK_FAKE_PLAYER.AutoTool = false;
        FUCK_FAKE_PLAYER.BetterInfinity = false;
        FUCK_FAKE_PLAYER.CraftDrop = false;
        FUCK_FAKE_PLAYER.DefaultF = false;
        FUCK_FAKE_PLAYER.DynamicLight = false;
        FUCK_FAKE_PLAYER.QuickDrop = false;
        FUCK_FAKE_PLAYER.QuickMove = false;
        FUCK_FAKE_PLAYER.QuickShulkerBox = false;
        FUCK_FAKE_PLAYER.ReplaceBrokenArmor = false;
        FUCK_FAKE_PLAYER.ReplaceBrokenTool = false;
    }
}
