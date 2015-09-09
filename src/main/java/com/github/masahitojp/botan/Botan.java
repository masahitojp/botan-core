package com.github.masahitojp.botan;

import com.github.masahitojp.botan.adapter.BotanAdapter;
import com.github.masahitojp.botan.brain.BotanBrain;
import com.github.masahitojp.botan.brain.LocalBrain;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Botan {
    public final BotanBrain brain;
    private final String name;
    private final BotanAdapter adapter;
    private final Robot robot;

    private Botan(final BotanBuilder builder) {
        this.adapter = builder.adapter;
        this.name = builder.name;
        this.brain = builder.brain;
        this.robot = new Robot(this);
    }

    public final String getName() {
        return name;
    }


    public void say(BotanMessage message) {
        this.adapter.say(message);
    }

    private Botan run() {
        this.robot.run();
        return this;
    }

    public final void receive(BotanMessageSimple message) {
        this.robot.receive(message);
    }

    @SuppressWarnings("unused")
    public final void start() throws BotanException {
        adapter.initialize(this);
        adapter.run();
    }

    @SuppressWarnings("unused")
    public final void stop() {
        adapter.beforeShutdown();
        brain.beforeShutdown();
        robot.doFinalize();
    }

    public static class BotanBuilder {
        private static String DEFAULT_NAME = "botan";
        private final BotanAdapter adapter;
        private String name = DEFAULT_NAME;
        private BotanBrain brain;

        public BotanBuilder(final BotanAdapter adapter) {

            this.adapter = adapter;
            if (adapter.getFromAdapterName().isPresent()) {
                this.name = adapter.getFromAdapterName().get();
            }
        }

        @SuppressWarnings("unused")
        public final BotanBuilder setName(final String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("unused")
        public final BotanBuilder setBrain(final BotanBrain brain) {
            this.brain = brain;
            return this;
        }

        @SuppressWarnings("unused")
        public final Botan build() {
            if (this.brain == null) {
                this.brain = new LocalBrain();
            }


            return new Botan(this).run();
        }
    }


}
