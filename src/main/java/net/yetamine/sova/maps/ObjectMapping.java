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

import net.yetamine.lang.creational.Singleton;
import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Mappable;

/**
 * A convenient simplification for {@link MappingTable} with {@link Object} type
 * parameters (i.e., the most general container possible).
 */
public interface ObjectMapping extends MappingTable<Object, Object> {

    /**
     * Returns an empty unmodifiable instance.
     *
     * @return an empty unmodifiable instance
     */
    static ObjectMapping empty() {
        return EmptyObjectMapping.getInstance();
    }

    /**
     * Adapts a new {@link HashMap} instance.
     *
     * @return the new adapter instance
     */
    static ObjectMapping create() {
        return adapt(new HashMap<>());
    }

    /**
     * Adapts the given {@link Map} instance.
     *
     * @param storage
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectMapping adapt(Map<Object, Object> storage) {
        return new ObjectMappingAdapter(storage);
    }

    /**
     * Adapts the given source's {@link #mappings()} as an unmodifiable
     * {@link Map}.
     *
     * @param source
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectMapping unmodifiable(MappingView<?, ?> source) {
        return unmodifiable(source.mappings());
    }

    /**
     * Adapts the given {@link Map} instance as an unmodifiable instance.
     *
     * @param source
     *            the instance to adapt. It must not be {@code null}.
     *
     * @return the new adapter instance
     */
    static ObjectMapping unmodifiable(Map<?, ?> source) {
        return adapt(Collections.unmodifiableMap(source));
    }

    // Overriding this-returning methods

    /**
     * @see net.yetamine.sova.maps.MappingTable#clear()
     */
    default ObjectMapping clear() {
        MappingTable.super.clear();
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#add(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <T> ObjectMapping add(Mappable<? extends Object, T> symbol, T value) {
        MappingTable.super.add(symbol, value);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#addAll(java.util.Map)
     */
    default ObjectMapping addAll(Map<? extends Object, ? extends Object> source) {
        MappingTable.super.addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#addAll(net.yetamine.sova.maps.MappingView)
     */
    default ObjectMapping addAll(MappingView<? extends Object, ? extends Object> source) {
        MappingTable.super.addAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#discard(net.yetamine.sova.Mappable)
     */
    default ObjectMapping discard(Mappable<?, ?> symbol) {
        MappingTable.super.discard(symbol);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#forEach(java.util.function.BiConsumer)
     */
    default ObjectMapping forEach(BiConsumer<? super Object, ? super Object> consumer) {
        MappingTable.super.forEach(consumer);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#putAll(java.util.Map)
     */
    default ObjectMapping putAll(Map<? extends Object, ? extends Object> source) {
        MappingTable.super.putAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#putAll(net.yetamine.sova.maps.MappingView)
     */
    default ObjectMapping putAll(MappingView<? extends Object, ? extends Object> source) {
        MappingTable.super.putAll(source);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#replaceAll(java.util.function.BiFunction)
     */
    default ObjectMapping replaceAll(BiFunction<? super Object, ? super Object, ? extends Object> function) {
        MappingTable.super.replaceAll(function);
        return this;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#set(net.yetamine.sova.Mappable,
     *      java.lang.Object)
     */
    default <T> ObjectMapping set(Mappable<? extends Object, T> symbol, T value) {
        MappingTable.super.set(symbol, value);
        return this;
    }
}

/**
 * A default implementation of the {@link MappingTable} interface that just
 * adapts an existing {@link Map} instance.
 */
final class ObjectMappingAdapter extends AbstractMappingTable<Object, Object> implements Serializable, ObjectMapping {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Underlying {@link Map} instance. */
    private final Map<Object, Object> mappings;

    /**
     * Creates a new instance.
     *
     * @param map
     *            the map providing the underlying storage. It must not be
     *            {@code null}.
     */
    public ObjectMappingAdapter(Map<Object, Object> map) {
        mappings = Objects.requireNonNull(map);
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#mappings()
     */
    public Map<Object, Object> mappings() {
        return mappings;
    }
}

/**
 * The implementation of an empty instance.
 */
final class EmptyObjectMapping extends Singleton implements ObjectMapping {

    /** Sole instance of this class. */
    private static final EmptyObjectMapping INSTANCE = new EmptyObjectMapping();

    /**
     * Creates a new instance.
     */
    private EmptyObjectMapping() {
        // Default constructor
    }

    /**
     * Returns an instance.
     *
     * @return an instance
     */
    @Singleton.AccessPoint
    public static EmptyObjectMapping getInstance() {
        return INSTANCE;
    }

    /**
     * @see net.yetamine.sova.maps.MappingTable#mappings()
     */
    public Map<Object, Object> mappings() {
        return Collections.emptyMap();
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#contains(net.yetamine.sova.Mappable)
     */
    public boolean contains(Mappable<?, ?> symbol) {
        return false;
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#get(net.yetamine.sova.Mappable)
     */
    public <R> R get(Mappable<?, R> symbol) {
        return null;
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#use(net.yetamine.sova.Mappable)
     */
    public <R> R use(Mappable<?, R> symbol) {
        return symbol.fallback().get();
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#find(net.yetamine.sova.Mappable)
     */
    public <R> Optional<R> find(Mappable<?, R> symbol) {
        return Optional.empty();
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#yield(net.yetamine.sova.Mappable)
     */
    public <R> AdaptationResult<R> yield(Mappable<?, R> symbol) {
        return AdaptationResult.of(null, null, symbol);
    }
}
