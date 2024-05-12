package me.xpyex.plugin.invactions.bukkit.config;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class InvActionsServerConfig extends InvActionsConfig {
    private static final InvActionsServerConfig DEFAULT = new InvActionsServerConfig();
    private static InvActionsServerConfig config;
    public boolean AutoFarmer_AllowPumpkinAndMelon = false;
    public boolean Debug = false;
    public String lang = (Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()).toLowerCase();
    public Set<String> AllowInvs = new HashSet<>();

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
