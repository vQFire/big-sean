package Application;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Firebase {
    /**
     * Connection to Firebase firestore service
     *
     * @author Youp van Leeuwen
     */
    static public Firestore db;

    static public void initialiseFirebase () {
        if (db != null) return;

        try {
            InputStream serviceAccount = Firebase.class.getResourceAsStream("/firebase/serviceAccountKey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            db = FirestoreClient.getFirestore();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
