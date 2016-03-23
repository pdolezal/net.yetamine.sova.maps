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
 * A function-like view of a {@link Map} instance that derives mapping from
 * {@link Mappable} symbols to values that the symbols are associated with and
 * can adapt them to the desired form.
 *
 * <p>
 * The interface is designed as read-only; however, changing the content might
 * be possible anyway: an implementation may offer yet a mutable interface for
 * the content, or an implementation may allow removing entries via the view.
 *
 * <p>
 * Note that this interface, as a functional-based view, provides no concept of
 * equality nor identity itself, despite it might be implied by the equality or
 * identity of the underlying {@link Map} instance.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
@FunctionalInterface
public interface MappingView<K, V> extends MappingSource {

    /**
     * Returns the source {@link Map} instance.
     *
     * <p>
     * The provided result should be considered unmodifiable, unless its
     * provider states otherwise.
     *
     * @return the source {@link Map} instance
     */
    Map<K, V> mappings();

    /**
     * @see net.yetamine.sova.maps.MappingSource#get(net.yetamine.sova.Mappable)
     */
    default <R> R get(Mappable<?, R> symbol) {
        return symbol.get(mappings());
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#use(net.yetamine.sova.Mappable)
     */
    default <R> R use(Mappable<?, R> symbol) {
        return symbol.use(mappings());
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#find(net.yetamine.sova.Mappable)
     */
    default <R> Optional<R> find(Mappable<?, R> symbol) {
        return symbol.find(mappings());
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#contains(net.yetamine.sova.Mappable)
     */
    default boolean contains(Mappable<?, ?> symbol) {
        return (symbol.get(mappings()) != null);
    }

    /**
     * @see net.yetamine.sova.maps.MappingSource#yield(net.yetamine.sova.Mappable)
     */
    default <R> AdaptationResult<R> yield(Mappable<?, R> symbol) {
        return symbol.yield(mappings());
    }
}
