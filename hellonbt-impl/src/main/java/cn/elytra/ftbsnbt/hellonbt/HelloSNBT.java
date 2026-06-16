package cn.elytra.ftbsnbt.hellonbt;

import cn.elytra.ftbsnbt.SNBT;
import org.glavo.nbt.tag.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class HelloSNBT {

    private static final HelloNBTAdaptor ADAPTOR = new HelloNBTAdaptor();

    public static Tag readLines(List<String> lines) {
        return SNBT.readLines(lines, ADAPTOR);
    }

    public static Tag tryRead(Path path) throws IOException {
        return readLines(Files.readAllLines(path, StandardCharsets.UTF_8));
    }

}
