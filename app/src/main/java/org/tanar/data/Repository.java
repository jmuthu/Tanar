package org.tanar.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.tanar.R;
import org.tanar.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class Repository {

    private static volatile Repository instance;
    private static final String TAG = "DataSource";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private Repository() {

    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void login(String username, String password, MutableLiveData<LoginResult> loginResult) {

        db.collection("Users").whereEqualTo("username", username).whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            LoggedInUser data =
                                    new LoggedInUser(document.getId()
                                            , document.get("firstName").toString()
                                            + " " + document.get("lastName").toString()
                                    );
                            loginResult.setValue(new LoginResult(data.getDisplayName()));
                            setLoggedInUser(data);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                });

    }


}