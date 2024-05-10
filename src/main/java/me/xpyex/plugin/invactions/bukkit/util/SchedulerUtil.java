package me.xpyex.plugin.invactions.bukkit.util;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.xplib.bukkit.util.reflect.MethodUtil;
import org.bukkit.Bukkit;

public class SchedulerUtil {
    private static Object foliaGlobalScheduler = null;
    private static Object foliaAsyncScheduler = null;

    static {
        try {
            foliaGlobalScheduler = MethodUtil.executeClassMethod(Bukkit.class, "getGlobalRegionScheduler");  //Bukkit.getGlobalRegionScheduler()拿到调度器实例
            foliaAsyncScheduler = MethodUtil.executeClassMethod(Bukkit.class, "getAsyncScheduler");  //Bukkit.getAsyncScheduler()拿到调度器实例
            InvActions.getInstance().getLogger().info("您正在使用Folia或其分支作为服务端核心，已尝试启用兼容.");
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static void runTaskTimer(Consumer<PackagedTask> consumer, long delayTicks, long periodTicks) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        try {
            if (delayTicks > 0) {
                if (periodTicks > 0) {
                    if (foliaGlobalScheduler == null) {
                        Bukkit.getScheduler().runTaskTimer(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(consumer)), delayTicks, periodTicks);
                    } else {
                        MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)), delayTicks, periodTicks);
                    }
                } else {
                    if (foliaGlobalScheduler == null) {
                        Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)), delayTicks);
                    } else {
                        MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "runDelayed", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)), delayTicks);
                    }
                }
            }
            if (periodTicks > 0) {
                if (foliaGlobalScheduler == null) {
                    Bukkit.getScheduler().runTaskTimer(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)), 1, periodTicks);
                } else {
                    MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)), 1, periodTicks);
                }
            }
            if (foliaGlobalScheduler == null) {
                Bukkit.getScheduler().runTask(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)));
                return;
            }
            MethodUtil.executeInstanceMethod(foliaGlobalScheduler, "run", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)));
        } catch (ReflectiveOperationException e) {
            InvActions.getInstance().getLogger().severe("无法调用Folia调度器");
            e.printStackTrace();
        }
    }

    public static void runTaskTimerAsync(Consumer<PackagedTask> consumer, long delayTicks, long periodTicks) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        try {
            if (delayTicks > 0) {
                if (periodTicks > 0) {
                    if (foliaAsyncScheduler == null) {
                        Bukkit.getScheduler().runTaskTimerAsynchronously(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)), delayTicks, periodTicks);
                    } else {
                        MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)), delayTicks * 50, periodTicks * 50, TimeUnit.MILLISECONDS);
                    }
                } else {
                    if (foliaAsyncScheduler == null) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)), delayTicks);
                    } else {
                        MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runDelayed", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)), delayTicks * 50, TimeUnit.MILLISECONDS);
                    }
                }
            }
            if (periodTicks > 0) {
                if (foliaAsyncScheduler == null) {
                    Bukkit.getScheduler().runTaskTimerAsynchronously(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)), 1, periodTicks);
                } else {
                    MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runAtFixedRate", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)), 50, periodTicks * 50, TimeUnit.MILLISECONDS);
                }
            }
            if (foliaAsyncScheduler == null) {
                Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), task -> consumer.accept(PackagedTask.of(task)));
                return;
            }
            MethodUtil.executeInstanceMethod(foliaAsyncScheduler, "runNow", InvActions.getInstance(), (Consumer<?>) task -> consumer.accept(PackagedTask.of(task)));
        } catch (ReflectiveOperationException e) {
            InvActions.getInstance().getLogger().severe("无法调用Folia调度器");
            e.printStackTrace();
        }
    }

    public static void runTask(Consumer<PackagedTask> consumer) {
        runTaskTimer(consumer, 0, 0);
        //
    }

    public static void runTaskLater(Consumer<PackagedTask> consumer, long delayTicks) {
        runTaskTimer(consumer, delayTicks, 0);
        //
    }

    public static void runTaskAsync(Consumer<PackagedTask> consumer) {
        runTaskTimerAsync(consumer, 0, 0);
        //
    }

    public static void runTaskLaterAsync(Consumer<PackagedTask> consumer, long delayTicks) {
        runTaskTimerAsync(consumer, delayTicks, 0);
        //
    }

    public static class PackagedTask {
        private final Object task;
        private static final WeakHashMap<Object, PackagedTask> TASK_CACHE = new WeakHashMap<>();

        public PackagedTask(Object task) {
            this.task = task;
            TASK_CACHE.put(task, this);
        }

        public static PackagedTask of(Object task) {
            if (TASK_CACHE.containsKey(task)) {
                return TASK_CACHE.get(task);
            }
            return new PackagedTask(task);
        }

        public boolean isCancelled() {
            try {
                return MethodUtil.executeInstanceMethod(task, "isCancelled");
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        public void cancel() {
            try {
                MethodUtil.executeInstanceMethod(task, "cancel");
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}
