package com.github.masahitojp.botan.message;

import lombok.Data;

@Data
public class BotanMessageSimple {
    private final String body;
    private final String from;
    private final String fromName;
    private final String to;
    private final int type;

    public BotanMessageSimple(String body) {
        this.body = body;
        this.from = "";
        this.fromName = "";
        this.to = "";
        this.type = -1;
    }

    public BotanMessageSimple(String body, String from, String fromName, String to, int type) {
        this.body = body;
        this.from = from;
        this.fromName = fromName;
        this.to = to;
        this.type = type;
    }
}
