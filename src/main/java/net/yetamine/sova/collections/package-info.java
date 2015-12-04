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

/**
 * Provides both interfaces and default implementations for symbol-based
 * map-like collections.
 *
 * <h1>Overview</h1>
 *
 * <p>
 * Although the {@link net.yetamine.sova.Symbol} hierarchy provides support for
 * using a {@link java.util.Map} as a native value source, the {@code Map}
 * interface is not designed as a heterogenous container and therefore not too
 * comfortable to use with symbols.
 *
 * <p>
 * This package provides interfaces for native symbol-based map-like collections
 * and a couple of adapters that turn any common {@code Map} instance into such
 * a collection.
 */
package net.yetamine.sova.collections;
