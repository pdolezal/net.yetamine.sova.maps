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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Mappable;

/**
 * A implementation of {@link SymbolSource} which joins one or more instances in
 * a single query point using the instances in the given sequence when searching
 * for a mapping.
 *
 * <p>
 * The implementation relies on the provided sequence of actual sources. The
 * sequence may be even dynamic, but the owner must ensure thread safety etc.
 */
public final class LayeringSymbolSource implements SymbolSource {

    /** Sources for searching. */
    private final Iterable<? extends SymbolSource> sources;

    /**
     * Creates a new instance.
     *
     * @param iterable
     *            the sources to search in. It must not be {@code null} and it
     *            must never return {@code null}, but it may return different
     *            values.
     */
    public LayeringSymbolSource(Iterable<? extends SymbolSource> iterable) {
        sources = Objects.requireNonNull(iterable);
    }

    /**
     * Creates a new instance from a fixed list.
     *
     * @param sources
     *            the sources to search in. It must not be {@code null} and it
     *            must not contain {@code null}.
     *
     * @return an instance
     */
    public static SymbolSource from(SymbolSource... sources) {
        final Collection<SymbolSource> iterable = Arrays.asList(sources);
        if (iterable.isEmpty()) { // No arguments at all are legal too
            return SymbolMapping.empty();
        }

        iterable.forEach(Objects::requireNonNull);
        return new LayeringSymbolSource(iterable);
    }

    /**
     * Creates a new instance from a fixed collection.
     *
     * @param sources
     *            the sources to search in. It must not be {@code null} and it
     *            must not contain {@code null}.
     *
     * @return an instance
     */
    public static SymbolSource from(Collection<? extends SymbolSource> sources) {
        final Collection<SymbolSource> iterable = new ArrayList<>(sources);
        if (iterable.isEmpty()) { // No arguments at all are legal too
            return SymbolMapping.empty();
        }

        iterable.forEach(Objects::requireNonNull);
        return new LayeringSymbolSource(iterable);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringJoiner result = new StringJoiner(", ", "[", "]");
        sources.forEach(s -> result.add(s.toString()));
        return result.toString();
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#get(net.yetamine.sova.Mappable)
     */
    public <T> T get(Mappable<?, T> symbol) {
        for (SymbolSource source : sources) {
            final T result = source.get(symbol);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#use(net.yetamine.sova.Mappable)
     */
    public <T> T use(Mappable<?, T> symbol) {
        return symbol.fallback(get(symbol));
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#find(net.yetamine.sova.Mappable)
     */
    public <T> Optional<T> find(Mappable<?, T> symbol) {
        return Optional.ofNullable(get(symbol));
    }

    /**
     * @see net.yetamine.sova.maps.SymbolSource#yield(net.yetamine.sova.Mappable)
     */
    public <T> AdaptationResult<T> yield(Mappable<?, T> symbol) {
        for (SymbolSource source : sources) {
            final AdaptationResult<T> result = source.yield(symbol);
            if (result.isPresent()) {
                return result;
            }
        }

        return SymbolMapping.empty().yield(symbol);
    }
}
