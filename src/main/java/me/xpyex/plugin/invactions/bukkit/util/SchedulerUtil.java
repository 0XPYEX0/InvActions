package me.xpyex.plugin.invactions.bukkit.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.xplib.bukkit.util.reflect.MethodUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SchedulerUtil {
    private static Object foliaGlobalScheduler;
    private static Object foliaAsyncScheduler;

    static {
        try {
            foliaGlobalScheduler = MethodUtil.executeClassMethod(Bukkit.class, "getGlobalRegionScheduler");  //Bukkit.getGlobalRegionScheduler()拿到调度器实例
            foliaAsyncScheduler = MethodUtil.executeClassMethod(Bukkit.class, "getAsyncScheduler");  //Bukkit.getAsyncScheduler()拿到调度器实例
            InvActions.getInstance().getLogger().info("您正在使用Folia或其分支作为服务端核心，已尝试启用兼容.");
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static PackagedTask runTaskTimer(Runnable r, long delayTicks, long periodTicks) {
        if (r == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        try {
            if (delayTicks > 0) {
                if (periodTicks > 0) {
                    if (foliaGlobalScheduler == null) {
                        return new PackagedTask(Bukkit.getScheduler().runTaskTimer(InvActions.getInstance(), r, delayTicks, periodTicks));
                    } else {
                        return new PackagedTask(
                            MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> r.run(), delayTicks, periodTicks)
                        );
                    }
                } else {
                    if (foliaAsyncScheduler == null) {
                        return new PackagedTask(Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), r, delayTicks));
                    } else {
                        return new PackagedTask(
                            MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "runDelayed", InvActions.getInstance(), (Consumer<?>) task -> r.run(), delayTicks)
                        );
                    }
                }
            }
            if (periodTicks > 0) {
                if (foliaAsyncScheduler == null) {
                    return new PackagedTask(Bukkit.getScheduler().runTaskTimer(InvActions.getInstance(), r, 1, periodTicks));
                } else {
                    return new PackagedTask(
                        MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> r.run(), 1, periodTicks)
                    );
                }
            }
            if (foliaAsyncScheduler == null) {
                return new PackagedTask(Bukkit.getScheduler().runTask(InvActions.getInstance(), r));
            }
            return new PackagedTask(
                MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "run", InvActions.getInstance(), (Consumer<?>) task -> r.run())
            );
        } catch (ReflectiveOperationException e) {
            InvActions.getInstance().getLogger().severe("无法调用Folia调度器");
            e.printStackTrace();
            return null;
        }
    }

    public static PackagedTask runTaskTimerAsync(Runnable r, long delayTicks, long periodTicks) {
        if (r == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        try {
            if (delayTicks > 0) {
                if (periodTicks > 0) {
                    if (foliaAsyncScheduler == null) {
                        return new PackagedTask(Bukkit.getScheduler().runTaskTimerAsynchronously(InvActions.getInstance(), r, delayTicks, periodTicks));
                    } else {
                        return new PackagedTask(
                            MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> r.run(), delayTicks * 50, periodTicks * 50, TimeUnit.MILLISECONDS)
                        );
                    }
                } else {
                    if (foliaAsyncScheduler == null) {
                        return new PackagedTask(Bukkit.getScheduler().runTaskLaterAsynchronously(InvActions.getInstance(), r, delayTicks));
                    } else {
                        return new PackagedTask(
                            MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runDelayed", InvActions.getInstance(), (Consumer<?>) task -> r.run(), delayTicks * 50, TimeUnit.MILLISECONDS)
                        );
                    }
                }
            }
            if (periodTicks > 0) {
                if (foliaAsyncScheduler == null) {
                    return new PackagedTask(Bukkit.getScheduler().runTaskTimerAsynchronously(InvActions.getInstance(), r, 1, periodTicks));
                } else {
                    MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> r.run(), 50, periodTicks * 50, TimeUnit.MILLISECONDS);
                }
            }
            if (foliaAsyncScheduler == null) {
                return new PackagedTask(Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), r));
            }
            return new PackagedTask(
                MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runNow", InvActions.getInstance(), (Consumer<?>) task -> r.run())
            );
        } catch (ReflectiveOperationException e) {
            InvActions.getInstance().getLogger().severe("无法调用Folia调度器");
            e.printStackTrace();
            return null;
        }
    }

    public static PackagedTask runTask(Runnable r) {
        return runTaskTimer(r, 0, 0);
        //
    }

    public static PackagedTask runTaskLater(Runnable r, long delayTicks) {
        return runTaskTimer(r, delayTicks, 0);
        //
    }

    public static PackagedTask runTaskAsync(Runnable r) {
        return runTaskTimerAsync(r, 0, 0);
        //
    }

    public static PackagedTask runTaskLaterAsync(Runnable r, long delayTicks) {
        return runTaskTimerAsync(r, delayTicks, 0);
        //
    }

    public static void runTask(BukkitRunnable runnable) {
        PackagedTask[] task = new PackagedTask[1];
        task[0] = runTask(() -> {
            runnable.run();
            if (runnable.isCancelled()) {
                task[0].setCancelled(true);
            }
        });
    }

    public static void runTaskAsync(BukkitRunnable runnable) {
        PackagedTask[] task = new PackagedTask[1];
        task[0] = runTaskAsync(() -> {
            runnable.run();
            if (runnable.isCancelled()) {
                task[0].setCancelled(true);
            }
        });
    }

    public static void runTaskLaterAsync(BukkitRunnable runnable, long delayTicks) {
        PackagedTask[] task = new PackagedTask[1];
        task[0] = runTaskLaterAsync(() -> {
            runnable.run();
            if (runnable.isCancelled()) {
                task[0].setCancelled(true);
            }
        }, delayTicks);
    }

    public static void runTaskTimer(BukkitRunnable runnable, long delayTicks, long periodTicks) {
        PackagedTask[] task = new PackagedTask[1];
        task[0] = runTaskTimer(() -> {
            runnable.run();
            if (runnable.isCancelled()) {
                task[0].setCancelled(true);
            }
        }, delayTicks, periodTicks);
    }

    public static void runTaskTimerAsync(BukkitRunnable runnable, long delayTicks, long periodTicks) {
        PackagedTask[] task = new PackagedTask[1];
        task[0] = runTaskTimerAsync(() -> {
            runnable.run();
            if (runnable.isCancelled()) {
                task[0].setCancelled(true);
            }
        }, delayTicks, periodTicks);
    }

    public void runTaskLater(BukkitRunnable runnable, long delayTicks) {
        PackagedTask[] task = new PackagedTask[1];
        task[0] = runTaskLater(() -> {
            runnable.run();
            if (runnable.isCancelled()) {
                task[0].setCancelled(true);
            }
        }, delayTicks);
    }

    public static class PackagedTask {
        private final Object task;

        public PackagedTask(Object task) {
            this.task = task;
        }

        public boolean isCancelled() {
            try {
                return MethodUtil.executeInstanceMethod(task, "isCancelled");
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        public void setCancelled(boolean cancel) {
            try {
                MethodUtil.executeInstanceMethod(task, "cancel");
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}
