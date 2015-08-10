package com.github.masahitojp.botan;

import com.github.masahitojp.botan.exception.BotanException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by masahito on 15/08/06.
 */
public class BotanMessage {
    private Chat chat;
    private MultiUserChat muc;
    private final Message message;

    public BotanMessage(final Chat chat, final Message message) {
        this.chat = chat;
        this.message = message;
    }
    public BotanMessage(final MultiUserChat muc, final Message message) {
        this.muc = muc;
        this.message = message;
    }

    public void reply(final String body) throws BotanException {
        try {
            // Todo : コンストラクタでnullが割り当てられないようにする
            if (chat != null)  {
                this.chat.sendMessage(body);
            }
            else if (muc!=null) {
                this.muc.sendMessage(body);
            }

        } catch (SmackException.NotConnectedException e) {
            throw new BotanException(e);
        }
    }
}
