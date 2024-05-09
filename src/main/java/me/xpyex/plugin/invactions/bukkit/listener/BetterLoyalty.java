package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BetterLoyalty implements Listener {
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!InvActionsServerConfig.getConfig().BetterLoyalty) {  //服务器未启用
            return;
        }
        if (event.getEntity() instanceof Trident) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player shooter = (Player) event.getEntity().getShooter();
                if (!SettingsUtil.getConfig(shooter).BetterLoyalty) {  //玩家未启用
                    return;
                }
                if (((Trident) event.getEntity()).getItem().getEnchantments().containsKey(Enchantment.LOYALTY)) {
                    //附魔忠诚了
                    int serverDis = (Bukkit.getServer().getViewDistance() - 1) * 16;  //不会有人视距设为1吧？不会吧不会吧
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!event.getEntity().isValid()) {  //三叉戟跑出视距外了，不再处理，也没办法处理
                                cancel();
                                return;
                            }
                            if (!shooter.isOnline()) {  //玩家离线，把三叉戟传送到玩家离线位置
                                event.getEntity().teleport(shooter.getWorld().getHighestBlockAt(shooter.getLocation()).getLocation());
                                cancel();
                                return;
                            }
                            double distance = getDistance(event.getEntity(), shooter);
                            if (distance >= serverDis) {  //距离即将过远，把三叉戟传送回来
                                event.getEntity().teleport(shooter.getWorld().getHighestBlockAt(shooter.getLocation()).getLocation());
                                MsgUtil.sendActionBar(shooter, "&a三叉戟即将过远，已强制其开始返航. " + InvActionsServerConfig.SETTING_HELP);
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(InvActions.getInstance(), 5L, 5L);
                }
            }
        }
    }

    private double getDistance(Entity e1, Entity e2) {
        if (e1 == null || e2 == null) {
            return 0;
        }
        double x1 = e1.getLocation().getX();
        double z1 = e1.getLocation().getZ();
        double x2 = e2.getLocation().getX();
        double z2 = e2.getLocation().getZ();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2));  //√[(x2-x1)²+(z2-z1)²] 两点间距离 忽略高度y
    }
}