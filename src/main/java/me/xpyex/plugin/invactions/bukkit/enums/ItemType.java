package me.xpyex.plugin.invactions.bukkit.enums;

import org.bukkit.Material;

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

    public enum ToolType {
        AXE,
        PICKAXE,
        SHOVEL,
        HOE,
        SHEARS,
        SWORD,
        UNKNOWN
    }
}
