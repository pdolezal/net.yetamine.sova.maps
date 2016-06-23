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

import net.yetamine.lang.functional.Producer;
import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Mappable;
import net.yetamine.sova.ObjectRecord;

/**
 * A convenient simplification for {@link MappingTable} with {@link Object} type
 * parameters (i.e., the most general container possible).
 */
public interface ObjectTable extends ObjectRecord, MappingTable<Object, Object> {

    /**
     * Adapts the given {@link Map} instance.
     *
     * @param storage
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectTable adapt(Map<Object, Object> storage) {
        return new ObjectTableAdapter(storage);
    }

    /**
     * Returns an empty unmodifiable instance.
     *
     * @return an empty unmodifiable instance
     */
    static ObjectTable empty() {
        return EmptyObjectTable.getInstance();
    }

    /**
     * Adapts a new {@link HashMap} instance.
     *
     * @return the new adapter instance
     */
    static ObjectTable create() {
        return adapt(new HashMap<>());
    }

    /**
     * Adapts a new {@link HashMap} instance which copies the content of the
     * given source.
     *
     * @param source
     *            the source to mirror. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectTable mirror(Map<?, ?> source) {
        return adapt(new HashMap<>(source));
    }

    /**
     * Adapts the given {@link Map} instance as an unmodifiable instance.
     *
     * @param source
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectTable unmodifiable(Map<?, ?> source) {
        final Map<Object, Object> unmodifiable = Collections.unmodifiableMap(source);

        if (unmodifiable.getClass() == source.getClass()) {
            @SuppressWarnings("unchecked") // It is unmodifiable already
            final Map<Object, Object> safe = (Map<Object, Object>) source;
            return adapt(safe);
        }

        return adapt(unmodifiable);
    }

    /**
     * Adapts the given source as an unmodifiable instance.
     *
     * @param source
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectTable unmodifiable(MappingView<?, ?> source) {
        return unmodifiable(source.mappings());
    }

    // Overriding this-returning methods

    /**
     * @see net.yetamine.sova.maps.MappingTable#add(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R> ObjectTable add(Mappable<? extends Object, R> ref, R value) {
        MappingTable.super.add(ref, value);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#addAll(java.util.Map)
     */
    default ObjectTable addAll(Map<? extends Object, ? extends Object> source) {
        MappingTable.super.addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#addAll(net.yetamine.sova.maps.MappingView)
     */
    default ObjectTable addAll(MappingView<? extends Object, ? extends Object> source) {
        MappingTable.super.addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#clear()
     */
    default ObjectTable clear() {
        MappingTable.super.clear();
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#discard(net.yetamine.sova.Mappable)
     */
    default ObjectTable discard(Mappable<?, ?> ref) {
        MappingTable.super.discard(ref);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#patch(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R> ObjectTable patch(Mappable<? extends Object, R> ref, R value) {
        MappingTable.super.patch(ref, value);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#patch(net.yetamine.sova.Mappable,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    default <R> ObjectTable patch(Mappable<? extends Object, R> ref, R value, BiFunction<? super R, ? super R, ? extends R> remapping) {
        MappingTable.super.patch(ref, value, remapping);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#putAll(java.util.Map)
     */
    default ObjectTable putAll(Map<? extends Object, ? extends Object> source) {
        MappingTable.super.putAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#putAll(net.yetamine.sova.maps.MappingView)
     */
    default ObjectTable putAll(MappingView<? extends Object, ? extends Object> source) {
        MappingTable.super.putAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#set(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <R> ObjectTable set(Mappable<? extends Object, R> ref, R value) {
        MappingTable.super.set(ref, value);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#self()
     */
    default Producer<? extends ObjectTable> self() {
        return () -> this;
    }
}

/**
 * A default implementation of the {@link MappingTable} interface that just
 * adapts an existing {@link Map} instance.
 */
final class ObjectTableAdapter extends AbstractMappingTable<Object, Object> implements Serializable, ObjectTable {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Underlying {@link Map} instance. */
    private final Map<Object, Object> mappings;
    /** Cached view instance (allocated on demand). */
    private transient MappingView<Object, Object> view;

    /**
     * Creates a new instance.
     *
     * @param map
     *            the map providing the underlying storage. It must not be
     *            {@code null}.
     */
    public ObjectTableAdapter(Map<Object, Object> map) {
        mappings = Objects.requireNonNull(map);
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#mappings()
     */
    public Map<Object, Object> mappings() {
        return mappings;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#view()
     */
    public MappingView<Object, Object> view() {
        MappingView<Object, Object> result = view;

        if (result == null) {
            final Map<Object, Object> unmodifiable = Collections.unmodifiableMap(mappings);
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

/**
 * The implementation of an empty instance.
 */
final class EmptyObjectTable implements Serializable, ObjectTable {

    /** Sole instance of this class. */
    private static final EmptyObjectTable INSTANCE = new EmptyObjectTable();

    /**
     * Creates a new instance.
     */
    private EmptyObjectTable() {
        // Default constructor
    }

    /**
     * Returns an instance.
     *
     * @return an instance
     */
    public static EmptyObjectTable getInstance() {
        return INSTANCE;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        return ((obj instanceof MappingTable<?, ?>) && mappings().equals(((MappingTable<?, ?>) obj).mappings()));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return mappings().hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mappings().toString();
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#mappings()
     */
    public Map<Object, Object> mappings() {
        return Collections.emptyMap();
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#view()
     */
    public MappingView<Object, Object> view() {
        return this::mappings;
    }

    /**
     * @see net.yetamine.sova.Mapping#contains(net.yetamine.sova.Mappable)
     */
    public boolean contains(Mappable<?, ?> symbol) {
        return false;
    }

    /**
     * @see net.yetamine.sova.Mapping#get(net.yetamine.sova.Mappable)
     */
    public <R> R get(Mappable<?, R> symbol) {
        return null;
    }

    /**
     * @see net.yetamine.sova.Mapping#give(net.yetamine.sova.Mappable)
     */
    public <R> R give(Mappable<?, R> symbol) {
        return symbol.fallback().get();
    }

    /**
     * @see net.yetamine.sova.Mapping#find(net.yetamine.sova.Mappable)
     */
    public <R> Optional<R> find(Mappable<?, R> symbol) {
        return Optional.empty();
    }

    /**
     * @see net.yetamine.sova.Mapping#yield(net.yetamine.sova.Mappable)
     */
    public <R> AdaptationResult<R> yield(Mappable<?, R> symbol) {
        return AdaptationResult.of(null, null, symbol);
    }

    // Serialization support

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /**
     * Returns the common instance instead of the deserialized instance.
     *
     * @return the common instance
     */
    private Object readResolve() {
        return getInstance();
    }
}
