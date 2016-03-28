package com.github.masahitojp.botan.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@Slf4j
@UtilityClass
@SuppressWarnings("unused")
public final class BotanUtils {

    public static <T> T getRandomValue(final List<T> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }

    public static Optional<String> envToOpt(final String envName) {
        Optional<String> system = Optional.ofNullable(System.getenv(envName));
        if(system.isPresent()) {
            return system;
        }
        return Optional.ofNullable(System.getProperty(envName));
    }

    public static void readDotEnv() {
        // read .env file
        final Path path = Paths.get(".env");
        if (Files.exists(path)) {
            try (final InputStream is = new FileInputStream(path.toFile())) {
                final Properties p = new Properties();
                p.load(is);
                System.setProperties(p);
            } catch (final IOException e) {
                log.warn("[Botan] .env file ; {}", e);
            }
        }
    }

}