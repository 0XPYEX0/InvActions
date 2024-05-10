package me.xpyex.plugin.invactions.bukkit.listener;

import java.util.UUID;
import java.util.WeakHashMap;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.invactions.bukkit.util.SchedulerUtil;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DynamicLight implements Listener {
    private static final WeakHashMap<UUID, Location> PLAYER_DYNAMIC_LIGHT = new WeakHashMap<>();
    private static final String[] LIGHTS = {"LANTERN", "TORCH", "GLOW", "ShroomLight", "FrogLight", "END_ROD", "CampFire", "LAVA"};
    private static final BlockData LIGHT_DATA;

    static {
        Material light = Material.getMaterial("LIGHT");  //1.17的光源方块. 除了火把外的大部分光源都有碰撞箱，所以选火把
        LIGHT_DATA = Bukkit.createBlockData(light != null ? light : Material.TORCH);
    }

    public static void registerTask() {
        SchedulerUtil.runTaskTimerAsync(task -> {
            if (InvActionsServerConfig.getConfig().DynamicLight) {  //服务端启用动态光源
                for (Player player : Bukkit.getOnlinePlayers()) {  //遍历玩家
                    if (SettingsUtil.getConfig(player).DynamicLight) {  //玩家启用动态光源
                        Material toolType = player.getInventory().getItemInMainHand().getType();
                        Material offhandType = player.getInventory().getItemInOffHand().getType();

                        if (StrUtil.containsIgnoreCaseOr(toolType.toString(), LIGHTS) || StrUtil.containsIgnoreCaseOr(offhandType.toString(), LIGHTS)) {  //玩家手里的东西是光源的情况
                            Location location = player.getEyeLocation();
                            if (!ItemType.isAir(location.getBlock().getType())) {  //不在有方块的地方模拟光源了，观感不好且影响游泳
                                //保持动态光源在上一次的位置，直到玩家走回空气
                                continue;
                            }
                            if (!PLAYER_DYNAMIC_LIGHT.containsKey(player.getUniqueId())) {
                                MsgUtil.sendActionBar(player, "&a你目前手持光源，动态光源启用. " + InvActionsServerConfig.SETTING_HELP);
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

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (!InvActionsServerConfig.getConfig().DynamicLight) return;

        Projectile projectile = event.getEntity();
        final Location[] loc = {projectile.getLocation().clone()};
        SchedulerUtil.runTaskTimerAsync(task -> {
            if (projectile.isValid() && projectile.getFireTicks() > 0) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (SettingsUtil.getConfig(player).DynamicLight) {
                        if (player.getLocation().getWorld().equals(loc[0].getWorld())) {
                            player.sendBlockChange(loc[0], loc[0].getBlock().getBlockData());
                            loc[0] = projectile.getLocation().clone();
                            if (ItemType.isAir(loc[0].getBlock().getType())) {
                                player.sendBlockChange(loc[0], LIGHT_DATA);
                            }
                        }
                    }
                });
                return;
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (loc[0].getWorld().equals(player.getWorld())) {
                    //给所有人复原
                    player.sendBlockChange(loc[0], loc[0].getBlock().getBlockData());
                }
            });
            task.cancel();
        }, 5L, 5L);
    }
}
