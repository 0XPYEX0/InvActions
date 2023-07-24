package me.xpyex.plugin.invactions.bukkit.enums;

import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ItemType {
    TOOL,  //工具
    WEAPON,  //武器
    ARROW,
    ARMOR,  //装备
    FOOD,  //食物
    FISH,  //鱼
    RECORD,  //唱片
    ENCHANTED_BOOK,  //附魔书
    RAIL,  //铁轨
    MINE_CART,  //矿车
    LOG,  //原木
    PLANKS,  //木板
    FENCE,  //栅栏
    SLAB,  //半砖
    STAIR,  //楼梯
    LADDER,  //梯子
    BOAT,  //船
    CRAY,  //黏土
    WOOL,
    CARPET,
    ORE_BLOCK,  //矿石
    RAW_ORE,  //粗矿
    INGOT,  //锭
    NUGGET,  //金粒铁粒等
    BLOCK,  //方块
    HEAD,  //头颅
    POTION,  //药水
    BUCKET,  //桶
    OTHER;

    public static boolean isAir(Material material) {
        try {
            return material.isAir();
        } catch (NoSuchMethodError ignored) {
            return material == Material.AIR;
        }
    }

    @NotNull
    public static ItemType getType(ItemStack stack) {
        if (stack.getType().isRecord())  //唱片
            return ItemType.RECORD;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "RAIL"))  //铁轨
            return ItemType.RAIL;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_WOOL"))
            return ItemType.WOOL;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_CARPET"))
            return ItemType.CARPET;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_SLAB"))  //半砖
            return ItemType.SLAB;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_STAIRS"))  //楼梯
            return ItemType.STAIR;

        else if (stack.getType() == Material.LADDER)  //梯子
            return ItemType.LADDER;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "BOAT"))  //船
            return ItemType.BOAT;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "MINECART"))  //矿车
            return ItemType.MINE_CART;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_LOG", "_WOOD"))  //原木
            return ItemType.LOG;

        else if (StrUtil.containsIgnoreCaseOr(stack.getType().toString(), "_FENCE"))  //栅栏、栅栏门
            return ItemType.FENCE;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_SWORD", "BOW", "TRIDENT"))  //剑、弓、弩、三叉戟
            return ItemType.WEAPON;

        else if (stack.getType() == Material.ENCHANTED_BOOK)  //附魔书
            return ItemType.ENCHANTED_BOOK;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS"))  //防具
            return ItemType.ARMOR;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_AXE", "_PICKAXE", "_SHOVEL", "_HOE", "FISHING_ROD"))  //工具
            return ItemType.TOOL;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_HEAD", "_SKULL"))  //头颅
            return ItemType.HEAD;

        else if (stack.getType().isEdible())  //食物
            return ItemType.FOOD;

        else if (StrUtil.containsIgnoreCaseOr(stack.getType().toString(), "ARROW"))
            return ItemType.ARROW;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_PLANKS"))
            return ItemType.PLANKS;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "COD", "SALMON", "FISH"))  //鱼
            return ItemType.FISH;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_INGOT"))  //锭
            return ItemType.INGOT;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_NUGGET"))  //粒
            return ItemType.NUGGET;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_ORE"))  //矿石
            return ItemType.ORE_BLOCK;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "_TERRACOTTA", "_CONCRETE"))  //陶瓦、混凝土
            return ItemType.CRAY;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "POTION"))  //药水
            return ItemType.POTION;

        else if (StrUtil.startsWithIgnoreCaseOr(stack.getType().toString(), "RAW_"))
            return ItemType.RAW_ORE;

        else if (StrUtil.endsWithIgnoreCaseOr(stack.getType().toString(), "BUCKET"))
            return ItemType.BUCKET;

        else if (stack.getType().isBlock())  //其余方块
            return ItemType.BLOCK;

        else
            return ItemType.OTHER;
    }
}
