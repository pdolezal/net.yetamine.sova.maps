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

package net.yetamine.sova.collections;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A default implementation of the {@link SymbolMapping} interface that just
 * adapts an existing {@link Map} instance.
 *
 * <p>
 * The implementation does not care about the map being read-only or not and
 * exposes the map reference as {@link #map()} directly, therefore it may be
 * reasonable to provide an unmodifiable map instead in most cases.
 */
public final class DefaultSymbolMapping extends SymbolMappingAdapter implements Serializable {

    /** Serialization version: 1. */
    private static final long serialVersionUID = 1L;

    /** Common shared instance for an empty mapping. */
    static final SymbolMapping EMPTY = new DefaultSymbolMapping(Collections.emptyMap());

    /** Underlying map. */
    private final Map<?, ?> map;

    /**
     * Creates a new instance.
     *
     * @param m
     *            the map providing the underlying storage. It must not be
     *            {@code null}.
     */
    public DefaultSymbolMapping(Map<?, ?> m) {
        map = Objects.requireNonNull(m);
    }

    /**
     * @see net.yetamine.sova.collections.SymbolMapping#map()
     */
    public Map<?, ?> map() {
        return map;
    }
}
