package me.xpyex.plugin.invactions.bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.module.HandleEvent;
import me.xpyex.plugin.invactions.bukkit.module.InventoryF;
import me.xpyex.plugin.invactions.bukkit.module.RootModule;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.lib.xplib.bukkit.config.ConfigUtil;
import me.xpyex.lib.xplib.bukkit.core.XPPlugin;
import me.xpyex.lib.xplib.bukkit.inventory.HandleMenu;
import me.xpyex.lib.xplib.bukkit.inventory.Menu;
import me.xpyex.lib.xplib.bukkit.version.VersionUtil;
import me.xpyex.lib.xplib.util.reflect.ClassUtil;
import me.xpyex.lib.xplib.util.reflect.MethodUtil;
import me.xpyex.lib.xplib.util.value.ValueUtil;
import me.xpyex.lib.xplib.util.version.UpdateUtil;
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
        info("已卸载");
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
        info("已注册监听器");

        registerCmd("InvActions", new HandleCmd());
        info("已注册命令");

        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            hookBStats(17118);
            info("与bStats挂钩");
        });

        getServer().getScheduler().runTaskAsynchronously(getInstance(), () -> {
            info("&b开始检查更新");
            ValueUtil.optional(UpdateUtil.getUpdateFromGitHub(getInstance()), ver -> {
                info("&a当前插件版本: &r" + getInstance().getDescription().getVersion() + " ,有一个更新的版本: " + ver);
                info("&e前往&r https://gitee.com/XPYEX/InvActions/releases &e下载吧！");
            }, () -> info("&a当前版本已是最新！"));
        });

        getServer().getScheduler().runTaskAsynchronously(getInstance(), this::checkHybrid);

        info("已加载");
        info("感谢使用InvActions. 本插件在&6&oGitHub开源&r，谨防受骗. 作者QQ1723275529");
        info("&eGitHub:&r https://github.com/0XPYEX0/InvActions");
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
            warn("&c你目前运行在具有 &r" + type + " &c的Hybrid服务端.");
            warn("&6由于Mod功能的实现方式影响，插件&e&l可能&r&6不会那么“正常”地运行，包括但不限于功能失效");
        }
    }

    public boolean initCheck() {
        initXPLib();

        if (VersionUtil.getMainVersion() < 9) {
            error("&c本插件需要Minecraft至少为1.9才可运行");
            error("&c很遗憾，您的服务器不满足此条件...");
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
                            || (moduleClass.getModifiers() & Modifier.ABSTRACT) > 0
                    ) continue;

                    try {
                        moduleClass.getConstructor().newInstance();
                    } catch (Throwable e) {
                        if (InvActionsServerConfig.getConfig().Debug) e.printStackTrace();
                        warn("&c加载模块 &r" + moduleClass.getSimpleName() + " &c时出错: &e" + e);
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
            warn("&c您的服务器 &4&l不支持 &c在玩家背包内按F整理&r，该功能已被禁用");
        }
    }

    public void initXPLib() {
        Plugin xpLib = getServer().getPluginManager().getPlugin("XPLib");
        if (xpLib != null) {  //装了XPLib
            if (xpLib.getDescription().getAuthors().contains("XPYEX")) {  //是不是我写的
                info("&6XPLib已退出历史舞台，不再需要安装它");
                if (xpLib.getClass().getClassLoader() instanceof URLClassLoader) {
                    URLClassLoader classLoader = (URLClassLoader) xpLib.getClass().getClassLoader();
                    xpLib.getLogger().info("即将自动删除");
                    try {
                        File file = MethodUtil.executeInstanceMethod(xpLib, "getFile");
                        File folderFile = xpLib.getDataFolder();

                        getServer().getPluginManager().disablePlugin(xpLib);
                        classLoader.close();

                        if (file.delete()) info("&e已删除XPLib文件: &r" + file.getAbsolutePath());
                        if (folderFile.delete()) info("&e已删除XPLib目录: &r" + file.getAbsolutePath());
                    } catch (IOException | ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        info("&b当前服务端核心版本: &r" + getServer().getBukkitVersion());
        saveResource("minecraft/zh_cn.json", false);
        registerListener(new HandleMenu());
        getServer().getScheduler().runTaskTimerAsynchronously(getInstance(), () -> {
            for (Menu menu : Menu.getMenus().values()) {
                menu.updateInventory();
            }
        }, 0L, 5L);
        info("已加载");
    }
}
