# botan(牡丹)

[![Circle CI](https://circleci.com/gh/masahitojp/botan.svg?style=svg)](https://circleci.com/gh/liquidz/jubot)

tiny chat bot framework for Java SE 8.

## requirement

Java8

## Getting Started

### Write simple java application

```java
import com.github.masahitojp.botan.adapter.SlackAdapter;
import com.github.masahitojp.botan.brain.MapDBBrain;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.utils.BotanUtils;

public class SlackBot {


	static public void main(String[] Args) {

        final String team = BotanUtils.envToOpt("SLACK_TEAM").orElse("");
        final String user = BotanUtils.envToOpt("SLACK_USERNAME").orElse("");
        final String pswd = BotanUtils.envToOpt("SLACK_PASSWORD").orElse("");
        final String channel = BotanUtils.envToOpt("SLACK_ROOM").orElse("");

        final Botan botan = new Botan.BotanBuilder(new SlackAdapter(team, user, pswd, channel))
                .build();

        try {
            botan.start();
        } catch (final BotanException ex) {
            ex.printStackTrace();
        }

	}
}
```

if you want to more examples, see [sample project](https://github.com/masahitojp/botan-example).

### Add dependency to your build.gradle

```groovy
apply plugin: 'java'

repositories.mavenCentral()

dependencies {
	compile 'com.github.masahitojp:botan:0.0.0.24'
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

# Inspired projects

* https://hubot.github.com/
* https://www.lita.io/
* https://github.com/r7kamura/ruboty
* https://github.com/liquidz/jubot