package me.xpyex.plugin.invactions.bukkit;

import com.google.gson.JsonObject;
import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.listener.AutoFarmer;
import me.xpyex.plugin.invactions.bukkit.listener.AutoTool;
import me.xpyex.plugin.invactions.bukkit.listener.CraftDrop;
import me.xpyex.plugin.invactions.bukkit.listener.DynamicLight;
import me.xpyex.plugin.invactions.bukkit.listener.HandleEvent;
import me.xpyex.plugin.invactions.bukkit.listener.InventoryF;
import me.xpyex.plugin.invactions.bukkit.listener.QuickDrop;
import me.xpyex.plugin.invactions.bukkit.listener.QuickMove;
import me.xpyex.plugin.invactions.bukkit.listener.ReplaceBroken;
import me.xpyex.plugin.invactions.bukkit.listener.SortContainer;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.api.Version;
import me.xpyex.plugin.xplib.bukkit.core.XPPlugin;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.value.ValueUtil;
import me.xpyex.plugin.xplib.bukkit.util.version.UpdateUtil;
import me.xpyex.plugin.xplib.bukkit.util.version.VersionUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public final class InvActions extends XPPlugin {
    private static final String XPLIB_VER = "1.1.3";
    private static InvActions INSTANCE;

    public static InvActions getInstance() {
        return INSTANCE;
        //
    }

    @Override
    public void onDisable() {
        getLogger().info("已卸载");
        //
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
            ValueUtil.optional(UpdateUtil.getUpdateFromGitee(getInstance()), (ver) -> {
                getLogger().info("当前插件版本: " + getInstance().getDescription().getVersion() + " ,有一个更新的版本: " + ver);
                getLogger().info("前往 https://gitee.com/XPYEX/InvActions/releases 下载吧！");
            }, () -> {
                getLogger().info("当前版本已是最新！");
            });
        });

        getLogger().info("已加载");
        getLogger().info("感谢使用InvActions. 本插件在GitHub与Gitee开源，谨防受骗. 作者QQ1723275529");
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

        if (!VersionUtil.requireXPLib(new Version(XPLIB_VER))) {
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
        ConfigUtil.saveConfig(getInstance(), "config", GsonUtil.parseStr(SettingsUtil.DEFAULT_SERVER_SETTINGS), false);
        JsonObject o = GsonUtil.copy(SettingsUtil.DEFAULT_SERVER_SETTINGS);
        JsonObject config = ConfigUtil.getConfig(getInstance());
        for (String key : GsonUtil.getKeysOfJsonObject(config)) {
            o.add(key, config.get(key));
        }
        ConfigUtil.saveConfig(getInstance(), "config", GsonUtil.parseStr(o), true);
    }  //更新服务端config.json

    public void updatePlayersConfig() {
        getServer().getOnlinePlayers().forEach((player -> {
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(SettingsUtil.DEFAULT_SETTINGS), false);
            //如果文件还不存在，则新建一份
            JsonObject before = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());
            JsonObject out = GsonUtil.copy(SettingsUtil.DEFAULT_SETTINGS);
            for (String setting : GsonUtil.getKeysOfJsonObject(SettingsUtil.DEFAULT_SETTINGS)) {
                if (before.has(setting)) {
                    out.add(setting, before.get(setting));
                }
            }  //更新设定，应对热拔插
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(out), true);
        }));
    }

    public void registerListeners() {
        registerListener(new AutoFarmer());
        registerListener(new CraftDrop());
        registerListener(new HandleEvent());
        registerListener(new QuickDrop());
        registerListener(new QuickMove());
        registerListener(new ReplaceBroken());
        registerListener(new SortContainer());
        try {
            @SuppressWarnings("unused")
            ClickType swapOffhand = ClickType.SWAP_OFFHAND;  //1.16+
            registerListener(new InventoryF());
        } catch (Throwable ignored) {
            getLogger().warning("您的服务器不支持在玩家背包内按F整理，该功能已被禁用");
        }

        try {
            Block.class.getMethod("getBreakSpeed", Player.class);  //1.17+
            registerListener(new AutoTool());
        } catch (NoSuchMethodError | NoSuchMethodException ignored) {
            getLogger().warning("您的服务器不支持自动切换玩家工具，该功能已禁用");
        }

        try {
            Block.class.getMethod("getBlockData");  //1.13+
            DynamicLight.registerTask();
            registerListener(new DynamicLight());
        } catch (NoSuchMethodError | NoSuchMethodException ignored) {
            getLogger().warning("您的服务器不支持动态光源，该功能已禁用");
        }
    }
}
