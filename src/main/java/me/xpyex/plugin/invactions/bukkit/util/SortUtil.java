package me.xpyex.plugin.invactions.bukkit.util;

import java.util.ArrayList;
import java.util.TreeMap;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.xplib.bukkit.api.Pair;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SortUtil {

    public static void sortPlayerInv(PlayerInventory inv) {
        Inventory i = Bukkit.createInventory(inv.getHolder(), 27);
        int start_p = 9;
        for (int start_i = 0; start_i < 27; start_i++) {
            i.setItem(start_i, inv.getItem(start_p));
            start_p++;
        }
        sortInv(i);
        start_p = 9;
        for (int start_i = 0; start_i < 27; start_i++) {
            inv.setItem(start_p, i.getItem(start_i));
            start_p++;
        }
    }

    public static void sortInv(Inventory inv) {
        if (inv instanceof PlayerInventory) {
            sortPlayerInv((PlayerInventory) inv);
            return;
        }

        TreeMap<String, Pair<ItemStack, Integer>> items = new TreeMap<>();
        for (ItemStack is0 : inv.getStorageContents()) {
            if (is0 == null) {
                continue;
            }
            ItemStack is = new ItemStack(is0);
            int amount = is.getAmount();
            is.setAmount(1);
            if (items.containsKey(is.toString())) {
                items.put(is.toString(), new Pair<>(is, items.get(is.toString()).getValue() + amount));
            } else {
                items.put(is.toString(), new Pair<>(is, amount));
            }
        }

        TreeMap<ItemType, ArrayList<Pair<ItemStack, Integer>>> computed = new TreeMap<>();

        for (ItemType t : ItemType.values()) {
            computed.put(t, new ArrayList<>());
        }
        
        items.values().forEach((pair) -> {
            ItemType sortType;

            if (pair.getKey().getType().isRecord())  //??????
                sortType = ItemType.RECORD;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "RAIL"))  //??????
                sortType = ItemType.RAIL;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_WOOL"))
                sortType = ItemType.WOOL;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_CARPET"))
                sortType = ItemType.CARPET;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_SLAB"))  //??????
                sortType = ItemType.SLAB;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_STAIRS"))  //??????
                sortType = ItemType.STAIR;

            else if (pair.getKey().getType() == Material.LADDER)  //??????
                sortType = ItemType.LADDER;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "BOAT"))  //???
                sortType = ItemType.BOAT;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "MINECART"))  //??????
                sortType = ItemType.MINE_CART;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_LOG", "_WOOD"))  //??????
                sortType = ItemType.LOG;

            else if (StrUtil.containsIgnoreCaseOr(pair.getKey().getType().toString(), "_FENCE"))  //??????????????????
                sortType = ItemType.FENCE;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_SWORD", "BOW", "TRIDENT"))  //???????????????????????????
                sortType = ItemType.WEAPON;

            else if (pair.getKey().getType() == Material.ENCHANTED_BOOK)  //?????????
                sortType = ItemType.ENCHANTED_BOOK;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS"))  //??????
                sortType = ItemType.ARMOR;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_AXE", "_PICKAXE", "_SHOVEL", "_HOE", "FISHING_ROD"))  //??????
                sortType = ItemType.TOOL;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_HEAD", "_SKULL"))  //??????
                sortType = ItemType.HEAD;

            else if (pair.getKey().getType().isEdible())  //??????
                sortType = ItemType.FOOD;

            else if (StrUtil.containsIgnoreCaseOr(pair.getKey().getType().toString(), "ARROW"))
                sortType = ItemType.ARROW;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_PLANKS"))
                sortType = ItemType.PLANKS;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "COD", "SALMON", "FISH"))  //???
                sortType = ItemType.FISH;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_INGOT"))  //???
                sortType = ItemType.INGOT;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_NUGGET"))  //???
                sortType = ItemType.NUGGET;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_ORE"))  //??????
                sortType = ItemType.ORE_BLOCK;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_TERRACOTTA", "_CONCRETE"))  //??????????????????
                sortType = ItemType.CRAY;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "POTION"))  //??????
                sortType = ItemType.POTION;

            else if (StrUtil.startsWithIgnoreCaseOr(pair.getKey().getType().toString(), "RAW_"))
                sortType = ItemType.RAW_ORE;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "BUCKET"))
                sortType = ItemType.BUCKET;

            else if (pair.getKey().getType().isBlock())  //????????????
                sortType = ItemType.BLOCK;

            else
                sortType = ItemType.OTHER;


            computed.get(sortType).add(pair);
            //????????????
        });

        //????????????
        int slot = 0;
        inv.clear();
        for (ArrayList<Pair<ItemStack, Integer>> list : computed.values()) {
            for (Pair<ItemStack, Integer> pair : list) {
                int amount = pair.getValue();
                while (amount > pair.getKey().getMaxStackSize()) {
                    ItemStack result = new ItemStack(pair.getKey());
                    result.setAmount(result.getMaxStackSize());
                    inv.setItem(slot, result);
                    amount = amount - result.getMaxStackSize();
                    slot++;
                }
                if (amount <= pair.getKey().getMaxStackSize() && amount > 0) {
                    ItemStack result = new ItemStack(pair.getKey());
                    result.setAmount(amount);
                    inv.setItem(slot, result);
                    slot++;
                }
            }
        }
    }

    public static void replaceTool(Player p, ItemStack before) {
        EquipmentSlot slot = ItemUtil.equals(before, p.getInventory().getItemInMainHand()) ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        if (SettingsUtil.getSetting(p, "ReplaceBrokenTool")) {  //???????????????????????????????????????
            Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> {
                if (p.getInventory().getItem(slot).getType() == Material.AIR) {
                    for (ItemStack content : p.getInventory().getContents()) {  //???????????????
                        if (content == null) continue;

                        if (ItemUtil.equals(content, before)) {
                            ItemStack copied = new ItemStack(content);
                            p.getInventory().setItem(slot, copied);
                            content.setAmount(0);
                            MsgUtil.sendActionBar(p, "&a???????????????????????????????????????. " + SettingsUtil.SETTING_HELP);
                            return;
                        }
                    }
                }
            }, 1L);
        }
    }
}
