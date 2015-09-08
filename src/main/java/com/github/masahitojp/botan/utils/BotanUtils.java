package com.github.masahitojp.botan.utils;

import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerSetter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public final class BotanUtils {


    public static <T> T getRandom(final List<T> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }
}