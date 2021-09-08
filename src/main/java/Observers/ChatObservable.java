package Observers;

import Models.Message;

import java.util.List;

public interface ChatObservable {
    public Message getLastMessage();
}
