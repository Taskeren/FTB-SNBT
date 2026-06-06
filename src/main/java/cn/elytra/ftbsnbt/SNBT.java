/*
 * All Rights Reserved
 *
 * Copyright (c) 2025 Feed The Beast Ltd
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elytra.ftbsnbt;

import org.glavo.nbt.tag.CompoundTag;
import org.glavo.nbt.tag.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SNBT {

    private SNBT() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag readLines(List<String> lines) {
        return SNBTParser.read(lines);
    }

    public static CompoundTag readLines(List<String> lines, Tag nullTag) {
        return SNBTParser.read(lines, nullTag);
    }

    public static CompoundTag tryRead(Path path) throws IOException {
        return readLines(Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    public static CompoundTag tryRead(Path path, Tag nullTag) throws IOException {
        return readLines(Files.readAllLines(path, StandardCharsets.UTF_8), nullTag);
    }
}
