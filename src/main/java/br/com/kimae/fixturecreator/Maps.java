package br.com.kimae.fixturecreator;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Classe de utilidade que torna possível a criação de Maps de modo fluente.
 * i.e.
 * Maps.<String, String>.builder().put("chave1", "blabla").build();
 *
 * A implementação default utilizada é a HashMap para Map e a ConcurrentHashMap para o ConcurrentMap.
 */
public class Maps {

    private Maps() {
    }

    /**
     * Cria um Map com a implementação HashMap.
     */
    public static <K, V> MapBuilder<K, V> builder() {
        return builder(HashMap::new);
    }

    public static <K, V> MapBuilder<K, V> builder(final Supplier<Map<K, V>> mapSupplier) {
        return new MapBuilder<>(mapSupplier.get());
    }

    public static <K, V> ConcurrentMapBuilder<K, V> concurrentBuilder() {
        return concurrentBuilder(ConcurrentHashMap::new);
    }

    public static <K, V> ConcurrentMapBuilder<K, V> concurrentBuilder(final Supplier<ConcurrentMap<K, V>> mapSupplier) {
        return new ConcurrentMapBuilder<>(mapSupplier.get());
    }

    private static class BaseBuilder<M extends Map<K, V>, K, V> {

        protected final M map;

        public BaseBuilder(final M map) {
            this.map = map;
        }

        public BaseBuilder<M, K, V> put(final K key, final V value) {
            map.put(key, value);
            return this;
        }

        public M build() {
            return map;
        }

    }

    public static class MapBuilder<K, V> extends BaseBuilder<Map<K, V>, K, V> {

        private boolean unmodifiable;

        public MapBuilder(final Map<K, V> map) {
            super(map);
        }

        @Override
        public MapBuilder<K, V> put(final K key, final V value) {
            super.put(key, value);
            return this;
        }

        public MapBuilder<K, V> unmodifiable(final boolean unmodifiable) {
            this.unmodifiable = unmodifiable;
            return this;
        }

        @Override
        public Map<K, V> build() {
            if (unmodifiable) {
                return Collections.unmodifiableMap(super.build());
            } else {
                return super.build();
            }
        }

    }

    public static class ConcurrentMapBuilder<K, V> extends BaseBuilder<ConcurrentMap<K, V>, K, V> {

        public ConcurrentMapBuilder(final ConcurrentMap<K, V> map) {
            super(map);
        }

        @Override
        public ConcurrentMapBuilder<K, V> put(final K key, final V value) {
            super.put(key, value);
            return this;
        }

    }
}
