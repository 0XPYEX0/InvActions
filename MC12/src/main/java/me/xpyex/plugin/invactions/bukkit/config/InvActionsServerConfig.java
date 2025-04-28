package me.xpyex.plugin.invactions.bukkit.config;

import java.util.HashSet;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

public class InvActionsServerConfig extends InvActionsConfig {
    private static final InvActionsServerConfig DEFAULT = new InvActionsServerConfig();
    @Getter
    @Setter
    private static InvActionsServerConfig config;
    public boolean AutoFarmer_AllowPumpkinAndMelon = false;
    public boolean Debug = false;
    public String Lang = (Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()).toLowerCase();
    public HashSet<String> AllowInvs = new HashSet<>();
    public boolean PermCheck = false;

    public InvActionsServerConfig() {
        setConfig(this);
        //
    }

    public static InvActionsServerConfig getDefault() {
        return DEFAULT;
        //
    }
}
