package me.xpyex.plugin.sortitems.bukkit.command;

import com.google.gson.JsonObject;
import me.xpyex.plugin.sortitems.bukkit.SortItems;
import me.xpyex.plugin.xplib.bukkit.api.InvSetter;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class HandleCmd implements CommandExecutor {
    private static final JsonObject DEFAULT_SETTINGS = new JsonObject();
    static {
        DEFAULT_SETTINGS.addProperty("ReplaceBrokenTool", true);
        DEFAULT_SETTINGS.addProperty("ReplaceBrokenArmor", true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(SortItems.getInstance(), () -> {
            if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("SortItems.admin")) {
                    sender.sendMessage("你没有权限");
                    return;
                }
                ConfigUtil.reload(SortItems.getInstance());
                sender.sendMessage("重载完成");
                return;
            }
            if (sender instanceof Player) {
                ConfigUtil.saveConfig(SortItems.getInstance(), "players/" + ((Player) sender).getUniqueId(), GsonUtil.parseStr(DEFAULT_SETTINGS), false);
                InvSetter setter = new InvSetter(((Player) sender), "F键整理-设定-" + sender.getName(), "#########", "#12    A#", "#########");
                setter.setSign("#", ItemUtil.getItemStack(Material.BLACK_STAINED_GLASS, " "));
                setter.setSign(" ", new ItemStack(Material.AIR));
                JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + ((Player) sender).getUniqueId());
                setter.setSign("1", ItemUtil.getItemStack((o.get("ReplaceBrokenTool").getAsBoolean() ? Material.GREEN_WOOL : Material.RED_WOOL), "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: " + (o.get("ReplaceBrokenTool").getAsBoolean() ? "&a启用" : "&c禁用")));
                setter.setSign("2", ItemUtil.getItemStack((o.get("ReplaceBrokenArmor").getAsBoolean() ? Material.GREEN_WOOL : Material.RED_WOOL), "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: " + (o.get("ReplaceBrokenArmor").getAsBoolean() ? "&a启用" : "&c禁用")));
                setter.setSign("A", sender.hasPermission("SortItems.admin") ? ItemUtil.getItemStack(Material.ORANGE_WOOL, "&6重载所有玩家的配置文件") : new ItemStack(Material.AIR));
                Bukkit.getScheduler().runTask(SortItems.getInstance(), () -> {
                    ((Player) sender).openInventory(setter.getInv());
                    ((Player) sender).setMetadata("SortItems-Menu", new FixedMetadataValue(SortItems.getInstance(), true));
                });
            }
        });
        return true;
    }
}
