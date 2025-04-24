package me.xpyex.lib.xplib.bukkit.strings;

import com.google.gson.JsonObject;
import me.xpyex.lib.xplib.bukkit.config.ConfigUtil;
import me.xpyex.lib.xplib.util.RootUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MsgUtil extends RootUtil {
    /**
     * 获取MC的彩色文本
     *
     * @param msg 原文本
     * @return 彩色文本
     */
    public static String getColorMsg(String msg) {
        if (msg == null) return "";  //不检查trim，允许返回空格字符串

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * 向玩家发送ActionBar消息
     *
     * @param p    目标玩家
     * @param text 消息
     */
    public static void sendActionBar(Player p, String text) {
        try {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getColorMsg(text)));
        } catch (Throwable e) {
            p.sendMessage(getColorMsg(text));
        }
    }

    /**
     * 在后台输出Debug消息
     *
     * @param plugin 哪个插件要求发送，需要在config.json内将debug项设为true
     * @param info   消息
     */
    public static void debugLog(Plugin plugin, String info) {
        if (ConfigUtil.getConfig(plugin, JsonObject.class) != null && ConfigUtil.getConfig(plugin, JsonObject.class).has("Debug") && ConfigUtil.getConfig(plugin, JsonObject.class).get("Debug").getAsBoolean())
            plugin.getLogger().info("[Debug] " + info);
    }

    /**
     * 在后台输出Debug消息
     *
     * @param plugin    哪个插件要求发送，需要在config.json内将debug项设为true
     * @param throwable 出现的异常
     */
    public static void debugLog(Plugin plugin, Throwable throwable) {
        if (ConfigUtil.getConfig(plugin, JsonObject.class) != null && ConfigUtil.getConfig(plugin, JsonObject.class).has("Debug") && ConfigUtil.getConfig(plugin, JsonObject.class).get("Debug").getAsBoolean())
            throwable.printStackTrace();
    }

    /**
     * 在后台输出Debug消息
     *
     * @param plugin    哪个插件要求发送，需要在config.json内将debug项设为true
     * @param errMsg    描述异常发生的原因
     * @param throwable 出现的异常
     */
    public static void debugLog(Plugin plugin, String errMsg, Throwable throwable) {
        if (ConfigUtil.getConfig(plugin, JsonObject.class) != null && ConfigUtil.getConfig(plugin, JsonObject.class).has("Debug") && ConfigUtil.getConfig(plugin, JsonObject.class).get("Debug").getAsBoolean())
            new Throwable(errMsg, throwable).printStackTrace();
    }

    /**
     * 向服务器广播，消息自动染色
     *
     * @param msg 消息
     */
    public static void broadcast(String msg) {
        Bukkit.broadcastMessage(getColorMsg(msg));
        //
    }
}
