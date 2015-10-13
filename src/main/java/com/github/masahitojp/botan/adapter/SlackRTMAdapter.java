package com.github.masahitojp.botan.adapter;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter for Slack Real Time API
 */
@SuppressWarnings("unused")
@Slf4j
public final class SlackRTMAdapter implements BotanAdapter {

    private final String apiToken;
    private final AtomicBoolean flag = new AtomicBoolean(true);
    private Botan botan;
    private SlackSession session;
    private final AtomicReference<Pattern> patternRef = new AtomicReference<>();

    public SlackRTMAdapter() {
        this(Optional.ofNullable(System.getProperty("slack.api.token")).orElse(""));
    }
    public SlackRTMAdapter(final String apiToken) {
        this.apiToken = apiToken;
    }

    private Pattern getPattern() {
        Pattern pattern = patternRef.get();
        if (pattern == null) {
            synchronized (this.patternRef) {
                final String convertSlackReferenceToBotName = "<@([^>]+)>[:,]?(\\s)+?(?<body>.*)";
                pattern = Pattern.compile(convertSlackReferenceToBotName, Pattern.DOTALL);
                this.patternRef.set(pattern);
            }
        }
        return pattern;
    }

    // Slackで"@botan body" と入力した際に、　<@DDDD> という内部表現になるので、差し替える
    private String convertSlackMessageContentForBotan(final String messageContent) {
        final Matcher matcher = getPattern().matcher(messageContent);
        if (matcher.find()) {
            return String.format("%s %s", session.sessionPersona().getUserName(), matcher.group("body"));
        } else {
            return messageContent;
        }
    }

    @Override
    public void run() throws BotanException {
        session = SlackSessionFactory.createWebSocketSlackSession(this.apiToken);
        try {
            session.connect();
            session.addMessagePostedListener((e, s)
                    -> {
                        final String body = convertSlackMessageContentForBotan(e.getMessageContent());
                        botan.receive(new BotanMessageSimple(
                                body,
                                e.getSender().getUserName(),
                                e.getSender().getUserName(),
                                e.getChannel().getId(),
                                e.getEventType().ordinal()

                        ));
                    }
            );
            while (flag.get()) {
                Thread.sleep(1000);
            }
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                session.disconnect();
            } catch (final  IOException ignore) {
            }
        }

    }

    @Override
    public void say(BotanMessage message) {
        session.sendMessageOverWebSocket(session.findChannelById(message.getTo()), message.getBody(), null);

    }

    @Override
    public void initialize(Botan botan) {
        this.botan = botan;
    }

    @Override
    public void beforeShutdown() {
        flag.compareAndSet(true, false);
    }



    public Optional<String> getFromAdapterName() {
        return Optional.of(session.sessionPersona().getUserName());
    }
}
