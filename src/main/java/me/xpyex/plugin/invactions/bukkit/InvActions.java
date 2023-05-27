package me.xpyex.plugin.invactions.bukkit;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.UUID;
import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.listener.AutoFarmer;
import me.xpyex.plugin.invactions.bukkit.listener.AutoTool;
import me.xpyex.plugin.invactions.bukkit.listener.CraftDrop;
import me.xpyex.plugin.invactions.bukkit.listener.HandleEvent;
import me.xpyex.plugin.invactions.bukkit.listener.InventoryF;
import me.xpyex.plugin.invactions.bukkit.listener.QuickDrop;
import me.xpyex.plugin.invactions.bukkit.listener.QuickMove;
import me.xpyex.plugin.invactions.bukkit.listener.ReplaceBroken;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.api.Version;
import me.xpyex.plugin.xplib.bukkit.core.XPPlugin;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import me.xpyex.plugin.xplib.bukkit.util.version.UpdateUtil;
import me.xpyex.plugin.xplib.bukkit.util.version.VersionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public final class InvActions extends XPPlugin {
    public static final String[] LIGHTS = {"LANTERN", "TORCH", "GLOW", "ShroomLight", "FrogLight", "END_ROD", "CampFire", "LAVA"};
    private static final String XPLIB_VER = "1.0.8";
    private static final HashMap<UUID, Location> PLAYER_DYNAMIC_LIGHT = new HashMap<>();
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
            String ver = UpdateUtil.getUpdateFromGitee(getInstance());
            if (ver != null) {
                getLogger().info("当前插件版本: " + getInstance().getDescription().getVersion() + " ,有一个更新的版本: " + ver);
                getLogger().info("前往 https://gitee.com/XPYEX/InvActions/releases 下载吧！");
            } else {
                getLogger().info("当前版本已是最新！");
            }
        });

        if (VersionUtil.getMainVersion() >= 13) {  //仅1.13+支持
            Material light = Material.getMaterial("LIGHT");  //1.17的光源方块. 除了火把外的大部分光源都有碰撞箱，所以选火把
            BlockData lightData = getServer().createBlockData(light != null ? light : Material.TORCH);  //对于lambda来说的常量
            getServer().getScheduler().runTaskTimerAsynchronously(getInstance(), () -> {
                if (SettingsUtil.getServerSetting("DynamicLight")) {  //服务端启用动态光源
                    for (Player player : getServer().getOnlinePlayers()) {  //遍历玩家
                        if (SettingsUtil.getSetting(player, "DynamicLight")) {  //玩家启用动态光源
                            Material toolType = player.getInventory().getItemInMainHand().getType();
                            Material offhandType = player.getInventory().getItemInOffHand().getType();

                            if (StrUtil.containsIgnoreCaseOr(toolType.toString(), LIGHTS) || StrUtil.containsIgnoreCaseOr(offhandType.toString(), LIGHTS)) {  //玩家手里的东西是光源的情况
                                Location location = player.getEyeLocation();
                                if (!location.getBlock().getType().toString().contains("AIR")) {  //不在有方块的地方模拟光源了，观感不好且影响游泳
                                    //保持动态光源在上一次的位置，直到玩家走回空气
                                    continue;
                                }
                                if (!PLAYER_DYNAMIC_LIGHT.containsKey(player.getUniqueId())) {
                                    MsgUtil.sendActionBar(player, "&a你目前手持光源，动态光源启用. " + SettingsUtil.SETTING_HELP);
                                    PLAYER_DYNAMIC_LIGHT.put(player.getUniqueId(), location);
                                }
                                Location loc = PLAYER_DYNAMIC_LIGHT.get(player.getUniqueId());  //上一次模拟的方块
                                player.sendBlockChange(loc, loc.getBlock().getBlockData());  //复原上次的方块
                                player.sendBlockChange(location.getBlock().getLocation(), lightData);  //显示当前方块
                                PLAYER_DYNAMIC_LIGHT.put(player.getUniqueId(), location.getBlock().getLocation());
                            } else if (PLAYER_DYNAMIC_LIGHT.containsKey(player.getUniqueId())) {  //没有拿着光源，就不显示动态光源
                                Location loc = PLAYER_DYNAMIC_LIGHT.get(player.getUniqueId());
                                player.sendBlockChange(loc, loc.getBlock().getBlockData());
                                PLAYER_DYNAMIC_LIGHT.remove(player.getUniqueId());
                            }
                        } else if (PLAYER_DYNAMIC_LIGHT.containsKey(player.getUniqueId())) {
                            Location loc = PLAYER_DYNAMIC_LIGHT.get(player.getUniqueId());
                            player.sendBlockChange(loc, loc.getBlock().getBlockData());
                            PLAYER_DYNAMIC_LIGHT.remove(player.getUniqueId());
                        }
                    }
                }
            }, 0L, 5L);
        }

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
    }
}
