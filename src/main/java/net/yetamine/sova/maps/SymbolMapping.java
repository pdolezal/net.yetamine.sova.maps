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

import java.util.Map;
import java.util.Optional;

import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Mappable;

/**
 * An unmodifiable symbol-based view of a {@link Map}.
 *
 * <p>
 * <i>Symbol-based</i> means here that the structure, unlike an ordinary map,
 * uses surrogate objects, so called <i>symbols</i>, to stand for the actual
 * keys of the stored mappings. Symbols serve for interpreting the values as
 * well; the {@link Mappable} interface provides the base for symbols - the
 * {@link Mappable#remap()} method supplies the actual keys for the mapping
 * viewed via the {@link #map()} method.
 */
public interface SymbolMapping extends SymbolSource {

    /**
     * Returns a map-like view on the container. The view should be considered
     * unmodifiable, nevertheless an implementation may allow removing entries.
     *
     * @return a map-like view on the container
     */
    Map<?, ?> map();

    /**
     * Compares the specified object with this instance for equality and returns
     * {@code true} iff the object provides equal {@link #map()} view too.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    boolean equals(Object obj);

    /**
     * Returns the hash code of the {@link #map()} view.
     *
     * @see java.lang.Object#hashCode()
     */
    int hashCode();

    /**
     * Returns the size of the {@link #map()} view.
     *
     * @return the size of the {@link #map()} view
     */
    default int size() {
        return map().size();
    }

    /**
     * Returns {@code true} iff {@link #size()} is zero.
     *
     * @return {@code true} iff {@link #size()} is zero
     */
    default boolean isEmpty() {
        return map().isEmpty();
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#get(net.yetamine.sova.Mappable)
     */
    default <T> T get(Mappable<?, T> symbol) {
        return symbol.get(map());
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#use(net.yetamine.sova.Mappable)
     */
    default <T> T use(Mappable<?, T> symbol) {
        return symbol.use(map());
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#find(net.yetamine.sova.Mappable)
     */
    default <T> Optional<T> find(Mappable<?, T> symbol) {
        return symbol.find(map());
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#yield(net.yetamine.sova.Mappable)
     */
    default <T> AdaptationResult<T> yield(Mappable<?, T> symbol) {
        return symbol.yield(map());
    }

    /**
     * Returns an empty instance.
     *
     * @return an empty instance
     */
    static SymbolMapping empty() {
        return DefaultSymbolMapping.EMPTY;
    }
}
