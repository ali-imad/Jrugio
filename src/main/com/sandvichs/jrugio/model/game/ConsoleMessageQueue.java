package com.sandvichs.jrugio.model.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.min;

public class ConsoleMessageQueue {
    private static int MAX_STRING_LENGTH;  // max size of string in chars
    private final int maxMessages;  // number of messages

    private final Queue<String> messages;

    public ConsoleMessageQueue(int maxMessages, int consoleWidth) {
        this.maxMessages = maxMessages;
        this.messages = new LinkedList<>();
        MAX_STRING_LENGTH = consoleWidth;
    }

    // REQUIRE: newMessage.length() < MAX_STRING_LENGTH * maxMessages
    // MODIFIES: this.messages
    // EFFECTS: Adds newMessage to the collection, splitting the string and removing old elements if necessary
    public void add(String newMessage) {
        if (newMessage.length() > MAX_STRING_LENGTH) {  // Split Case
            ArrayList<String> croppedMessages = splitToStringArray(newMessage);
            int messagesToAdd = croppedMessages.size() % this.maxMessages;
            // pop n elements in the list
            for (int i = 0; i < messagesToAdd; i++) {
                this.add(croppedMessages.get(i));  // Trust The Natural Recursion
            }
        } else {  // base case
            if (this.messages.size() == this.maxMessages) {
                this.messages.poll();
                this.messages.add(newMessage);
            } else {
                this.messages.add(newMessage);
            }
        }
    }

    // REQUIRE: newMessage.length() < MAX_STRING_LENGTH * maxMessages
    // MODIFIES:
    // EFFECTS: Returns a list of strings (in reverse order) split at every MAX_MESSAGES index
    private ArrayList<String> splitToStringArray(String newMessage) {
        ArrayList<String> strings = new ArrayList<>();
        int i;
        String croppedString;

        for (i = 0; i < newMessage.length(); i += MAX_STRING_LENGTH) {
            croppedString = newMessage.substring(i, min(newMessage.length(), i + MAX_STRING_LENGTH));
            strings.add(croppedString);
        }
        return strings;
    }

    public Queue<String> getMessages() {
        return messages;
    }

}
