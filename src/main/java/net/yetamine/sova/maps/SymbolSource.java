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

import java.util.Optional;

import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Mappable;

/**
 * An unmodifiable symbol-based value source.
 *
 * <p>
 * This structure does not support {@code null} values, although implementations
 * may tolerate them and allow storing mappings to {@code null} values, which is
 * sometimes useful for the interoperability with other collections.
 *
 * <p>
 * Using {@code null} for the symbol arguments is prohibited (consistently with
 * prohibiting {@code null} values). When suitable or necessary, it is possible
 * to use {@link Mappable#nullified()} as a surrogate for a {@code null} symbol.
 *
 * <p>
 * The interface is designed as read-only; however, changing the content might
 * be possible anyway: an implementation may offer yet a mutable interface for
 * the content, or an implementation may allow removing entries via the view.
 */
public interface SymbolSource {

    /**
     * Returns the value associated with the given symbol, or {@code null} if no
     * mapping for the symbol exists.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned. It must
     *            not be {@code null}.
     *
     * @return the value associated with the given symbol, or {@code null} if no
     *         mapping for the symbol exists
     */
    <T> T get(Mappable<?, T> symbol);

    /**
     * Returns the value associated with the given symbol, or the default value
     * for the symbol if no mapping for the symbol exists.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned. It must
     *            not be {@code null}.
     *
     * @return the value associated with the given symbol, or the default value
     *         for the symbol if no mapping for the symbol exists
     */
    <T> T use(Mappable<?, T> symbol);

    /**
     * Returns an {@link Optional} with the value associated with the given
     * symbol, or an empty container if no mapping for the symbol exists.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned. It must
     *            not be {@code null}.
     *
     * @return an {@link Optional} containing the value associated with the
     *         given symbol, or an empty container if no mapping for the symbol
     *         exists
     */
    <T> Optional<T> find(Mappable<?, T> symbol);

    /**
     * Returns {@code true} if a value associated with the given symbol exists.
     *
     * @param symbol
     *            the symbol to test. It must not be {@code null}.
     *
     * @return {@code true} if a value associated with the given symbol exists
     */
    default boolean contains(Mappable<?, ?> symbol) {
        return find(symbol).isPresent();
    }

    /**
     * Returns an {@link AdaptationResult} describing the attempt to adapt the
     * value associated to the given symbol with the symbol; the result allows
     * querying the value or the fallback as well as other details of the
     * operation.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol whose associated value is to be returned. It must
     *            not be {@code null}.
     *
     * @return an {@link AdaptationResult} describing the attempt to adapt the
     *         value associated to the given symbol with the symbol
     */
    <T> AdaptationResult<T> yield(Mappable<?, T> symbol);
}
