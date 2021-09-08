package Models;

import Application.Firebase;
import Observers.ChatObservable;
import Observers.ChatObserver;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.EventListener;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Chat implements ChatObservable {
    static public List<Message> messages = new ArrayList<>();
    private List<ChatObserver> observers = new ArrayList<>();
    private CollectionReference chatCollection;

    public Chat (String gameCode) {
        chatCollection = Firebase.db.collection("games").document(gameCode).collection("chat");

        try {
            if (chatCollection.get().get().isEmpty()) {
                Message message = new Message("The chat is open!");
                messages.add(message);
                chatCollection.document().set(message);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        chatCollection.orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirestoreException e) {
                if (value != null) {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        Message message = documentChange.getDocument().toObject(Message.class);
                        messages.add(message);

                        notifyAllObservers();
                    }
                }
            }
        });
    }

    public void sendMessage (Message message) {
        chatCollection.document().set(message);
    }

    @Override
    public Message getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public List<Message> getMessages() {
        return messages;
    }

    private void notifyAllObservers () {
        for (ChatObserver chatObserver: observers) {
            chatObserver.update(this);
        }
    }

    public void registerObserver (ChatObserver chatObserver) {
        observers.add(chatObserver);
    }
}
