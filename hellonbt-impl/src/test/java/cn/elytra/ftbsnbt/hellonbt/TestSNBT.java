/*
 * All Rights Reserved
 *
 * Copyright (c) 2025 Feed The Beast Ltd
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elytra.ftbsnbt.hellonbt;

import org.glavo.nbt.tag.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"SameParameterValue", "DataFlowIssue"})
class TestSNBT {

    @Test
    public void testReadStream() throws IOException {
        try (InputStream inputStream = TestSNBT.class.getResourceAsStream("/test_snbt.snbt")) {
            assertNotNull(inputStream);

            CompoundTag tag = assertInstanceOf(CompoundTag.class, HelloSNBT.readLines(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().toList()));

            assertEquals("value", tag.getString("test_string"), "getString");
            assertEquals(1, tag.getByte("testBool"), "getBoolean(getByte)");
            assertEquals(1234, tag.getInt("testInt"), "getInt");
            assertEquals((short) 49, tag.getShort("testShort"), "getShort");
            assertEquals(304993938434993L, tag.getLong("testLong"), "getShort");
            assertEquals(49, getIntArray(tag, "intArray")[1], "getIntArray");
            assertEquals(49, getByteArray(tag, "byteArray")[1], "getByteArray");
            assertEquals(-34348L, getLongArray(tag, "longArray")[1], "getLongArray");
            assertTrue(Double.isInfinite(tag.getDouble("testDouble")), "getDouble (infinity)");
            assertEquals("c $##@! 'string' 3", assertInstanceOf(StringTag.class, getList(tag, "testList").getTag(2)).getAsString(), "getList (string)");

            assertFalse(contains(tag, "missingField"), "check for missing field");

            CompoundTag subTag = getCompound(tag, "testCompound");
            assertNotNull(subTag, "testCompound presence");
            assertEquals(5, subTag.getInt("s1"), "testCompound integer");
        }
    }

    private static int[] getIntArray(CompoundTag tag, String name) {
        return assertInstanceOf(IntArrayTag.class, tag.get(name)).getArray();
    }

    private static long[] getLongArray(CompoundTag tag, String name) {
        return assertInstanceOf(LongArrayTag.class, tag.get(name)).getArray();
    }

    private static byte[] getByteArray(CompoundTag tag, String name) {
        return assertInstanceOf(ByteArrayTag.class, tag.get(name)).getArray();
    }

    @SuppressWarnings("unchecked")
    private static ListTag<Tag> getList(CompoundTag tag, String name) {
        return assertInstanceOf(ListTag.class, tag.get(name));
    }

    private static CompoundTag getCompound(CompoundTag tag, String name) {
        return assertInstanceOf(CompoundTag.class, tag.get(name));
    }

    private static boolean contains(CompoundTag tag, String name) {
        return tag.get(name) != null;
    }
}
