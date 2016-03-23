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
import java.util.function.BiFunction;
import java.util.function.Function;

import net.yetamine.sova.AdaptationException;
import net.yetamine.sova.Mappable;

/**
 * A mutable symbol-based view of a {@link Map}.
 *
 * <p>
 * The interface is designed as mutable; however, changing the content might be
 * prohibited anyway in the similar way in which regular maps may prevent their
 * modifications. The protection may be even partial (e.g., applied on selected
 * entries).
 *
 * <p>
 * Almost each operation may throw {@link UnsupportedOperationException} if the
 * operation could not be peformed, because it is indeed unsupported, or it may
 * not performed for the particular symbol - such a restriction could be imposed
 * by an implementation.
 *
 * <p>
 * Almost each operation may throw {@link AdaptationException} if the value to
 * be inserted in the container fails to pass the adaptation test. It prevents
 * polluting the container with values that are not actually permitted.
 *
 * <p>
 * Implementations may tolerate {@code null} values, or completely forbid such
 * mappings, resulting in throwing exceptions when such a mapping shall be made
 * ({@link IllegalArgumentException} and {@link NullPointerException} are often
 * choices).
 */
public interface SymbolContext extends SymbolMapping {

    /**
     * Returns an unmodifiable view on this instance.
     *
     * @return an unmodifable view
     */
    SymbolMapping unmodifiable();

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
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return this instance
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified
     *             symbol
     */
    default <T> SymbolContext set(Mappable<?, T> symbol, T value) {
        put(symbol, value);
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
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return this instance
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified
     *             symbol
     */
    default <T> SymbolContext add(Mappable<?, T> symbol, T value) {
        putIfAbsent(symbol, value);
        return this;
    }

    /**
     * Puts the default to this instance if the mapping does not provide an
     * adaptable value and returns the adaptation of the value then.
     *
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol defining the mapping. It must not be {@code null}.
     *
     * @return the result of adaptation, or {@code null} if there is no default
     */
    <T> T let(Mappable<?, ? extends T> symbol);

    /**
     * Puts the default to this instance if the mapping is absent, otherwise
     * tries to use the present mapping to get the result.
     *
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol defining the mapping. It must not be {@code null}.
     *
     * @return the adaptation of the resulting value; an empty container is
     *         returned if the value could not be adapted, or no default is
     *         provided
     */
    <T> Optional<T> have(Mappable<?, ? extends T> symbol);

    /**
     * Removes all entries, making this instance empty.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if the operation is prohibited
     */
    SymbolContext clear();

    /**
     * Removes all entries, making this instance empty.
     *
     * @return this instance
     *
     * @throws UnsupportedOperationException
     *             if the operation is prohibited
     */
    default SymbolContext discard() {
        clear();
        return this;
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
     *
     * @throws UnsupportedOperationException
     *             if the operation is not supported for the specified symbol
     */
    SymbolContext discard(Mappable<?, ?> symbol);

    /**
     * Removes an entry for the specified symbol.
     *
     * @param <T>
     *            the type of the result
     * @param symbol
     *            the symbol identifying the value to be removed. It must not be
     *            {@code null}.
     *
     * @return the removed value, or {@code null} if no such value exists
     *
     * @throws UnsupportedOperationException
     *             if the operation is not supported for the specified symbol
     */
    <T> T remove(Mappable<?, T> symbol);

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
     * @return {@code true} if the value was removed, {@code false} if the value
     *         hadn't existed
     *
     * @throws UnsupportedOperationException
     *             if the operation is not supported for the specified symbol
     */
    boolean remove(Mappable<?, ?> symbol, Object value);

    /**
     * Associates the specified value with the specified symbol.
     *
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified
     *             symbol
     */
    <T> T put(Mappable<?, T> symbol, T value);

    /**
     * Associates the specified value with the specified symbol if the symbol is
     * not associated yet.
     *
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return the previously associated value, or {@code null} if no such value
     *         existed before
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if the value could not be associated with the specified
     *             symbol
     */
    <T> T putIfAbsent(Mappable<?, T> symbol, T value);

    /**
     * Replaces the entry for the specified symbol only if currently associated
     * with the specified value.
     *
     * @param <T>
     *            the type of the value
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
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if a value could not be associated with the specified symbol
     */
    <T> boolean replace(Mappable<?, T> symbol, Object oldValue, T newValue);

    /**
     * Replaces the entry for the specified symbol only if it is currently
     * associated with some value.
     *
     * @param <T>
     *            the type of the value
     * @param symbol
     *            the symbol with which the specified value is to be associated.
     *            It must not be {@code null}.
     * @param value
     *            the value to be associated with the specified symbol
     *
     * @return the replaced value, or {@code null} if no such value was
     *         associated with the symbol
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if a value could not be associated with the specified symbol
     */
    <T> T replace(Mappable<?, T> symbol, T value);

    /**
     * Associates the specified symbol, if the symbol is not already associated
     * with a value, with the given value; otherwise, replaces the associated
     * value with the results of the given remapping function, or removes if the
     * result is {@code null}.
     *
     * @param <T>
     *            the type of the value
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
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if a value could not be associated with the specified symbol
     */
    <T> T merge(Mappable<?, T> symbol, T value, BiFunction<? super T, ? super T, ? extends T> remappingFunction);

    /**
     * Attempts to compute a mapping for the specified symbol and its current
     * mapped value (or {@code null} if there is no current mapping).
     *
     * @param <K>
     *            the type of the mapping key
     * @param <T>
     *            the type of the value
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param remappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified symbol, or
     *         {@code null} if none
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if a value could not be associated with the specified symbol
     */
    <K, T> T compute(Mappable<K, T> symbol, BiFunction<? super K, ? super T, ? extends T> remappingFunction);

    /**
     * Attempts to compute a mapping for the specified symbol only if the symbol
     * is not associated yet.
     *
     * @param <K>
     *            the type of the mapping key
     * @param <T>
     *            the type of the value
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param mappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the current (existing or computed) value associated with the
     *         specified symbol, or {@code null} if the computed value was
     *         {@code null}
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if a value could not be associated with the specified symbol
     */
    <K, T> T computeIfAbsent(Mappable<K, T> symbol, Function<? super K, ? extends T> mappingFunction);

    /**
     * Attempts to compute a mapping for the specified symbol only if the symbol
     * is associated already to some value.
     *
     * @param <K>
     *            the type of the mapping key
     * @param <T>
     *            the type of the value
     * @param symbol
     *            symbol with which the specified value is to be associated. It
     *            must not be {@code null}.
     * @param remappingFunction
     *            the function to compute a value. It must not be {@code null}.
     *
     * @return the new value associated with the specified symbol, or
     *         {@code null} if none
     *
     * @throws AdaptationException
     *             if the value does not pass the symbol's adaptation
     * @throws UnsupportedOperationException
     *             if a value could not be associated with the specified symbol
     */
    <K, T> T computeIfPresent(Mappable<K, T> symbol, BiFunction<? super K, ? super T, ? extends T> remappingFunction);

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
    SymbolContext putAll(SymbolMapping source);
}
