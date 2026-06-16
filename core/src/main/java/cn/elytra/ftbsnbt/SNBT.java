package cn.elytra.ftbsnbt;

import cn.elytra.ftbsnbt.internal.SNBTParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SNBT {

    private SNBT() {
        throw new UnsupportedOperationException();
    }

    public static <T> T readLines(List<String> lines, NBTAdaptor<T> adaptor) {
        return SNBTParser.read(lines, adaptor);
    }

    public static <T> T tryRead(Path path, NBTAdaptor<T> adaptor) throws IOException {
        return readLines(Files.readAllLines(path, StandardCharsets.UTF_8), adaptor);
    }
}
