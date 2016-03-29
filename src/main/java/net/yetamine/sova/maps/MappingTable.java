/*
 * Copyright 2016 Yetamine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.yetamine.sova.maps;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.yetamine.sova.Adaptation;
import net.yetamine.sova.AdaptationException;
import net.yetamine.sova.Mappable;

/**
 * A map-like container of mappings defined using {@link Mappable} symbols.
 *
 * <h1>Overview</h1>
 *
 * <p>
 * The symbols provide both the keys for the values and adaptation strategies to
 * adapt the values to the desired form. The interface can be understood as an
 * adapter of the {@link Map} interface or as an independent container. Anyway,
 * it provides a {@code Map}-compatible view via {@link #mappings()}.
 *
 * <p>
 * The actual keys, which the {@link #mappings()} view exposes, are retrieved
 * using {@link Mappable#remap()}, the value adaptation then uses the symbol's
 * interface, but does not have to use the symbol implementation necessarily.
 *
 * <p>
 * The interface is designed as mutable; however, changing the content might be
 * prohibited anyway in the similar way in which regular maps may prevent their
 * modifications. The protection may be even partial (e.g., applied on selected
 * entries or on direct access via {@link #mappings()}).
 *
 * <h1>Exceptions</h1>
 *
 * <p>
 * Almost each operation may throw {@link UnsupportedOperationException} if the
 * operation could not be peformed, because it is indeed unsupported, or it may
 * not performed due to a restriction.
 *
 * <p>
 * Almost each operation may throw {@link AdaptationException} if the value to
 * be inserted in the container fails to pass the adaptation test. It prevents
 * polluting the container with values that are not actually permitted; however,
 * full access to the container's underlying storage, if possible, can polute it
 * anyway and such entries looks like invisible for clients of this interface,
 * except for the {@link #mappings()} view which may expose them. This allows
 * mixed content and using this interface just as an adapter though.
 *
 * <p>
 * Implementations may tolerate {@code null} values, or completely forbid such
 * mappings, resulting in throwing exceptions when such a mapping shall be made
 * ({@link IllegalArgumentException} and {@link NullPointerException} are often
 * choices).
 *
 * <h1>Implementation</h1>
 *
 * Most methods have a default implementation that uses {@link #mappings()} as
 * the source and target of the operations. Implementations that does not allow
 * full access to {@link #mappings()} have to override the methods and supply a
 * different implementation, usually very similar, but using a privileged point.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
public interface MappingTable<K, V> extends MappingView<K, V> {

    /**
     * Returns an empty unmodifiable instance.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     *
     * @return an empty unmodifiable instance
     */
    @SuppressWarnings("unchecked")
    static <K, V> MappingTable<K, V> empty() {
        return (MappingTable<K, V>) EmptyObjectMapping.getInstance();
    }

    /**
     * Adapts a new {@link HashMap} instance.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     *
     * @return the new adapter instance
     */
    static <K, V> MappingTable<K, V> create() {
        return adapt(new HashMap<>());
    }

    /**
     * Adapts the given {@link Map} instance.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     * @param storage
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static <K, V> MappingTable<K, V> adapt(Map<K, V> storage) {
        return new MappingTableAdapter<>(storage);
    }

    /**
     * Adapts the given source's {@link #mappings()} as an unmodifiable
     * {@link Map}.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     * @param source
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static <K, V> MappingTable<K, V> unmodifiable(MappingView<? extends K, ? extends V> source) {
        return unmodifiable(source.mappings());
    }

    /**
     * Adapts the given {@link Map} instance as an unmodifiable instance.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     * @param source
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static <K, V> MappingTable<K, V> unmodifiable(Map<? extends K, ? extends V> source) {
        return adapt(Collections.unmodifiableMap(source));
    }

    // General methods

    /**
     * Compares the specified object with this instance for equality and returns
     * {@code true} iff the object provides an equal {@link #mappings()} view.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    boolean equals(Object obj);

    /**
     * Returns the hash code of the {@link #mappings()} view.
     *
     * @see java.lang.Object#hashCode()
     */
    int hashCode();

    /**
     * Returns the string representation for debugging purposes. Implementations
     * should return {@code mappings().toString()}.
     *
     * @see java.lang.Object#toString()
     */
    String toString();

    // View and bulk operation support

    /**
     * Returns the source map-like view on this container.
     *
     * <p>
     * The provided instance may be modifiable, and thus allow direct access to
     * the container data; on the other hand, an implementation may disallow it
     * and provide mutability only via the mappable-aware interface.
     *
     * @return the map-like view on this container
     *
     * @see net.yetamine.sova.maps.MappingView#mappings()
     */
    Map<K, V> mappings();

    /**
     * Provides an unmodifiable view on {@link #mappings()}.
     *
     * <p>
     * The default implementation assumes that the implementation of
     * {@link #mappings()} returns the same instance always. If this assumption
     * is not valid for an implementation, this method needs overriding to cope
     * with the possibly changing results.
     *
     * @return an unmodifiable view on {@link #mappings()}
     */
    default MappingView<K, V> view() {
        final Map<K, V> view = Collections.unmodifiableMap(mappings());
        return () -> view;
    }

    /**
     * Removes all entries, making this instance empty.
     *
     * @return this instance
     */
    default MappingTable<K, V> clear() {
        mappings().clear();
        return this;
    }

    /**
     * Invokes the given consumer on all mappings.
     *
     * <p>
     * This method behaves like {@link Map#forEach(BiConsumer)} and it is
     * actually a shortcut for invoking that on {@link #mappings()}.
     *
     * @param consumer
     *            the consumer to use. It must not be {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> forEach(BiConsumer<? super K, ? super V> consumer) {
        mappings().forEach(consumer);
        return this;
    }

    /**
     * Replaces all mappings with the results of the given function.
     *
     * <p>
     * This method behaves like {@link Map#replaceAll(BiFunction)} and it is
     * actually a shortcut for invoking that on {@link #mappings()}.
     *
     * @param function
     *            the function to process mappings. It must not be {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        mappings().replaceAll(function);
        return this;
    }

    /**
     * Puts all mappings from the provided source; existing associations for the
     * symbols provided by the source are replaced.
     *
     * @param source
     *            the source of mappings to put in this context. It must not be
     *            {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> putAll(MappingView<? extends K, ? extends V> source) {
        return putAll(source.mappings());
    }

    /**
     * Puts all mappings from the provided source; existing associations for the
     * symbols provided by the source are replaced.
     *
     * @param source
     *            the source of mappings to put in this context. It must not be
     *            {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> putAll(Map<? extends K, ? extends V> source) {
        mappings().putAll(source);
        return this;
    }

    /**
     * Copies all associations from a source to this instance, but retains the
     * existing ones.
     *
     * @param source
     *            the source of the associations to copy. It must not be
     *            {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> addAll(MappingView<? extends K, ? extends V> source) {
        return addAll(source.mappings());
    }

    /**
     * Copies all associations from a source to this instance, but retains the
     * existing ones.
     *
     * @param source
     *            the source of the associations to copy. It must not be
     *            {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> addAll(Map<? extends K, ? extends V> source) {
        source.forEach(mappings()::putIfAbsent);
        return this;
    }

    // Individual mutating operations

    /**
     * Associates the specified value with the specified symbol.
     *
     * <p>
     * This method is equivalent to {@link #put(Mappable, Object)}, it just
     * returns this instance instead of the previously associated value. This
     * method is more convenient when the previously associtated value is not
     * interesting and multiple values shall be associated easily. It may be
     * more efficient.
     *
     * @param <R>
     *            the type of the mapping result
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return this instance
     */
    default <R extends V> MappingTable<K, V> set(Mappable<? extends K, R> symbol, R value) {
        // Require the value to be valid for the symbol
        final R item = symbol.adapt(value).request();
        mappings().put(symbol.remap(), item);
        return this;
    }

    /**
     * Associates the specified value with the specified symbol if this instance
     * contains no association for the specified symbol yet.
     *
     * <p>
     * This method is equivalent to {@link #putIfAbsent(Mappable, Object)}, it
     * just returns this instance instead. This method is more convenient if
     * multiple values shall be associated easily and the possibly associated
     * previous values are not interesting. It may be more efficient.
     *
     * @param <R>
     *            the type of the mapping result
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return this instance
     */
    default <R extends V> MappingTable<K, V> add(Mappable<? extends K, R> symbol, R value) {
        putIfAbsent(symbol, value);
        return this;
    }

    /**
     * Puts the default to this instance if the mapping does not provide an
     * adaptable value and returns the adaptation of the value then.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            the symbol defining the mapping. It must not be {@code null}.
     *
     * @return the result of adaptation, or {@code null} if there is no default
     */
    default <R extends V> R let(Mappable<? extends K, ? extends R> symbol) {
        return symbol.let(mappings());
    }

    /**
     * Puts the default to this instance if the mapping is absent, otherwise
     * tries to use the present mapping to get the result.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            the symbol defining the mapping. It must not be {@code null}.
     *
     * @return the adaptation of the resulting value; an empty container is
     *         returned if the value could not be adapted, or no default is
     *         provided
     */
    default <R extends V> Optional<R> have(Mappable<? extends K, R> symbol) {
        return symbol.have(mappings());
    }

    /**
     * Removes an entry for the specified symbol.
     *
     * <p>
     * This method is equivalent to {@link #remove(Mappable)}, it just returns
     * this instance instead. This method is more convenient if multiple values
     * shall be removed and the possibly removed values are not interesting.
     *
     * @param symbol
     *            the symbol identifying the value to be removed. It must not be
     *            {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> discard(Mappable<?, ?> symbol) {
        mappings().remove(symbol.remap());
        return this;
    }

    /**
     * Removes an entry for the specified symbol.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            the symbol identifying the value to be removed. It must not be
     *            {@code null}.
     *
     * @return the removed value, or {@code null} if no such value exists or the
     *         existing value could not be adapted to the desired form
     */
    default <R extends V> R remove(Mappable<?, R> symbol) {
        return symbol.derive(mappings().remove(symbol.remap()));
    }

    /**
     * Removes an entry for the specified symbol if the symbol maps to the given
     * value.
     *
     * @param symbol
     *            the symbol identifying the value to be removed. It must not be
     *            {@code null}.
     * @param value
     *            the value to be removed
     *
     * @return {@code true} if the value was removed (no matter if has been
     *         adaptable or not), {@code false} if the value hadn't existed
     */
    default boolean remove(Mappable<?, ?> symbol, Object value) {
        return mappings().remove(symbol.remap(), value);
    }

    /**
     * Associates the specified value with the specified symbol.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before or the value could not be adapted to the desired
     *         form
     */
    default <R extends V> R put(Mappable<? extends K, R> symbol, R value) {
        final R item = symbol.adapt(value).request();
        return symbol.derive(mappings().put(symbol.remap(), item));
    }

    /**
     * Associates the specified value with the specified symbol if the symbol is
     * not associated yet.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before
     */
    default <R extends V> R putIfAbsent(Mappable<? extends K, R> symbol, R value) {
        final R item = symbol.adapt(value).request();

        @SuppressWarnings("unchecked")
        final R result = (R) mappings().merge(symbol.remap(), item, (u, v) -> {
            return symbol.resolve(v).orElse(item);
        });

        // Verify that the result is either null, or is equal to what the adaptation would return
        assert (result == null) || result.equals(symbol.derive(result));
        return result;
    }

    /**
     * Replaces the entry for the specified symbol only if currently associated
     * with the specified value.
     *
     * @param <R>
     *            the type of the mapping result
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param oldValue
     *            the expected currently associated value
     * @param newValue
     *            the value to be associated with the specified symbol
     *
     * @return {@code true} if the value was replaced, {@code false} if the
     *         expected value didn't match the actual current value
     */
    default <R extends V> boolean replace(Mappable<? extends K, R> symbol, V oldValue, R newValue) {
        return mappings().replace(symbol.remap(), oldValue, symbol.adapt(newValue).request());
    }

    /**
     * Replaces the entry for the specified symbol only if it is currently
     * associated with some value.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return the replaced value, or {@code null} if no such value was
     *         associated with the symbol or the result could not be adapted
     */
    default <R extends V> R replace(Mappable<? extends K, R> symbol, R value) {
        return symbol.derive(mappings().replace(symbol.remap(), symbol.adapt(value).request()));
    }

    /**
     * Associates the specified symbol, if the symbol is not already associated
     * with a value, with the given value; otherwise, replaces the associated
     * value with the results of the given remapping function, or removes if the
     * result is {@code null}.
     *
     * @param <R>
     *            the type of the result
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param value
     *            the value to associate the symbol with
     * @param remappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified symbol, or
     *         {@code null} if no value is associated with the symbol
     */
    default <R extends V> R merge(Mappable<? extends K, R> symbol, R value, BiFunction<? super R, ? super R, ? extends R> remappingFunction) {
        final R item = symbol.adapt(value).request();

        final V result = mappings().merge(symbol.remap(), item, (u, v) -> {
            final R current = symbol.derive(v);
            final R replace = remappingFunction.apply(item, current);
            return symbol.adapt(replace).request();
        });

        return symbol.derive(result);
    }

    /**
     * Attempts to compute a mapping for the specified symbol and its current
     * mapped value (or {@code null} if there is no current mapping).
     *
     * @param <U>
     *            the type of the mapping key
     * @param <R>
     *            the type of the result
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param remappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified symbol, or
     *         {@code null} if none
     */
    default <U extends K, R extends V> R compute(Mappable<U, R> symbol, BiFunction<? super U, ? super R, ? extends R> remappingFunction) {
        final Adaptation<R> adaptation = symbol.adaptation();
        final U key = symbol.remap();

        final Object result = mappings().compute(key, (k, v) -> {
            final R current = adaptation.apply(v);
            final R replace = remappingFunction.apply(key, current);
            return symbol.adapt(replace).request();
        });

        return adaptation.apply(result);
    }

    /**
     * Attempts to compute a mapping for the specified symbol only if the symbol
     * is not associated yet.
     *
     * @param <U>
     *            the type of the mapping key
     * @param <R>
     *            the type of the result
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param mappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the current (existing or computed) value associated with the
     *         specified symbol, or {@code null} if none
     */
    default <U extends K, R extends V> R computeIfAbsent(Mappable<U, R> symbol, Function<? super U, ? extends R> mappingFunction) {
        return compute(symbol, (t, v) -> (v == null) ? symbol.adapt(mappingFunction.apply(t)).request() : v);
    }

    /**
     * Attempts to compute a mapping for the specified symbol only if the symbol
     * is associated already to some value.
     *
     * @param <U>
     *            the type of the mapping key
     * @param <R>
     *            the type of the result
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param remappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified symbol, or
     *         {@code null} if none
     */
    default <U extends K, R extends V> R computeIfPresent(Mappable<U, R> symbol, BiFunction<? super U, ? super R, ? extends R> remappingFunction) {
        return compute(symbol, (t, v) -> (v != null) ? symbol.adapt(remappingFunction.apply(t, v)).request() : null);
    }

    /**
     * Attempts to compute a mapping for the specified symbol only if the symbol
     * is not associated yet.
     *
     * @param <R>
     *            the type of the result
     * @param <S>
     *            the type of the supplied value
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param valueSupplier
     *            the supplier of the value. It must not be {@code null}.
     *
     * @return the computed value associated with the specified symbol
     */
    default <R extends V, S extends R> R supplyIfAbsent(Mappable<? extends K, R> symbol, Supplier<S> valueSupplier) {
        final V result = mappings().compute(symbol.remap(), (k, v) -> symbol.adapt(valueSupplier.get()).request());
        return symbol.derive(result);
    }

    /**
     * Attempts to compute a mapping for the specified symbol only if the symbol
     * is associated already to some value.
     *
     * @param <R>
     *            the type of the result
     * @param <S>
     *            the type of the supplied value
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param valueSupplier
     *            the supplier of the value. It must not be {@code null}.
     *
     * @return the computed value associated with the specified symbol
     */
    default <R extends V, S extends R> R supplyIfPresent(Mappable<? extends K, R> symbol, Supplier<S> valueSupplier) {
        final V result = mappings().compute(symbol.remap(), (k, v) -> symbol.adapt(valueSupplier.get()).request());
        return symbol.derive(result);
    }
}

/**
 * A default implementation of the {@link MappingTable} interface that just
 * adapts an existing {@link Map} instance.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
final class MappingTableAdapter<K, V> extends AbstractMappingTable<K, V> implements Serializable {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Underlying {@link Map} instance. */
    private final Map<K, V> mappings;
    /** Cached view instance. */
    private MappingView<K, V> view;

    /**
     * Creates a new instance.
     *
     * @param map
     *            the map providing the underlying storage. It must not be
     *            {@code null}.
     */
    public MappingTableAdapter(Map<K, V> map) {
        mappings = Objects.requireNonNull(map);
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#mappings()
     */
    public Map<K, V> mappings() {
        return mappings;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#view()
     */
    public MappingView<K, V> view() {
        MappingView<K, V> result = view;

        if (result == null) {
            result = super.view();
            view = result;
        }

        return result;
    }
}
