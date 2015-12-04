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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A default implementation of the {@link SymbolContext} interface that just
 * adapts an existing {@link Map} instance.
 */
public final class DefaultSymbolContext extends SymbolContextAdapter implements Serializable {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Underlying {@link Map} instance. */
    private final Map<Object, Object> storage;

    /**
     * Creates a new instance.
     *
     * @param map
     *            the map providing the underlying storage. It must not be
     *            {@code null}.
     */
    public DefaultSymbolContext(Map<Object, Object> map) {
        storage = Objects.requireNonNull(map);
    }

    /**
     * Creates a new instance backed by a new empty {@link Map} (currently, the
     * {@link HashMap} is used).
     */
    public DefaultSymbolContext() {
        this(new HashMap<>());
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContextAdapter#storage()
     */
    @Override
    protected Map<Object, Object> storage() {
        return storage;
    }
}
