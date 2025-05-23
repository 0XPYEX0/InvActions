package me.xpyex.plugin.invactions.bukkit.module;

import java.util.UUID;
import java.util.WeakHashMap;
import me.xpyex.lib.xplib.bukkit.strings.MsgUtil;
import me.xpyex.lib.xplib.util.strings.StrUtil;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DynamicLight extends RootModule {
    private static final WeakHashMap<UUID, Location> PLAYER_DYNAMIC_LIGHT = new WeakHashMap<>();
    private static final String[] LIGHTS = {"LANTERN", "TORCH", "GLOW", "ShroomLight", "FrogLight", "END_ROD", "CampFire", "LAVA"};
    private static BlockData LIGHT_DATA;

    @Override
    public void registerCustomListener() {
        registerTask();
        //
    }

    @Override
    protected boolean canLoad() {
        try {
            Block.class.getMethod("getBlockData");  //1.13+
            Material light = Material.getMaterial("LIGHT");  //1.17的光源方块.
            LIGHT_DATA = Bukkit.createBlockData(light != null ? light : Material.TORCH);  //除了火把外的大部分光源都有碰撞箱，所以选火把
            return true;
        } catch (NoSuchMethodError | NoSuchMethodException ignored) {
            return false;
        }
    }

    public void registerTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(InvActions.getInstance(), () -> {
            if (serverEnabled()) {  //服务端启用动态光源
                for (Player player : Bukkit.getOnlinePlayers()) {  //遍历玩家
                    if (playerEnabled(player)) {  //玩家启用动态光源
                        Material toolType = player.getInventory().getItemInMainHand().getType();
                        Material offhandType = player.getInventory().getItemInOffHand().getType();

                        if (StrUtil.containsIgnoreCaseOr(toolType.toString(), LIGHTS) || StrUtil.containsIgnoreCaseOr(offhandType.toString(), LIGHTS)) {  //玩家手里的东西是光源的情况
                            Location location = player.getEyeLocation();
                            if (!ItemType.isAir(location.getBlock().getType())) {  //不在有方块的地方模拟光源了，观感不好且影响游泳
                                //保持动态光源在上一次的位置，直到玩家走回空气
                                continue;
                            }
                            if (!PLAYER_DYNAMIC_LIGHT.containsKey(player.getUniqueId())) {
                                MsgUtil.sendActionBar(player, getMessageWithSuffix("light"));
                                PLAYER_DYNAMIC_LIGHT.put(player.getUniqueId(), location);
                            }
                            Location loc = PLAYER_DYNAMIC_LIGHT.get(player.getUniqueId());  //上一次模拟的方块
                            player.sendBlockChange(loc, loc.getBlock().getBlockData());  //复原上次的方块
                            player.sendBlockChange(location.getBlock().getLocation(), LIGHT_DATA);  //显示当前方块
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

    @EventHandler(ignoreCancelled = true)
    public void onShoot(ProjectileLaunchEvent event) {
        if (!serverEnabled()) return;

        Projectile projectile = event.getEntity();
        new BukkitRunnable() {
            Location lastLoc = projectile.getLocation().clone();

            @Override
            public void run() {
                if (projectile.isValid() && projectile.getFireTicks() > 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (playerEnabled(player)) {
                            if (player.getWorld().equals(lastLoc.getWorld())) {
                                player.sendBlockChange(lastLoc, lastLoc.getBlock().getBlockData());
                                lastLoc = projectile.getLocation().clone();
                                if (ItemType.isAir(lastLoc.getBlock().getType())) {
                                    player.sendBlockChange(lastLoc, LIGHT_DATA);
                                }
                            }
                        }
                    });
                    return;
                }
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (lastLoc.getWorld().equals(player.getWorld())) {
                        //给所有人复原
                        player.sendBlockChange(lastLoc, lastLoc.getBlock().getBlockData());
                    }
                });
                cancel();
            }
        }.runTaskTimerAsynchronously(InvActions.getInstance(), 5L, 5L);
    }
}
