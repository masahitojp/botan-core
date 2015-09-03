package com.github.masahitojp.botan.adapter;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class ComandLineAdapter implements BotanAdapter {
    private Botan botan;
    @Override
    public void run() throws BotanException {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            for (; ; ) {
                System.out.print(botan.getName() + "> ");

                final String line;
                try {
                    line = in.readLine();
                    botan.receive(new BotanMessageSimple(line, "in", "in", "out", 0));
                } catch (Exception e) {
                    System.out.println("Invalid Input");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void say(BotanMessage message) {
        System.out.println(message.getBody());
    }

    @Override
    public void initialize(Botan botan) {
        this.botan = botan;
    }

    @Override
    public void beforeShutdown() {

    }
}
