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
import java.util.function.BiFunction;

import net.yetamine.lang.containers.Box;
import net.yetamine.lang.functional.Producer;
import net.yetamine.sova.Adaptation;
import net.yetamine.sova.Mappable;
import net.yetamine.sova.MappingStore;

/**
 * An extension of {@link MappingStore} serving as a map-like container, or as
 * an adapter that provides type-safe access to heterogeneous values contained
 * in an underlying {@link Map} instance.
 *
 * <h1>Remarks</h1>
 *
 * This interface extends {@link MappingStore} with enumeration capabilities and
 * bulk operations. Unlike the storage-agnostic parent interface, it defines the
 * an association to a {@link Map} instance, which provides a bridge to standard
 * collections.
 *
 * <p>
 * The link to a map instance enables better interoperability, but brings some
 * implications as well: the map may contain entries that might not be adapted
 * with the given references, it may contain perhaps even {@code null} values,
 * which are unsupported (as {@code null} being used for non-adaptable values),
 * it may be changed directly without appropriate checks, resulting in poluting
 * the view with non-adaptable values etc. Restricting the access to the map is
 * difficult as well.
 *
 * <h1>Implementation</h1>
 *
 * Most methods have a default implementation that uses {@link #mappings()} as
 * the source and target of the operations. Implementations that does not allow
 * full access to {@link #mappings()} should apply the protection rather on the
 * returned instance than trying to reimplement the methods in order to provide
 * a more robust control. If some methods or references need a privileged access
 * to the underlying storage, it should be then handled as an internal matter of
 * the implementation, out of the scope of this interface.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
public interface MappingTable<K, V> extends MappingView<K, V>, MappingStore<K, V> {

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
     * Returns an empty unmodifiable instance.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     *
     * @return an empty unmodifiable instance
     */
    static <K, V> MappingTable<K, V> empty() {
        @SuppressWarnings("unchecked") // Empty is unmodifiable, any type is good
        final MappingTable<K, V> result = (MappingTable<K, V>) ObjectTable.empty();
        return result;
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
     * Adapts a new {@link HashMap} instance which copies the content of the
     * given source.
     *
     * @param <K>
     *            the type of the keys
     * @param <V>
     *            the type of the values
     * @param source
     *            the source to mirror. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static <K, V> MappingTable<K, V> mirror(Map<? extends K, ? extends V> source) {
        return adapt(new HashMap<>(source));
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
        final Map<K, V> unmodifiable = Collections.unmodifiableMap(source);

        if (unmodifiable.getClass() == source.getClass()) {
            @SuppressWarnings("unchecked") // It is unmodifiable already
            final Map<K, V> safe = (Map<K, V>) source;
            return adapt(safe);
        }

        return adapt(unmodifiable);
    }

    /**
     * Adapts the given source as an unmodifiable instance.
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

    // Object methods

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

    // Mapping core

    /**
     * Returns the source map-like view on this container.
     *
     * <p>
     * The result may or may not be modifiable, depending on the implementation;
     * when modifiable, the result provides direct access to the container data.
     *
     * @return the map-like view on this container
     *
     * @see net.yetamine.sova.maps.MappingView#mappings()
     */
    Map<K, V> mappings();

    // Interoperability support

    /**
     * Returns a {@link Producer} providing {@link #mappings()}, which can be
     * used for subsequent monadic processing (like filtering, mapping and
     * consuming it).
     *
     * @return an {@link Producer} providing {@link #mappings()}
     */
    default Producer<? extends Map<K, V>> that() {
        return this::mappings;
    }

    /**
     * Returns a {@link Producer} providing this instance, which can be used for
     * subsequent monadic processing (like filtering, mapping and consuming it).
     *
     * @return an {@link Producer} providing this instance
     */
    default Producer<? extends MappingTable<K, V>> self() {
        return () -> this;
    }

    /**
     * Returns a read-only view on {@link #mappings()}.
     *
     * <p>
     * Overrides of this method should not change the return type and retain the
     * read-only interface, which declares explicitly the purpose of the result:
     * it is a read-only view on the data of this instance.
     *
     * @return a read-only view on {@link #mappings()}
     */
    default MappingView<K, V> view() {
        return () -> Collections.unmodifiableMap(mappings());
    }

    // Bulk operations: native interface

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
     * Puts all mappings from the provided source; existing associations for the
     * references provided by the source are replaced.
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

    // Bulk operations: Map interoperability

    /**
     * Puts all mappings from the provided source; existing associations for the
     * references provided by the source are replaced.
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
    default MappingTable<K, V> addAll(Map<? extends K, ? extends V> source) {
        source.forEach(mappings()::putIfAbsent);
        return this;
    }

    // Insertion methods

    /**
     * @see net.yetamine.sova.MappingStore#put(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R extends V> R put(Mappable<? extends K, R> ref, R value) {
        return ref.nullable(mappings().put(ref.remap(), ref.adapt(value).require()));
    }

    /**
     * @see net.yetamine.sova.MappingStore#putIfAbsent(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R extends V> R putIfAbsent(Mappable<? extends K, R> ref, R value) {
        final R item = ref.adapt(value).require();

        final Box<R> result = Box.empty();
        mappings().merge(ref.remap(), item, (o, v) -> {
            assert (v == item); // It should be the same object (after adapting)!

            final R current = ref.nullable(o);
            if (current == null) {
                return item;
            }

            result.accept(current);
            return current;
        });

        return result.get();
    }

    /**
     * @see net.yetamine.sova.MappingStore#have(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R extends V> Optional<R> have(Mappable<? extends K, R> ref, Object value) {
        final Optional<R> result = ref.optional(value);
        result.ifPresent(v -> mappings().put(ref.remap(), v));
        return result;
    }

    // Replacement methods

    /**
     * @see net.yetamine.sova.MappingStore#replace(net.yetamine.sova.Mappable,
     *      java.lang.Object, java.lang.Object)
     */
    default <R extends V> boolean replace(Mappable<? extends K, R> ref, V oldValue, R newValue) {
        return mappings().replace(ref.remap(), oldValue, ref.adapt(newValue).require());
    }

    /**
     * @see net.yetamine.sova.MappingStore#replace(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R extends V> V replace(Mappable<? extends K, R> ref, R value) {
        final Box<V> item = Box.of(ref.adapt(value).require());
        mappings().compute(ref.remap(), (k, v) -> item.put(v));
        return ref.nullable(item.get());
    }

    // Removal methods

    /**
     * @see net.yetamine.sova.MappingStore#remove(net.yetamine.sova.Mappable)
     */
    default <R extends V> R remove(Mappable<?, R> ref) {
        return ref.nullable(mappings().remove(ref.remap()));
    }

    /**
     * @see net.yetamine.sova.MappingStore#remove(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default boolean remove(Mappable<?, ?> ref, Object value) {
        return mappings().remove(ref.remap(), value);
    }

    // Composite operations

    /**
     * @see net.yetamine.sova.MappingStore#merge(net.yetamine.sova.Mappable,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    default <R extends V> R merge(Mappable<? extends K, R> ref, R value, BiFunction<? super R, ? super R, ? extends R> remapping) {
        final R item = ref.adapt(value).request();

        final V computed = mappings().compute(ref.remap(), (k, v) -> {
            final R current = ref.nullable(v);
            if (current == null) {
                return item;
            }

            // If present, invoke the remapping function to do the actual merge
            final R replace = remapping.apply(current, item);
            return ref.adapt(replace).request();
        });

        // The result underwent the adaptation
        @SuppressWarnings("unchecked")
        final R result = (R) computed;
        return result;
    }

    /**
     * @see net.yetamine.sova.MappingStore#compute(net.yetamine.sova.Mappable,
     *      java.util.function.BiFunction)
     */
    default <U extends K, R extends V> R compute(Mappable<U, R> ref, BiFunction<? super U, ? super R, ? extends R> remapping) {
        final Adaptation<R> adaptation = ref.adaptation();
        final U key = ref.remap();

        final Object computed = mappings().compute(key, (k, v) -> {
            final R current = adaptation.apply(v);
            final R replace = remapping.apply(key, current);
            // Omit the adaptation if the current (which has been verified) was returned
            return (replace == current) ? current : ref.adapt(replace).request();
        });

        // The result underwent the adaptation
        @SuppressWarnings("unchecked")
        final R result = (R) computed;
        return result;
    }

    // Fluent method alternatives

    /**
     * Associates the specified value with the specified reference if this
     * instance contains no association for the specified reference yet.
     *
     * <p>
     * This method is equivalent to {@link #putIfAbsent(Mappable, Object)}, it
     * just returns this instance instead. This method is more convenient if
     * multiple values shall be associated easily and the possibly associated
     * previous values are not interesting. It may be more efficient.
     *
     * @param <R>
     *            the type of the mapping result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified reference. It
     *            must not be {@code null}.
     *
     * @return this instance
     */
    default <R extends V> MappingTable<K, V> add(Mappable<? extends K, R> ref, R value) {
        mappings().merge(ref.remap(), ref.adapt(value).require(), (o, v) -> {
            final R current = ref.nullable(o);
            return (current != null) ? current : v;
        });

        return this;
    }

    /**
     * Associates the specified value with the specified reference.
     *
     * <p>
     * This method is equivalent to {@link #put(Mappable, Object)}, it just
     * returns this instance instead. This method is more convenient if multiple
     * values shall be associated easily and the possibly associated previous
     * values are not interesting. It may be more efficient.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference with which the specified value is to be
     *            associated. It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified reference. It
     *            must not be {@code null}.
     *
     * @return this instance
     */
    default <R extends V> MappingTable<K, V> set(Mappable<? extends K, R> ref, R value) {
        mappings().put(ref.remap(), ref.adapt(value).require());
        return this;
    }

    /**
     * Associates the specified value with the specified reference.
     *
     * <p>
     * This method is equivalent to {@link #let(Mappable, Object)}, it just
     * returns this instance instead of the merge result. This method is more
     * convenient if multiple values shall be associated easily and the possibly
     * associated previous values are not interesting. It may be more efficient.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to associate the reference with
     *
     * @return this instance
     */
    default <R extends V> MappingTable<K, V> patch(Mappable<? extends K, R> ref, R value) {
        return (value == null) ? discard(ref) : set(ref, value);
    }

    /**
     * Associates the specified reference, if the reference is not already
     * associated with a value, with the given value; otherwise, replaces the
     * associated value with the results of the given remapping function, or
     * removes if the result is {@code null}.
     *
     * <p>
     * This method is equivalent to {@link #merge(Mappable, Object, BiFunction)}
     * and returns this instance instead of the merge result. This method may be
     * more convenient if multiple values shall be associated easily and the
     * possibly associated previous values are not interesting. It may be more
     * efficient.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            reference with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to associate the reference with
     * @param remapping
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return this instance
     */
    default <R extends V> MappingTable<K, V> patch(Mappable<? extends K, R> ref, R value, BiFunction<? super R, ? super R, ? extends R> remapping) {
        final R item = ref.adapt(value).request();

        mappings().compute(ref.remap(), (k, v) -> {
            final R current = ref.nullable(v);
            if (current == null) {
                return item;
            }

            // If present, invoke the remapping function to do the actual merge
            final R replace = remapping.apply(current, item);
            return ref.adapt(replace).request();
        });

        return this;
    }

    /**
     * Removes an entry for the specified reference.
     *
     * <p>
     * This method is equivalent to {@link #remove(Mappable)}, it just returns
     * this instance instead. This method is more convenient if multiple values
     * shall be removed and the possibly removed values are not interesting.
     *
     * @param ref
     *            the reference identifying the value to be removed. It must not
     *            be {@code null}.
     *
     * @return this instance
     */
    default MappingTable<K, V> discard(Mappable<?, ?> ref) {
        mappings().remove(ref.remap());
        return this;
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
    /** Cached view instance (allocated on demand). */
    private transient MappingView<K, V> view;

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
            final Map<K, V> unmodifiable = Collections.unmodifiableMap(mappings);
            // Avoid over-wrapping (hence, if already unmodifiable, the view can this instance too
            result = (unmodifiable.getClass() == mappings.getClass()) ? this : () -> unmodifiable;

            // Using a local variable intentionally to employ out-of-thin-air
            // thread safety; the previous value could be either null, or an
            // existing instance that referred to the same mappings, so it's
            // as good as this one and mixing the references makes no harm.
            view = result;
        }

        return result;
    }
}
