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

import java.util.Map;

/**
 * A skeletal implementation of an adapter for {@link Map}.
 *
 * <p>
 * Inherited classes must override {@link #map()} to supply the underlying
 * {@link Map} instance which the implementation encapsulates and adapts; the
 * instance may tolerate {@code null} values as well as it may prohibit them.
 */
public abstract class SymbolMappingAdapter implements SymbolMapping {

    /**
     * Prepares a new instance.
     */
    protected SymbolMappingAdapter() {
        // Default constructor
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        return (obj == this) || ((obj instanceof SymbolMapping) && this.map().equals(((SymbolMapping) obj).map()));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return map().hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return map().toString();
    }
}
