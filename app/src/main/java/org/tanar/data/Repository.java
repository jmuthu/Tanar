package org.tanar.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.tanar.R;
import org.tanar.data.model.LoggedInUser;
import org.tanar.data.model.Subject;
import org.tanar.data.model.Tutor;
import org.tanar.data.result.CreateUserResult;
import org.tanar.data.result.LoginResult;
import org.tanar.data.result.SubjectResult;
import org.tanar.data.result.TutorsNearbyResult;
import org.tanar.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class Repository {

    private static final String TAG = "DataSource";
    private static volatile Repository instance;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;
    private final Subject subject = null;

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

    public void login(String email, String password, MutableLiveData<LoginResult> loginResult) {

        db.collection("Users").whereEqualTo("email", email).whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            LoggedInUser data =
                                    new LoggedInUser(document.getId(),
                                            document.get("name").toString(),
                                            document.getBoolean("isTutor"),
                                            document.getDouble("latitude"),
                                            document.getDouble("longitude"),
                                            document.getDouble("altitude"));
                            loginResult.setValue(new LoginResult(data.getDisplayName()));
                            setLoggedInUser(data);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                });

    }

    public void createUser(String name,
                           Boolean isTutor,
                           String email,
                           String classNumber,
                           String phoneNumber,
                           String subject,
                           String expYear,
                           String password,
                           Double latitude,
                           Double longitude,
                           Double altitude,
                           MutableLiveData<CreateUserResult> createUserResultLiveData) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("classNumber", classNumber);
        user.put("phoneNumber", phoneNumber);
        user.put("subject", subject);
        user.put("expyear", expYear);
        user.put("password", password);
        user.put("isTutor", isTutor);
        user.put("latitude", latitude);
        user.put("longitude", longitude);
        user.put("altitude", altitude);
        user.put("rating", 0.0);

        db.collection("Users").document(email).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully added!");
                        createUserResultLiveData.setValue(new CreateUserResult(name));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating user", e);
                        createUserResultLiveData.setValue(new CreateUserResult(R.string.create_user_failed));
                    }

                });
    }

    public void getTutorsNearby(MutableLiveData<TutorsNearbyResult> tutorsNearbyResult) {
        db.collection("Users").whereEqualTo("isTutor", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Tutor> tutorList = new ArrayList<Tutor>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Double distance = Utils.distance(user.getLatitude(), document.getDouble("latitude"),
                                    user.getLongitude(), document.getDouble("longitude"),
                                    user.getAltitude(), document.getDouble("altitude"));
                            Tutor tutor = new Tutor(document.getString("name"), document.getString("email"), document.getString("phoneNumber"), document.getString("subject"), document.getString("classNumber"), document.getString("expyear"), distance);
                            tutorList.add(tutor);
                        }

                        //Sort the Tutors by their distance
                        Collections.sort(tutorList, (t1, t2) -> Double.compare(t1.getDistance(), t2.getDistance()));
                        tutorsNearbyResult.setValue(new TutorsNearbyResult(tutorList));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        tutorsNearbyResult.setValue(new TutorsNearbyResult(R.string.login_failed));
                    }
                });

    }

    public void getSubjects(MutableLiveData<SubjectResult> subjectsResult) {
        db.collection("Subjects").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Subject> subjectList = new ArrayList<Subject>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Subject subject = new Subject(document.getId(), document.getString("Name"));
                            subjectList.add(subject);
                        }
                        subjectsResult.setValue(new SubjectResult(subjectList));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        subjectsResult.setValue(new SubjectResult(R.string.login_failed));
                    }
                });
    }
}