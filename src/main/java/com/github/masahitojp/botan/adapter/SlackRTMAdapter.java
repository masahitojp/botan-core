package com.github.masahitojp.botan.adapter;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import com.github.masahitojp.botan.utils.BotanUtils;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
    private Thread thread;
    private final AtomicReference<Pattern> patternRef = new AtomicReference<>();

    public SlackRTMAdapter() {
        this(BotanUtils.envToOpt("SLACK_API_TOKEN").orElse(""));
    }
    public SlackRTMAdapter(final String apiToken) {
        if(apiToken == null || apiToken.equals("")) {
            throw new NullPointerException("SLACK_API_TOKEN is null");
        }
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
        // bind event
        session.addMessagePostedListener((e, s)
                        -> {
                    final String body = convertSlackMessageContentForBotan(e.getMessageContent());
                    botan.receive(new BotanMessageSimple(
                            body,
                            e.getSender().getUserName(),
                            e.getSender().getUserName(),
                            e.getChannel().getName(),
                            e.getEventType().ordinal()
                    ));
                }
        );

        thread = new Thread(() -> {
            while (flag.get()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        thread.start();

    }

    @Override
    public void say(final BotanMessage message) {
        SlackChannel slackSession = session.findChannelByName(message.getTo());
        if (slackSession == null) {
            slackSession = session.findChannelById(message.getTo());
        }
        if (slackSession == null) {
            final SlackUser user = session.findUserByUserName(message.getTo());
            if(user != null)slackSession = session.findChannelById(user.getId());
        }
        if (slackSession != null) {
            session.sendMessageOverWebSocket(slackSession, message.getBody());
        } else {
            log.warn("reply failure {}", message.getTo());
        }
    }

    @Override
    public void initialize(final Botan botan) {

        session = SlackSessionFactory.createWebSocketSlackSession(this.apiToken);
        try {
            session.connect();
        } catch (final IOException e1) {
            log.warn("{}", e1);
        }
        this.botan = botan;
    }

    @Override
    public void beforeShutdown() {
        flag.compareAndSet(true, false);
        try {
            session.disconnect();
            thread.join();
        } catch (final IOException | InterruptedException e1) {
            log.warn("{}", e1);
        }
    }



    public Optional<String> getFromAdapterName() {
        if (session != null) {
            return Optional.ofNullable(session.sessionPersona().getUserName());
        } else {
            return Optional.empty();
        }
    }
}
