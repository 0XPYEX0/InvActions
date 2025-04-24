package me.xpyex.lib.xplib.api;

/**
 * 允许抛出任何错误的Callable
 */
public interface AnyCallable<T> {
    T call() throws Throwable;
}
