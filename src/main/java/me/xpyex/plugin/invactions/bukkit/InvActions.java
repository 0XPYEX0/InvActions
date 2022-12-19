package me.xpyex.plugin.invactions.bukkit;

import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.listener.HandleEvent;
import me.xpyex.plugin.invactions.bukkit.listener.HighVerListener;
import me.xpyex.plugin.xplib.bukkit.api.Version;
import me.xpyex.plugin.xplib.bukkit.util.bstats.BStatsUtil;
import me.xpyex.plugin.xplib.bukkit.util.version.UpdateUtil;
import me.xpyex.plugin.xplib.bukkit.util.version.VersionUtil;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

public final class InvActions extends JavaPlugin {
    private static InvActions INSTANCE;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        if (!getServer().getPluginManager().isPluginEnabled("XPLib")) {
            getLogger().warning("本插件需要XPLib作为前置...");
            getLogger().warning("请在 https://github.com/0XPYEX0/XPLib/releases 下载后，再加载本插件");
            getServer().getPluginManager().disablePlugin(getInstance());
            return;
        }

        if (VersionUtil.getMainVersion() < 9) {
            getLogger().severe("本插件需要Minecraft至少为1.9才可运行");
            getLogger().severe("很遗憾，您的服务器不满足此条件...");
            getServer().getPluginManager().disablePlugin(getInstance());
            return;
        }

        if (!VersionUtil.requireXPLib(new Version("1.0.4"))) {
            getLogger().severe("请更新您服务器内的XPLib！");
            getLogger().severe("当前XPLib无法支持本插件");
            getServer().getPluginManager().disablePlugin(getInstance());
            return;
        }

        getServer().getPluginManager().registerEvents(new HandleEvent(), getInstance());
        try {
            ClickType swapOffhand = ClickType.SWAP_OFFHAND;  //不是每个版本都有这个
            getServer().getPluginManager().registerEvents(new HighVerListener(), getInstance());
        } catch (Throwable ignored) {
            getLogger().warning("您的服务器不支持在玩家背包内按F整理，该功能已被禁用");
        }
        getLogger().info("已注册监听器");

        getCommand("InvActions").setExecutor(new HandleCmd());
        getLogger().info("已注册命令");

        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            BStatsUtil.hookWith(getInstance(), 17118);
            getLogger().info("与bStats挂钩");
        });

        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            getLogger().info("开始检查更新");
            String ver = UpdateUtil.getUpdateFromGitee(getInstance());
            if (ver != null) {
                getLogger().info("当前插件版本: " + getInstance().getDescription().getVersion() + " ,有一个更新的版本: " + ver);
                getLogger().info("前往 https://gitee.com/XPYEX/InvActions/releases 下载吧！");
            } else {
                getLogger().info("当前版本已是最新！");
            }
        });

        getLogger().info("已加载");
        getLogger().info("感谢使用InvActions. 本插件在GitHub与Gitee开源，谨防受骗. 作者QQ1723275529");
    }

    @Override
    public void onDisable() {
        getLogger().info("已卸载");
        //
    }

    public static InvActions getInstance() {
        return INSTANCE;
        //
    }
}