package me.xpyex.plugin.sortitems.bukkit.listener;

import com.google.gson.JsonObject;
import java.util.HashSet;
import me.xpyex.plugin.sortitems.bukkit.SortItems;
import me.xpyex.plugin.sortitems.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.NameUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class HandleEvent implements Listener {
    private final static HashSet<Material> IGNORES = new HashSet<>();
    static {
        for (Material m : Material.values()) {
            switch (m) {
                case TORCH:  //火把
                case WALL_TORCH:
                case SOUL_TORCH:  //灵魂火把
                case SOUL_WALL_TORCH:
                case REDSTONE_WIRE:  //红石线
                case REDSTONE_TORCH:  //红石火把
                case REDSTONE_WALL_TORCH:
                case SNOW:  //雪层
                case LAVA:  //岩浆
                case WATER:  //水
                    IGNORES.add(m);
                    break;
            }
            if (m.toString().endsWith("_BUTTON")) {  //按钮
                IGNORES.add(m);
            } else if (m.toString().endsWith("_PRESSURE_PLATE")) {  //压力板
                IGNORES.add(m);
            } else if (m.isAir()) {  //任意空气
                IGNORES.add(m);
            } else if (m.toString().endsWith("_SIGN")) {  //牌子
                IGNORES.add(m);
            } else if (m.toString().contains("VINE")) {  //藤蔓系列
                IGNORES.add(m);
            } else if (m.toString().endsWith("_TRAPDOOR")) {  //活板门
                IGNORES.add(m);
            } else if (m.toString().endsWith("_FENCE")) {  //栅栏
                IGNORES.add(m);
            } else if (m.toString().endsWith("FIRE")) {  //火、灵魂火
                IGNORES.add(m);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPressFWithoutInv(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().isSneaking()) {  //Shift+F
            Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
            if (target.getState() instanceof Container) {  //看向容器了
                event.setCancelled(true);
                SortUtil.sortInv(((Container) target.getState()).getInventory());
                event.getPlayer().sendMessage(MsgUtil.getColorMsg("&a已整理你看向的 &f" + NameUtil.getTranslationName(target.getType())));
            } else if (event.getPlayer().getLocation().getPitch() == 90f) {
                event.setCancelled(true);
                SortUtil.sortInv(event.getPlayer().getInventory());
                event.getPlayer().sendMessage(MsgUtil.getColorMsg("&a已整理你的背包"));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemBreak(PlayerItemBreakEvent event) {
        EquipmentSlot slot;
        if (event.getBrokenItem().equals(event.getPlayer().getInventory().getItemInOffHand())) {
            slot = EquipmentSlot.OFF_HAND;
        }
        else if (event.getBrokenItem().equals(event.getPlayer().getInventory().getHelmet())) {
            slot = EquipmentSlot.HEAD;
        }
        else if (event.getBrokenItem().equals(event.getPlayer().getInventory().getChestplate())) {
            slot = EquipmentSlot.CHEST;
        }
        else if (event.getBrokenItem().equals(event.getPlayer().getInventory().getLeggings())) {
            slot = EquipmentSlot.LEGS;
        }
        else if (event.getBrokenItem().equals(event.getPlayer().getInventory().getBoots())) {
            slot = EquipmentSlot.FEET;
        } else {
            slot = EquipmentSlot.HAND;
        }

        JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId());


        switch (slot) {
            case HAND:
            case OFF_HAND:
                if (!o.get("ReplaceBrokenTool").getAsBoolean()) {  //如果玩家未开启替换手中道具
                    return;
                }
            default:
                if (!o.get("ReplaceBrokenArmor").getAsBoolean()) {  //如果玩家未开启替换盔甲
                    return;
                }
        }
        ItemStack brokenItem = new ItemStack(event.getBrokenItem());

        Bukkit.getScheduler().runTaskLater(SortItems.getInstance(), () -> {
            for (ItemStack content : event.getPlayer().getInventory().getContents()) {
                if (content == null) continue;

                if (content.getType() == brokenItem.getType()) {  //同一种类型的道具
                    ItemStack out = new ItemStack(content);
                    content.setAmount(0);
                    event.getPlayer().getInventory().setItem(slot, out);
                    MsgUtil.sendActionBar(event.getPlayer(), "&a您的道具已损毁，从背包补全. &e该功能在 &f/SortItems &e中调整");
                    return;
                }
            }
        }, 2L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

            if (event.getItem() == null) return;

            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta() instanceof Damageable)
                return;  //可破坏的物品留给上面处理，不在这处理

            ItemStack before = new ItemStack(event.getItem());

            Bukkit.getScheduler().runTaskLater(SortItems.getInstance(), () -> {

                JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId());
                if (o.get("ReplaceBrokenTool").getAsBoolean()) {  //如果玩家开启了替换手中道具
                    if (event.getPlayer().getInventory().getItem(event.getHand()).getType() == Material.AIR) {
                        for (ItemStack content : event.getPlayer().getInventory().getContents()) {  //不遍历盔甲
                            if (content == null) continue;

                            if (ItemUtil.equals(content, before)) {
                                ItemStack copied = new ItemStack(content);
                                event.getPlayer().getInventory().setItem(event.getHand(), copied);
                                content.setAmount(0);
                                MsgUtil.sendActionBar(event.getPlayer(), "&a您的道具已用尽，从背包补全. &e该功能在 &f/SortItems &e中调整");
                                return;
                            }
                        }
                    }
                }
            }, 1L);
    }
}
