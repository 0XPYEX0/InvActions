package me.xpyex.plugin.invactions.bukkit.config;

public class InvActionsConfig {
    private static final InvActionsConfig DEFAULT = new InvActionsConfig();
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
}
