package me.xpyex.plugin.invactions.bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.module.HandleEvent;
import me.xpyex.plugin.invactions.bukkit.module.InventoryF;
import me.xpyex.plugin.invactions.bukkit.module.RootModule;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.core.XPPlugin;
import me.xpyex.plugin.xplib.bukkit.inventory.HandleMenu;
import me.xpyex.plugin.xplib.bukkit.inventory.Menu;
import me.xpyex.plugin.xplib.bukkit.version.VersionUtil;
import me.xpyex.plugin.xplib.util.reflect.ClassUtil;
import me.xpyex.plugin.xplib.util.reflect.MethodUtil;
import me.xpyex.plugin.xplib.util.value.ValueUtil;
import me.xpyex.plugin.xplib.util.version.UpdateUtil;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

public final class InvActions extends XPPlugin {
    private static InvActions INSTANCE;

    public static InvActions getInstance() {
        return INSTANCE;
        //
    }

    @Override
    public void onDisable() {
        for (Menu menu : Menu.getMenus().values()) {
            menu.getPlayer().closeInventory();
        }
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
            ValueUtil.optional(UpdateUtil.getUpdateFromGitHub(getInstance()), ver -> {
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
        initXPLib();

        if (VersionUtil.getMainVersion() < 9) {
            getLogger().severe("本插件需要Minecraft至少为1.9才可运行");
            getLogger().severe("很遗憾，您的服务器不满足此条件...");
            return false;
        }

        return true;
    }

    public void updateServerConfig() {
        ConfigUtil.saveConfig(getInstance(), "config", InvActionsServerConfig.getDefault(), false, false);
        //如果原先没有文件，先新建一份
        ConfigUtil.saveConfig(getInstance(), "config", ConfigUtil.getConfig(this, InvActionsServerConfig.class), true);
        //更新服务端config.json
    }

    public void updatePlayersConfig() {
        getServer().getOnlinePlayers().forEach((player -> {
            ConfigUtil.saveConfig(getInstance(), "players" + File.separator + player.getUniqueId(), InvActionsConfig.getDefault(), false, false);
            //如果文件还不存在，则新建一份
            ConfigUtil.saveConfig(getInstance(), "players" + File.separator + player.getUniqueId(), SettingsUtil.getConfig(player), true);
            //更新设定，应对热拔插
        }));
    }

    public void registerListeners() {
        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            for (Class<?> moduleClass : ClassUtil.getClasses("me.xpyex.plugin.invactions.bukkit.module")) {
                if (RootModule.class.isAssignableFrom(moduleClass)) {  //moduleClass是RootModule的子类，继承RootModule
                    if (
                        moduleClass.isPrimitive()
                            || moduleClass.isArray()
                            || moduleClass.isInterface()
                            || moduleClass.isAnnotation()
                            || moduleClass.isEnum()
                    ) continue;

                    try {
                        moduleClass.getConstructor().newInstance();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        InvActions.getInstance().getLogger().warning("加载模块 " + moduleClass.getSimpleName() + " 时出错: " + e);
                    }
                }
            }
        });
        registerListener(new HandleEvent());
        try {
            @SuppressWarnings("unused")
            ClickType swapOffhand = ClickType.valueOf("SWAP_OFFHAND");  //1.16+
            registerListener(new InventoryF());
        } catch (Throwable ignored) {
            getLogger().warning("您的服务器不支持在玩家背包内按F整理，该功能已被禁用");
        }
    }

    public void initXPLib() {
        Plugin xpLib = getServer().getPluginManager().getPlugin("XPLib");
        if (xpLib != null) {  //装了XPLib
            if (xpLib.getDescription().getAuthors().contains("XPYEX")) {  //是不是我写的
                getLogger().info("XPLib已退出历史舞台，不再需要安装它，即将自动删除");
                getServer().getPluginManager().disablePlugin(xpLib);
                if (xpLib.getClass().getClassLoader() instanceof URLClassLoader) {
                    try {
                        File file = MethodUtil.executeInstanceMethod(xpLib, "getFile");
                        ((URLClassLoader) xpLib.getClass().getClassLoader()).close();  //直接准备删了

                        if (file.delete()) getLogger().info("已删除XPLib文件: " + file.getAbsolutePath());
                    } catch (IOException | ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        getLogger().info("当前服务端核心版本: " + getServer().getBukkitVersion());
        saveResource("minecraft/zh_cn.json", false);
        registerListener(new HandleMenu());
        getServer().getScheduler().runTaskTimerAsynchronously(getInstance(), () -> {
            for (Menu menu : Menu.getMenus().values()) {
                menu.updateInventory();
            }
        }, 0L, 5L);
        getLogger().info("已加载");
    }
}
