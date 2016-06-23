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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.creational.Serialized;
import net.yetamine.sova.Downcasting;
import net.yetamine.sova.Mappable;

/**
 * Tests {@link MappingTable} and {@link ObjectTable}.
 */
public final class TestMappingTable {

    /** Key for integer values. */
    private static final Mappable<String, Integer> INTEGER = Mappable.of("integer", Downcasting.to(Integer.class));
    /** Key for integer values. */
    private static final Mappable<String, String> STRING = Mappable.of("string", Downcasting.to(String.class));

    /**
     * Tests core methods for creating and comparing the instances.
     */
    @Test
    public void testCore() {
        final Map<Object, Object> m = new HashMap<>();

        final MappingTable<Object, Object> t = MappingTable.adapt(m);
        Assert.assertSame(t.that().get(), m);
        Assert.assertSame(t.self().get(), t);

        Assert.assertSame(t.mappings(), m);
        Assert.assertEquals(t, MappingTable.empty());

        // Tunnel through the mappings
        m.put("string", "Hello");
        Assert.assertNotEquals(t, MappingTable.empty());
        Assert.assertEquals(MappingTable.mirror(m).mappings(), m);
        Assert.assertEquals(MappingTable.mirror(m), t);
        Assert.assertEquals(MappingTable.adapt(new LinkedHashMap<>(m)), t);

        Assert.assertEquals(t.hashCode(), m.hashCode());
    }

    /**
     * Tests {@link MappingTable#clear()}.
     */
    @Test
    public void testClear() {
        final Map<String, Object> m = new HashMap<>();
        m.put("string", "Hello");
        m.put("integer", 1);

        final MappingTable<String, Object> t = MappingTable.adapt(m);
        t.clear();
        Assert.assertTrue(m.isEmpty());
    }

    /**
     * Tests {@link MappingTable#putAll(MappingView)} and
     * {@link MappingTable#putAll(Map)}.
     */
    @Test
    public void testPutAll() {
        final Map<String, Object> m = new HashMap<>();
        m.put("string", "Hello");
        m.put("integer", 1);

        final MappingTable<String, Object> t = MappingTable.adapt(m);
        Assert.assertEquals(MappingTable.create().putAll(m), t);
        Assert.assertEquals(MappingTable.create().putAll(t), t);
    }

    /**
     * Tests {@link MappingTable#addAll(MappingView)} and
     * {@link MappingTable#addAll(Map)}.
     */
    @Test
    public void testAddAll() {
        // Starting point
        final Map<String, Object> s = new HashMap<>();
        s.put("string", "Hello");
        s.put("integer", 1);

        // Additional entries
        final Map<String, Object> a = new HashMap<>();
        a.put("new", new Object());
        a.put("integer", 2);

        // Desired state
        final Map<String, Object> d = new HashMap<>();
        d.putAll(s);
        d.put("new", a.get("new"));

        // Test addAll
        final MappingTable<String, Object> t = MappingTable.adapt(d);
        Assert.assertEquals(MappingTable.mirror(s).addAll(() -> a), t);
        Assert.assertEquals(MappingTable.mirror(s).addAll(a), t);
    }

    /**
     * Tests {@link MappingTable#put(Mappable, Object)}.
     */
    @Test
    public void testPut() {
        final MappingTable<String, Object> t = MappingTable.create();
        Assert.assertNull(t.put(STRING, "Hello"));
        Assert.assertEquals(t.get(STRING), "Hello");

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertNull(t.put(STRING, "Hello"));
        Assert.assertEquals(t.get(STRING), "Hello");

        Assert.assertEquals(t.put(STRING, "Another"), "Hello");
        Assert.assertEquals(t.get(STRING), "Another");
    }

    /**
     * Tests {@link MappingTable#putIfAbsent(Mappable, Object)}.
     */
    @Test
    public void testPutIfAbsent() {
        final MappingTable<String, Object> t = MappingTable.create();
        Assert.assertNull(t.putIfAbsent(STRING, "Hello"));
        Assert.assertEquals(t.get(STRING), "Hello");

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertNull(t.putIfAbsent(STRING, "Hello"));
        Assert.assertEquals(t.get(STRING), "Hello");

        Assert.assertEquals(t.putIfAbsent(STRING, "Another"), "Hello");
        Assert.assertEquals(t.get(STRING), "Hello");
    }

    /**
     * Tests {@link MappingTable#have(Mappable, Object)}.
     */
    @Test
    public void testHave() {
        final MappingTable<String, Object> t = MappingTable.create();

        // Valid case
        Assert.assertEquals(t.have(STRING, "Hello").get(), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");

        // Invalid insertion
        Assert.assertFalse(t.have(INTEGER, "Hello").isPresent());
        Assert.assertFalse(t.mappings().containsKey(INTEGER.remap()));

        // Replacement
        Assert.assertEquals(t.have(STRING, "Dolly").get(), "Dolly");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Dolly");

        // Overwrite non-adaptable mapping
        t.mappings().put(STRING.remap(), 1);
        Assert.assertEquals(t.have(STRING, "Hello").get(), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");
    }

    /**
     * Tests {@link MappingTable#replace(Mappable, Object)}.
     */
    @Test
    public void testReplace2() {
        final MappingTable<String, Object> t = MappingTable.create();

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertEquals(t.replace(STRING, "Dolly"), "Hello");
        Assert.assertEquals(t.get(STRING), "Dolly");

        Assert.assertEquals(t.replace(INTEGER, 1), null);
        Assert.assertEquals(t.get(INTEGER), Integer.valueOf(1));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertEquals(t.replace(INTEGER, 2), null);
        Assert.assertEquals(t.get(INTEGER), Integer.valueOf(2));
    }

    /**
     * Tests {@link MappingTable#replace(Mappable, Object, Object)}.
     */
    @Test
    public void testReplace3() {
        final MappingTable<String, Object> t = MappingTable.create();

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertTrue(t.replace(STRING, "Hello", "Dolly"));
        Assert.assertEquals(t.get(STRING), "Dolly");
        Assert.assertFalse(t.replace(STRING, "Hello", "World"));
        Assert.assertEquals(t.get(STRING), "Dolly");

        // Use null, works in a way with the standard HashMap underneath
        Assert.assertFalse(t.replace(INTEGER, null, 1));
        Assert.assertNull(t.get(INTEGER));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertTrue(t.replace(INTEGER, "invalid", 2));
        Assert.assertEquals(t.get(INTEGER), Integer.valueOf(2));
    }

    /**
     * Tests and {@link MappingTable#remove(Mappable)}.
     */
    @Test
    public void testRemove1() {
        final MappingTable<String, Object> t = MappingTable.create();

        Assert.assertNull(t.remove(STRING));
        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertEquals(t.remove(STRING), "Hello");
        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertNull(t.remove(INTEGER));
    }

    /**
     * Tests {@link MappingTable#remove(Mappable, Object)}.
     */
    @Test
    public void testRemove2() {
        final MappingTable<String, Object> t = MappingTable.create();

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertFalse(t.remove(STRING, "Dolly"));
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");
        Assert.assertTrue(t.remove(STRING, "Hello"));
        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertTrue(t.remove(INTEGER, "invalid"));
        Assert.assertFalse(t.mappings().containsKey(INTEGER.remap()));
    }

    /**
     * Tests {@link MappingTable#merge(Mappable, Object, BiFunction)}.
     */
    @Test
    public void testMerge() {
        final MappingTable<String, Object> t = MappingTable.create();

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertNull(t.merge(STRING, "Dolly", (o, v) -> {
            Assert.assertEquals(o, "Hello");
            Assert.assertEquals(v, "Dolly");
            return null;
        }));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));
        Assert.assertEquals(t.merge(STRING, "Hello", (o, v) -> null), "Hello");

        Assert.assertEquals(t.merge(STRING, "Dolly", (o, v) -> o), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");

        Assert.assertEquals(t.merge(STRING, "Dolly", (o, v) -> v), "Dolly");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Dolly");

        Assert.assertEquals(t.merge(STRING, "Hello", (o, v) -> v + " " + o), "Hello Dolly");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello Dolly");

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertEquals(t.merge(INTEGER, 1, (o, v) -> null), Integer.valueOf(1));
        Assert.assertEquals(t.mappings().get(INTEGER.remap()), Integer.valueOf(1));
    }

    /**
     * Tests {@link MappingTable#compute(Mappable, BiFunction)}.
     */
    @Test
    public void testCompute() {
        final MappingTable<String, Object> t = MappingTable.create();

        Assert.assertNull(t.compute(STRING, (k, v) -> {
            Assert.assertEquals(k, STRING.remap());
            Assert.assertNull(v);
            return null;
        }));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertNull(t.compute(STRING, (k, v) -> {
            Assert.assertEquals(k, STRING.remap());
            Assert.assertEquals(v, "Hello");
            return null;
        }));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        Assert.assertEquals(t.compute(STRING, (k, v) -> "Hello"), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");

        Assert.assertEquals(t.compute(STRING, (k, v) -> {
            Assert.assertEquals(k, STRING.remap());
            Assert.assertEquals(v, "Hello");
            return "Dolly";
        }), "Dolly");

        Assert.assertEquals(t.compute(STRING, (k, v) -> "Hello" + " " + v), "Hello Dolly");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello Dolly");

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertNull(t.compute(INTEGER, (k, v) -> null));
        Assert.assertFalse(t.mappings().containsKey(INTEGER.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertEquals(t.compute(INTEGER, (k, v) -> 1), Integer.valueOf(1));
        Assert.assertEquals(t.mappings().get(INTEGER.remap()), Integer.valueOf(1));
    }

    /**
     * Tests {@link MappingTable#computeIfAbsent(Mappable, Function)}.
     */
    @Test
    public void testComputeIfAbsent() {
        final MappingTable<String, Object> t = MappingTable.create();

        Assert.assertNull(t.computeIfAbsent(STRING, k -> {
            Assert.assertEquals(k, STRING.remap());
            return null;
        }));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        Assert.assertEquals(t.computeIfAbsent(STRING, k -> "Hello"), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");

        Assert.assertEquals(t.computeIfAbsent(STRING, k -> "Dolly"), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");

        Assert.assertEquals(t.computeIfAbsent(STRING, k -> null), "Hello");
        Assert.assertEquals(t.mappings().get(STRING.remap()), "Hello");

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertNull(t.computeIfAbsent(INTEGER, k -> null));
        Assert.assertFalse(t.mappings().containsKey(INTEGER.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertEquals(t.computeIfAbsent(INTEGER, k -> 1), Integer.valueOf(1));
        Assert.assertEquals(t.mappings().get(INTEGER.remap()), Integer.valueOf(1));
    }

    /**
     * Tests {@link MappingTable#computeIfPresent(Mappable, BiFunction)}.
     */
    @Test
    public void testComputeIfPresent() {
        final MappingTable<String, Object> t = MappingTable.create();

        Assert.assertNull(t.computeIfPresent(STRING, (k, v) -> {
            Assert.fail();
            return null;
        }));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertEquals(t.computeIfPresent(STRING, (k, v) -> {
            Assert.assertEquals(k, STRING.remap());
            Assert.assertEquals(v, "Hello");
            return "Dolly";
        }), "Dolly");

        Assert.assertEquals(t.mappings().get(STRING.remap()), "Dolly");

        Assert.assertNull(t.computeIfPresent(STRING, (k, v) -> null));
        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertNull(t.computeIfPresent(INTEGER, (k, v) -> {
            Assert.assertNull(v);
            return null;
        }));

        Assert.assertFalse(t.mappings().containsKey(INTEGER.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertNull(t.computeIfPresent(INTEGER, (k, v) -> {
            Assert.fail();
            return 1;
        }));

        Assert.assertNull(t.mappings().get(INTEGER.remap()));
    }

    /**
     * Tests {@link MappingTable#add(Mappable, Object)}
     */
    @Test
    public void testAdd() {
        final MappingTable<String, Object> t = MappingTable.create();
        Assert.assertEquals(t.add(STRING, "Hello").get(STRING), "Hello");

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertEquals(t.add(STRING, "Hello").get(STRING), "Hello");

        Assert.assertEquals(t.add(STRING, "Another").get(STRING), "Hello");
    }

    /**
     * Tests {@link MappingTable#set(Mappable, Object)}.
     */
    @Test
    public void testSet() {
        final MappingTable<String, Object> t = MappingTable.create();
        Assert.assertEquals(t.set(STRING, "Hello").get(STRING), "Hello");

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertEquals(t.set(STRING, "Hello").get(STRING), "Hello");

        Assert.assertEquals(t.set(STRING, "Another").get(STRING), "Another");
    }

    /**
     * Tests {@link MappingTable#patch(Mappable, Object)}.
     */
    @Test
    public void testPatch2() {
        final MappingTable<String, Object> t = MappingTable.create();
        Assert.assertEquals(t.patch(STRING, "Hello").get(STRING), "Hello");
        Assert.assertEquals(t.patch(STRING, "Dolly").get(STRING), "Dolly");
        Assert.assertNull(t.patch(STRING, null).get(STRING));
        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertEquals(t.patch(STRING, "Hello").get(STRING), "Hello");

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertNull(t.patch(STRING, null).get(STRING));
        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));
    }

    /**
     * Tests {@link MappingTable#patch(Mappable, Object, BiFunction)}.
     */
    @Test
    public void testPatch3() {
        final MappingTable<String, Object> t = MappingTable.create();

        Assert.assertEquals(t.patch(STRING, "Hello", (o, v) -> {
            Assert.fail();
            return null;
        }).get(STRING), "Hello");

        Assert.assertEquals(t.patch(STRING, "Dolly", (o, v) -> {
            Assert.assertEquals(o, "Hello");
            Assert.assertEquals(v, "Dolly");
            return v;
        }).get(STRING), "Dolly");

        Assert.assertNull(t.patch(STRING, null, (o, v) -> {
            Assert.assertNull(v);
            Assert.assertEquals(o, "Dolly");
            return null;
        }).get(STRING));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        Assert.assertNull(t.patch(STRING, null, (o, v) -> {
            Assert.fail();
            return null;
        }).get(STRING));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertEquals(t.patch(STRING, "Hello", (o, v) -> {
            Assert.fail();
            return o;
        }).get(STRING), "Hello");

        t.mappings().put(STRING.remap(), 1); // Put an invalid value
        Assert.assertNull(t.patch(STRING, null, (o, v) -> {
            Assert.fail();
            return o;
        }).get(STRING));

        Assert.assertFalse(t.mappings().containsKey(STRING.remap()));
    }

    /**
     * Tests {@link MappingTable#discard(Mappable)}.
     */
    @Test
    public void testDiscard() {
        final MappingTable<String, Object> t = MappingTable.create();

        Assert.assertFalse(t.discard(STRING).mappings().containsKey(STRING.remap()));

        t.mappings().put(STRING.remap(), "Hello");
        Assert.assertFalse(t.discard(STRING).mappings().containsKey(STRING.remap()));

        t.mappings().put(INTEGER.remap(), "invalid");
        Assert.assertFalse(t.discard(INTEGER).mappings().containsKey(INTEGER.remap()));
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        Assert.assertSame(new Serialized<>(MappingTable.empty()).build(), MappingTable.empty());

        final Map<String, Object> m = new HashMap<>();
        m.put("string", "Hello");
        m.put("integer", 1);
        final MappingTable<String, Object> t = MappingTable.adapt(m);
        Assert.assertEquals(new Serialized<>(t).build(), t);
    }
}
