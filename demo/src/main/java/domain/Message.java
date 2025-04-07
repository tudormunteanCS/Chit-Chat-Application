package domain;

import java.time.LocalDate;
import java.util.ArrayList;

public class Message extends Entity<Long> {
    private String from;
    private String to;
    private String message;



    public Message(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}
