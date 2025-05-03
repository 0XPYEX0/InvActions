package me.xpyex.plugin.invactions.bukkit.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvActionsServerConfig extends InvActionsConfig {
    @Getter
    private static final InvActionsServerConfig Default = new InvActionsServerConfig();
    @Getter
    @Setter
    private static InvActionsServerConfig current;


    private boolean AutoFarmer_AllowPumpkinAndMelon = false;
    private boolean Debug = false;
    private String Lang = (Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()).toLowerCase();
    private HashSet<String> AllowInvs = new HashSet<>();
    private boolean PermCheck = false;
    private HashMap<String, Integer> EggCatcher_Chance = new HashMap<>();

    public InvActionsServerConfig() {
        setCurrent(this);
        //
    }
}
