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

/**
 * A skeletal implementation of {@link MappingTable} which implements the
 * required {@link Object} methods, but leaves the default implementations of
 * the other methods, so that it is actually as complete as possible, leaving
 * just the {@link #mappings()} method for the inherited classes.
 *
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
public abstract class AbstractMappingTable<K, V> implements MappingTable<K, V> {

    /**
     * Prepares a new instance.
     */
    protected AbstractMappingTable() {
        // Default constructor
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        return ((obj instanceof MappingTable<?, ?>) && mappings().equals(((MappingTable<?, ?>) obj).mappings()));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return mappings().hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mappings().toString();
    }
}
