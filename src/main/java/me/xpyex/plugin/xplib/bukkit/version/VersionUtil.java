package me.xpyex.plugin.xplib.bukkit.version;

import me.xpyex.plugin.xplib.api.Version;
import me.xpyex.plugin.xplib.util.RootUtil;
import org.bukkit.Bukkit;

public class VersionUtil extends RootUtil {
    private static final int MAIN_VERSION = new Version(Bukkit.getBukkitVersion()).getVersion(1);

    public static int getMainVersion() {
        return MAIN_VERSION;
        //
    }

    public static String getServerVersion() {
        return Bukkit.getVersion().split("-")[0];
        //
    }
}
