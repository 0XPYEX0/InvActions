package me.xpyex.plugin.invactions.bukkit.module;

import java.util.Random;
import me.xpyex.lib.xplib.bukkit.inventory.ItemUtil;
import me.xpyex.lib.xplib.bukkit.version.VersionUtil;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EggCatcher extends RootModule {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static void catchEntity(Entity entity) {
        Material spawnEggType = Material.getMaterial(entity.getType() + "_SPAWN_EGG");
        if (spawnEggType != null) {
            entity.getWorld().dropItem(entity.getLocation(), ItemUtil.getItemStack(spawnEggType, entity.getName()));
            entity.remove();
        }
    }

    @Override
    protected boolean canLoad() {
        return VersionUtil.getMainVersion() >= 13;
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!serverEnabled()) return;

        if (event.getHitEntity() != null) {
            if (event.getEntity().getType() == EntityType.EGG) {
                if (event.getEntity().getShooter() instanceof Player) {
                    Player p = (Player) event.getEntity().getShooter();
                    if (playerEnabled(p)) {
                        Integer chance = InvActionsServerConfig.getCurrent().getEggCatcher_Chance().get(event.getHitEntity().getType().toString());
                        if (chance == null) {
                            catchEntity(event.getHitEntity());
                        } else if (chance >= 0 && chance <= 100) {
                            if (RANDOM.nextInt(100) < chance) {
                                catchEntity(event.getHitEntity());
                            }
                        }
                    }
                }
            }
        }
    }
}
