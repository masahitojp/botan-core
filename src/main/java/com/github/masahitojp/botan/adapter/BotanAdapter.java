package com.github.masahitojp.botan.adapter;

public interface BotanAdapter {
    String getNickName();

    String getPassword();

    String getRoomJabberId();

    String getHost();

    String getRoomHost();

    void initialize();

    void beforeShutdown();
}
