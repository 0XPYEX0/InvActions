package me.xpyex.plugin.invactions.bukkit;

import java.io.IOException;
import java.net.URLClassLoader;
import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.module.AutoFarmer;
import me.xpyex.plugin.invactions.bukkit.module.AutoTool;
import me.xpyex.plugin.invactions.bukkit.module.BetterInfinity;
import me.xpyex.plugin.invactions.bukkit.module.BetterLoyalty;
import me.xpyex.plugin.invactions.bukkit.module.CraftDrop;
import me.xpyex.plugin.invactions.bukkit.module.DynamicLight;
import me.xpyex.plugin.invactions.bukkit.module.HandleEvent;
import me.xpyex.plugin.invactions.bukkit.module.InventoryF;
import me.xpyex.plugin.invactions.bukkit.module.QuickDrop;
import me.xpyex.plugin.invactions.bukkit.module.QuickMove;
import me.xpyex.plugin.invactions.bukkit.module.QuickShulkerBox;
import me.xpyex.plugin.invactions.bukkit.module.ReplaceBrokenArmor;
import me.xpyex.plugin.invactions.bukkit.module.SortContainer;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.api.Version;
import me.xpyex.plugin.xplib.bukkit.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.core.XPPlugin;
import me.xpyex.plugin.xplib.bukkit.version.VersionUtil;
import me.xpyex.plugin.xplib.util.value.ValueUtil;
import me.xpyex.plugin.xplib.util.version.UpdateUtil;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

public final class InvActions extends XPPlugin {
    private static final String XPLIB_VER = "1.1.5";
    private static InvActions INSTANCE;

    public static InvActions getInstance() {
        return INSTANCE;
        //
    }

    @Override
    public void onDisable() {
        ConfigUtil.reload(getInstance());
        getLogger().info("已卸载");
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        if (!initCheck()) {
            getServer().getPluginManager().disablePlugin(getInstance());
            return;
        }

        updateServerConfig();
        getServer().getScheduler().runTaskAsynchronously(getInstance(), this::updatePlayersConfig);

        registerListeners();
        getLogger().info("已注册监听器");

        registerCmd("InvActions", new HandleCmd());
        getLogger().info("已注册命令");

        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            hookBStats(17118);
            getLogger().info("与bStats挂钩");
        });

        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            getLogger().info("开始检查更新");
            ValueUtil.optional(UpdateUtil.getUpdateFromGitee(getInstance()), ver -> {
                getLogger().info("当前插件版本: " + getInstance().getDescription().getVersion() + " ,有一个更新的版本: " + ver);
                getLogger().info("前往 https://gitee.com/XPYEX/InvActions/releases 下载吧！");
            }, () -> getLogger().info("当前版本已是最新！"));
        });

        getServer().getScheduler().runTaskAsynchronously(getInstance(), this::checkHybrid);

        getLogger().info("已加载");
        getLogger().info("感谢使用InvActions. 本插件在GitHub开源，谨防受骗. 作者QQ1723275529");
        getLogger().info("GitHub: https://github.com/0XPYEX0/InvActions");
    }

    public void checkHybrid() {
        String type = null;
        try {
            Class.forName("net.minecraftforge.common.MinecraftForge");
            type = "Forge";
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("net.neoforged.neoforge.common.NeoForge");
            type = "NeoForge";
        } catch (ClassNotFoundException ignored) {
        }

        if (Package.getPackage("net.fabricmc.fabric") != null) {
            type = "Fabric";
        }

        if (type != null) {
            getLogger().warning("你目前运行在具有 " + type + " 的Hybrid服务端.");
            getLogger().warning("由于Mod功能的实现方式影响，插件可能不会那么“正常”地运行，包括但不限于功能失效");
        }
    }

    public boolean initCheck() {
        if (!getServer().getPluginManager().isPluginEnabled("XPLib")) {
            getLogger().severe("本插件需要XPLib作为前置...");
            getLogger().severe("请在下载后，再加载本插件");
            getLogger().severe("GitHub: https://github.com/0XPYEX0/XPLib/releases");
            getLogger().severe("Gitee(国内): https://gitee.com/XPYEX/XPLib/releases");
            return false;
        }

        if (VersionUtil.getMainVersion() < 9) {
            getLogger().severe("本插件需要Minecraft至少为1.9才可运行");
            getLogger().severe("很遗憾，您的服务器不满足此条件...");
            return false;
        }

        if (!VersionUtil.isUpperXPLib(new Version(XPLIB_VER))) {
            getLogger().severe("请更新您服务器内的XPLib！");
            getLogger().severe("当前XPLib无法支持本插件");
            getLogger().severe("需要: " + XPLIB_VER + " , 当前: " + VersionUtil.getXPLibVersion().getVersion());
            getLogger().severe("GitHub: https://github.com/0XPYEX0/XPLib/releases");
            getLogger().severe("Gitee(国内): https://gitee.com/XPYEX/XPLib/releases");
            getServer().getPluginManager().disablePlugin(getInstance());
            return false;
        }

        return true;
    }

    public void updateServerConfig() {
        ConfigUtil.saveConfig(getInstance(), "config", InvActionsServerConfig.getDefault(), false, false);
        //如果原先没有文件，先新建一份
        ConfigUtil.saveConfig(getInstance(), "config", getJsonConfig(InvActionsServerConfig.class), true);
        //更新服务端config.json
    }

    public void updatePlayersConfig() {
        getServer().getOnlinePlayers().forEach((player -> {
            ConfigUtil.saveConfig(getInstance(), "players/" + player.getUniqueId(), InvActionsConfig.getDefault(), false, false);
            //如果文件还不存在，则新建一份
            ConfigUtil.saveConfig(getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
            //更新设定，应对热拔插
        }));
    }

    public void registerListeners() {
        new AutoFarmer();
        new AutoTool();
        new BetterInfinity();
        new BetterLoyalty();
        new CraftDrop();
        new DynamicLight();
        registerListener(new HandleEvent());
        new QuickDrop();
        new QuickMove();
        new QuickShulkerBox();
        new ReplaceBrokenArmor();
        new SortContainer();
        try {
            @SuppressWarnings("unused")
            ClickType swapOffhand = ClickType.SWAP_OFFHAND;  //1.16+
            registerListener(new InventoryF());
        } catch (Throwable ignored) {
            getLogger().warning("您的服务器不支持在玩家背包内按F整理，该功能已被禁用");
        }
    }
}
