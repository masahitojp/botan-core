package com.github.masahitojp.botan.responder;


import java.util.function.Consumer;

public interface BotanMessageResponderSetter extends Consumer<BotanMessageResponder> {

    @SuppressWarnings("unused")
    String getDescription();

    @SuppressWarnings("unused")
    String getPatternString();
}