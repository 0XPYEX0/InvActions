package me.xpyex.lib.xplib.api;

import me.xpyex.lib.xplib.util.value.ValueUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Pair<K, V> {
    private final K key;
    private final V value;

    private Pair(K key, V value) {
        ValueUtil.notNull("Pair key is null", key);
        ValueUtil.notNull("Pair value is null", value);

        this.key = key;
        this.value = value;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
        //
    }

    @NotNull
    public K getKey() {
        return key;
        //
    }

    @NotNull
    public V getValue() {
        return value;
        //
    }

    //泛型拿到的Pair无法指定内部类型，可用此方法
    @NotNull
    public K getKey(@SuppressWarnings("unused") Class<K> returnType) {
        return key;
        //
    }

    //泛型拿到的Pair无法指定内部类型，可用此方法
    @NotNull
    public V getValue(@SuppressWarnings("unused") Class<V> returnType) {
        return value;
        //
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pair<?, ?>) {
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return this.getKey().equals(pair.getKey()) && this.getValue().equals(pair.getValue());
        }
        return false;
    }
}
