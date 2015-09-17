# botan(牡丹)

[![Circle CI](https://circleci.com/gh/masahitojp/botan-core.svg?style=svg)](https://circleci.com/gh/masahitojp/botan-core)

tiny chat bot framework for Java SE 8.(like a Hubot)

## requirement

Java8

## Getting Started

### Write simple java application

```java
package com.github.masahitojp.implementation;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.exception.BotanException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackBot {

    static public void main(final String[] Args) {

        final Botan botan = new Botan.BotanBuilder()
                .addEnvironmentVariablesToGlobalProperties()
                .build();
        try {
            botan.start();
        } catch (final BotanException ex) {
            log.warn(ex.getMessage());
        }

    }
}
```

```java
package com.github.masahitojp.implementation.handlers;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;

@SuppressWarnings("unused")
public class PingMessageHandlers implements BotanMessageHandlers {

    @Override
    public void register(final Robot robot) {
        robot.respond(
                "ping",
                "ping method",
                message -> message.reply("pong")
        );
    }
}

```
if you want to more examples, see [sample project](https://github.com/masahitojp/botan-example).

### Add dependency to your build.gradle

```groovy
apply plugin: 'java'

repositories.mavenCentral()

dependencies {
	compile compile 'com.github.masahitojp:botan:0.0.1.+'
}

sourceCompatibility = targetCompatibility = 1.8
```

### Run and View

    show your slack team


## Features
Currently, supports following adapters and brains:

 * Adapter
  * [Slack](https://slack.com/)
 * Brain
  * [MapDB](http://www.mapdb.org/)

## License

Apache License, Version 2.0

## Inspired projects

* https://hubot.github.com/
* https://www.lita.io/
* https://github.com/r7kamura/ruboty
* https://github.com/liquidz/jubot

## How to write Handlers

* create class which implements BotanMessageHandlers.
* Override register method
  * Robot#respond
  
```java
package com.github.masahitojp.implementation.handlers;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;

@SuppressWarnings("unused")
public class PingMessageHandlers implements BotanMessageHandlers {

    @Override
    public void register(final Robot robot) {
        robot.respond(
                "ping",
                "ping method",
                message -> message.reply("pong")
        );
    }
}

```
