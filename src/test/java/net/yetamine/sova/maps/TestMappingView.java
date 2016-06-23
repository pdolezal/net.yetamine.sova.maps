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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.sova.AdaptationProvider;
import net.yetamine.sova.AdaptationResult;
import net.yetamine.sova.Downcasting;
import net.yetamine.sova.Mappable;

/**
 * Tests {@link MappingView}.
 */
public final class TestMappingView {

    /** Value to be present under the key {@code "integer"}. */
    private static final Integer INTEGER_VALUE = Integer.valueOf(1024);
    /** Value to be present under the key {@code "string"}. */
    private static final String STRING_VALUE = "hello";

    /**
     * Testing {@link MappingView} returning both {@link #INTEGER_VALUE} and
     * {@link #STRING_VALUE}.
     */
    private static final MappingView<String, ?> MAPPING;
    static {
        final Map<String, Object> data = new HashMap<>();
        data.put("integer", INTEGER_VALUE);
        data.put("string", STRING_VALUE);
        final Map<String, Object> m = Collections.unmodifiableMap(data);
        MAPPING = () -> m;
    }

    /**
     * Tests {@link MappingView#contains(Mappable)}.
     */
    @Test
    public void testContains() {
        Assert.assertTrue(MAPPING.contains(Mappable.of("integer", Downcasting.to(Integer.class))));
        Assert.assertFalse(MAPPING.contains(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertFalse(MAPPING.contains(Mappable.of("missing", Downcasting.to(Object.class))));
    }

    /**
     * Tests {@link MappingView#get(Mappable)}.
     */
    @Test
    public void testGet() {
        Assert.assertEquals(MAPPING.get(Mappable.of("integer", Downcasting.to(Integer.class))), INTEGER_VALUE);
        Assert.assertNull(MAPPING.get(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertNull(MAPPING.get(Mappable.of("missing", Downcasting.to(Object.class))));
    }

    /**
     * Tests {@link MappingView#give(Mappable)}.
     */
    @Test
    public void testGive() {
        Assert.assertEquals(MAPPING.give(Mappable.of("integer", Downcasting.to(Integer.class))), INTEGER_VALUE);
        Assert.assertNull(MAPPING.give(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertNull(MAPPING.give(Mappable.of("missing", Downcasting.to(Object.class))));

        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> p = Downcasting.withFallbackTo(Integer.class, i);
        Assert.assertEquals(MAPPING.give(Mappable.of("integer", p)), INTEGER_VALUE);
        Assert.assertEquals(MAPPING.give(Mappable.of("string", p)), i);
        Assert.assertEquals(MAPPING.give(Mappable.of("missing", p)), i);
    }

    /**
     * Tests {@link MappingView#find(Mappable)}.
     */
    @Test
    public void testFind() {
        final AdaptationProvider<Integer> p = Downcasting.to(Integer.class);
        Assert.assertEquals(MAPPING.find(Mappable.of("integer", p)).get(), INTEGER_VALUE);
        Assert.assertFalse(MAPPING.find(Mappable.of("string", p)).isPresent());
        Assert.assertFalse(MAPPING.find(Mappable.of("missing", p)).isPresent());
    }

    /**
     * Tests {@link MappingView#yield(Mappable)}.
     */
    @Test
    public void testYield() {
        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> p = Downcasting.withFallbackTo(Integer.class, i);

        final AdaptationResult<Integer> r1 = MAPPING.yield(Mappable.of("integer", p));
        Assert.assertEquals(r1.argument(), INTEGER_VALUE);
        Assert.assertEquals(r1.get(), INTEGER_VALUE);
        Assert.assertEquals(r1.fallback().get(), i);

        final AdaptationResult<Integer> r2 = MAPPING.yield(Mappable.of("string", p));
        Assert.assertEquals(r2.argument(), STRING_VALUE);
        Assert.assertNull(r2.get());
        Assert.assertEquals(r2.fallback().get(), i);

        final AdaptationResult<Integer> r3 = MAPPING.yield(Mappable.of("missing", p));
        Assert.assertEquals(r3.argument(), null);
        Assert.assertNull(r3.get());
        Assert.assertEquals(r3.fallback().get(), i);
    }

    /**
     * Tests {@link MappingView#forEach(java.util.function.BiConsumer)}.
     */
    @Test
    public void testForEach() {
        final Map<Object, Object> m = new HashMap<>();
        MAPPING.forEach(m::put);
        Assert.assertEquals(m, MAPPING.mappings());
    }
}
