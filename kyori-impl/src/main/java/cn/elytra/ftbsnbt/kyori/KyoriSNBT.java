package cn.elytra.ftbsnbt.kyori;

import cn.elytra.ftbsnbt.SNBT;
import net.kyori.adventure.nbt.BinaryTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class KyoriSNBT {
    private static final KyoriNBTAdaptor ADAPTOR = new KyoriNBTAdaptor();

    public static BinaryTag readLines(List<String> lines) {
        return SNBT.readLines(lines, ADAPTOR);
    }

    public static BinaryTag tryRead(Path path) throws IOException {
        return readLines(Files.readAllLines(path, StandardCharsets.UTF_8));
    }
}
