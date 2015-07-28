package com.github.masahitojp.botan.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Adapter for Slack.com
 */
public class SlackAdapter implements BotanAdapter {
    private final String team ;
    private final String user;
    private final String pswd;
    private final String room;

    public SlackAdapter(String team, String user, String pswd, String room) {
        this.team = team;
        this.user = user;
        this.pswd = pswd;
        this.room = room;
    }

    @Override
    public String getNickName() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.pswd;
    }

    @Override
    public String getRoomJabberId() {
        return room + getRoomHost();
    }

    @Override
    public String getHost() {
        return team + ".xmpp.slack.com";
    }

    @Override
    public String getRoomHost() {
        return "@conference." + this.getHost();
    }
}
