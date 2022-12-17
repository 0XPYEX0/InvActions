package me.xpyex.plugin.sortitems.bukkit.util;

import java.util.ArrayList;
import java.util.TreeMap;
import me.xpyex.plugin.sortitems.bukkit.enums.ItemType;
import me.xpyex.plugin.xplib.bukkit.api.Pair;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

            if (pair.getKey().getType().isRecord())  //唱片
                sortType = ItemType.RECORD;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "RAIL"))  //铁轨
                sortType = ItemType.RAIL;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_WOOL"))
                sortType = ItemType.WOOL;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_CARPET"))
                sortType = ItemType.CARPET;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_SLAB"))  //半砖
                sortType = ItemType.SLAB;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_STAIRS"))  //楼梯
                sortType = ItemType.STAIR;

            else if (pair.getKey().getType() == Material.LADDER)  //梯子
                sortType = ItemType.LADDER;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "BOAT"))  //船
                sortType = ItemType.BOAT;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "MINECART"))  //矿车
                sortType = ItemType.MINE_CART;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_LOG", "_WOOD"))  //半砖
                sortType = ItemType.LOG;

            else if (StrUtil.containsIgnoreCaseOr(pair.getKey().getType().toString(), "_FENCE"))  //栅栏、栅栏门
                sortType = ItemType.FENCE;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_SWORD", "BOW", "TRIDENT"))  //剑、弓、弩、三叉戟
                sortType = ItemType.WEAPON;

            else if (pair.getKey().getType() == Material.ENCHANTED_BOOK)  //附魔书
                sortType = ItemType.ENCHANTED_BOOK;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS"))  //防具
                sortType = ItemType.ARMOR;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_AXE", "_PICKAXE", "_SHOVEL", "_HOE", "FISHING_ROD"))  //工具
                sortType = ItemType.TOOL;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_HEAD", "_SKULL"))  //头颅
                sortType = ItemType.HEAD;

            else if (pair.getKey().getType().isEdible())  //食物
                sortType = ItemType.FOOD;

            else if (StrUtil.containsIgnoreCaseOr(pair.getKey().getType().toString(), "ARROW"))
                sortType = ItemType.ARROW;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_PLANKS"))
                sortType = ItemType.PLANKS;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "COD", "SALMON", "FISH"))  //鱼
                sortType = ItemType.FISH;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_INGOT"))  //锭
                sortType = ItemType.INGOT;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_NUGGET"))  //粒
                sortType = ItemType.NUGGET;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_ORE"))  //矿石
                sortType = ItemType.ORE_BLOCK;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "_TERRACOTTA", "_CONCRETE"))  //陶瓦、混凝土
                sortType = ItemType.CRAY;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "POISON"))  //药水
                sortType = ItemType.POISON;

            else if (StrUtil.startsWithIgnoreCaseOr(pair.getKey().getType().toString(), "RAW_"))
                sortType = ItemType.RAW_ORE;

            else if (StrUtil.endsWithIgnoreCaseOr(pair.getKey().getType().toString(), "BUCKET"))
                sortType = ItemType.BUCKET;

            else if (pair.getKey().getType().isBlock())  //其余方块
                sortType = ItemType.BLOCK;

            else
                sortType = ItemType.OTHER;


            computed.get(sortType).add(pair);
            //分类过程
        });

        //整理过程
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
}
