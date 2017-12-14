package xing.utd.com.spectre;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

/**
 * Created by yaoxi on 12/11/2017.
 */

public class Message implements IMessage {

    String id;
    String text;
    Author author;
    Date createdAt;

    public Message(String id, String text, Author author, Date createdAt) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
