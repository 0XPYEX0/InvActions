package me.xpyex.plugin.invactions.bukkit.config;

import java.util.ArrayList;

public class InvActionsServerConfig extends InvActionsConfig {
    public static final String SETTING_HELP = "&e该功能在 &f/InvActions &e中调整";
    private static final InvActionsServerConfig DEFAULT = new InvActionsServerConfig();
    private static InvActionsServerConfig config;
    public boolean AutoFarmer_AllowPumpkinAndMelon = false;
    public boolean Debug = false;
    public ArrayList<String> AllowInvs = new ArrayList<>();

    public InvActionsServerConfig() {
        setConfig(this);
        //
    }

    public static InvActionsServerConfig getConfig() {
        return config;
        //
    }

    public static void setConfig(InvActionsServerConfig config) {
        InvActionsServerConfig.config = config;
        //
    }

    public static InvActionsServerConfig getDefault() {
        return DEFAULT;
        //
    }
}
