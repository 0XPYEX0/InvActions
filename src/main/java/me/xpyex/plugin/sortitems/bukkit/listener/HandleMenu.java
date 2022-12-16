package me.xpyex.plugin.sortitems.bukkit.listener;

import com.google.gson.JsonObject;
import me.xpyex.plugin.sortitems.bukkit.SortItems;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class HandleMenu implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasMetadata("SortItems-Menu")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            ItemStack currentItem = new ItemStack(event.getCurrentItem());
            Bukkit.getScheduler().runTaskAsynchronously(SortItems.getInstance(), () -> {
                if (currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName()) {
                    JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getWhoClicked().getUniqueId());
                    if (currentItem.getItemMeta().getDisplayName().equals(MsgUtil.getColorMsg("&a自动补充道具"))) {
                        boolean futureMode = !o.get("ReplaceBrokenTool").getAsBoolean();
                        String modeStr = futureMode ? "&a启用" : "&c禁用";
                            o.addProperty("ReplaceBrokenTool", futureMode);
                            ConfigUtil.saveConfig(SortItems.getInstance(), "players/" + event.getWhoClicked().getUniqueId(), GsonUtil.parseStr(o), true);
                        Bukkit.getScheduler().runTaskLater(SortItems.getInstance(), () -> {
                            event.getWhoClicked().getOpenInventory().setItem(10, ItemUtil.getItemStack((o.get("ReplaceBrokenTool").getAsBoolean() ? Material.GREEN_WOOL : Material.RED_WOOL), "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: " + modeStr));
                        }, 1L);
                    } else if (currentItem.getItemMeta().getDisplayName().equals(MsgUtil.getColorMsg("&a自动穿戴盔甲"))) {
                        boolean futureMode = !o.get("ReplaceBrokenArmor").getAsBoolean();
                        String modeStr = futureMode ? "&a启用" : "&c禁用";
                        o.addProperty("ReplaceBrokenArmor", futureMode);
                        ConfigUtil.saveConfig(SortItems.getInstance(), "players/" + event.getWhoClicked().getUniqueId(), GsonUtil.parseStr(o), true);
                        Bukkit.getScheduler().runTaskLater(SortItems.getInstance(), () -> {
                            event.getWhoClicked().getOpenInventory().setItem(11, ItemUtil.getItemStack((o.get("ReplaceBrokenArmor").getAsBoolean() ? Material.GREEN_WOOL : Material.RED_WOOL), "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: " + modeStr));
                        }, 1L);
                    } else if (currentItem.getItemMeta().getDisplayName().equals(MsgUtil.getColorMsg("&6重载所有玩家的配置文件"))) {
                        Bukkit.getScheduler().runTask(SortItems.getInstance(), () -> {
                            Bukkit.dispatchCommand(event.getWhoClicked(), "sortItems reload");
                        });
                    }
                }
            });
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        event.getPlayer().removeMetadata("SortItems-Menu", SortItems.getInstance());
        //
    }
}
