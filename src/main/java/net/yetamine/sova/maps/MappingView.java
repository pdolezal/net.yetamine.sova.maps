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
import java.util.function.BiConsumer;

import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Mappable;
import net.yetamine.sova.Mapping;

/**
 * An adaptive function-like mapping from {@link Mappable} references to values
 * that the references are associated with based on using a {@link Map} source.
 *
 * <p>
 * The interface is designed as read-only; however, changing the content might
 * be possible anyway: an implementation may offer yet a mutable interface for
 * the content, or an implementation may allow removing entries via the view.
 *
 * <p>
 * Note that this interface, as a functional-based view, provides no concept of
 * equality nor identity itself, despite it might be implied by the equality or
 * identity of the {@link #mappings()}.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
@FunctionalInterface
public interface MappingView<K, V> extends Mapping {

    /**
     * Returns the source {@link Map} instance.
     *
     * <p>
     * The source may contain entries that are invalid from the point of view of
     * this interface, which acts as an adapter for the source. For instance, it
     * may contain {@code null} values and then the result of {@code contains}
     * methods for this instance and its source may differ.
     *
     * @return the source {@link Map} instance
     */
    Map<K, V> mappings();

    /**
     * @see net.yetamine.sova.Mapping#contains(net.yetamine.sova.Mappable)
     */
    default boolean contains(Mappable<?, ?> ref) {
        return (ref.get(mappings()) != null);
    }

    /**
     * @see net.yetamine.sova.Mapping#get(net.yetamine.sova.Mappable)
     */
    default <R> R get(Mappable<?, R> ref) {
        return ref.get(mappings());
    }

    /**
     * @see net.yetamine.sova.Mapping#give(net.yetamine.sova.Mappable)
     */
    default <R> R give(Mappable<?, R> ref) {
        return ref.give(mappings());
    }

    /**
     * @see net.yetamine.sova.Mapping#find(net.yetamine.sova.Mappable)
     */
    default <R> Optional<R> find(Mappable<?, R> ref) {
        return ref.find(mappings());
    }

    /**
     * @see net.yetamine.sova.Mapping#yield(net.yetamine.sova.Mappable)
     */
    default <R> AdaptationResult<R> yield(Mappable<?, R> ref) {
        return ref.yield(mappings());
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
     */
    default void forEach(BiConsumer<? super K, ? super V> consumer) {
        mappings().forEach(consumer);
    }
}
