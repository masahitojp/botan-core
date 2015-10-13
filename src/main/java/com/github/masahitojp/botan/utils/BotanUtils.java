package com.github.masahitojp.botan.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@UtilityClass
@SuppressWarnings("unused")
public final class BotanUtils {

    public static <T> T getRandomValue(final List<T> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }

    public static Optional<String> envToOpt(final String envName) {
        Optional<String> system = Optional.ofNullable(System.getProperty(envName));
        if(system.isPresent()) {
            return system;
        }
        return Optional.ofNullable(System.getenv(UpperUnderScoreToLowerDot(envName)));
    }

    private static String UpperUnderScoreToLowerDot(final String src) {
        return src.toLowerCase().replace("_", ".");
    }
}