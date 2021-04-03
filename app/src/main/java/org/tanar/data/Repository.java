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
import org.tanar.data.model.Student;
import org.tanar.data.model.Subject;
import org.tanar.data.model.Tutor;
import org.tanar.data.result.AppointmentResult;
import org.tanar.data.result.BookingResult;
import org.tanar.data.result.CreateUserResult;
import org.tanar.data.result.LoginResult;
import org.tanar.data.result.SubjectResult;
import org.tanar.data.result.TutorsNearbyResult;
import org.tanar.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
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
    private final Subject subject = null;
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

    public boolean isTutor() {
        return user.isTutor();
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void login(String email, String password, MutableLiveData<LoginResult> loginResultMutableLiveData) {

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
                            setLoggedInUser(data);
                            loginResultMutableLiveData.setValue(new LoginResult(data.getDisplayName()));
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        loginResultMutableLiveData.setValue(new LoginResult(R.string.login_failed));
                    }
                });

    }

    public void createBooking(String tutorId, int position, String status, String message, MutableLiveData<BookingResult> bookingResultMutableLiveData) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("tutorId", tutorId);
        booking.put("studentMessage", message);
        booking.put("status", status);
        booking.put("studentId", user.getUserId());

        db.collection("Bookings").document().set(booking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Booking successful.");
                        bookingResultMutableLiveData.setValue(new BookingResult(position));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error booking.", e);
                        bookingResultMutableLiveData.setValue(new BookingResult(R.string.booking_failed));
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
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("classNumber", classNumber);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("subject", subject);
        userMap.put("expyear", expYear);
        userMap.put("password", password);
        userMap.put("isTutor", isTutor);
        userMap.put("latitude", latitude);
        userMap.put("longitude", longitude);
        userMap.put("altitude", altitude);
        userMap.put("rating", 0.0);

        db.collection("Users").document(email).set(userMap)
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

    public void getTutorsNearby(MutableLiveData<TutorsNearbyResult> tutorsNearbyResultMutableLiveData) {
        db.collection("Users").whereEqualTo("isTutor", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Tutor> tutorList = new ArrayList<Tutor>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Double distance = Utils.distance(user.getLatitude(),
                                    document.getDouble("latitude"),
                                    user.getLongitude(),
                                    document.getDouble("longitude"),
                                    user.getAltitude(),
                                    document.getDouble("altitude"));
                            Tutor tutor = new Tutor(document.getString("name"),
                                    document.getString("email"),
                                    document.getString("phoneNumber"),
                                    document.getString("subject"),
                                    document.getString("classNumber"),
                                    document.getString("expyear"),
                                    distance,
                                    "Add");
                            tutorList.add(tutor);
                        }

                        //Sort the Tutors by their distance
                        Collections.sort(tutorList, (t1, t2) -> Double.compare(t1.getDistance(), t2.getDistance()));
                        db.collection("Bookings").whereEqualTo("studentId", user.getUserId()).get().addOnCompleteListener(bookingTask -> {
                                    if (bookingTask.isSuccessful() && !bookingTask.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : bookingTask.getResult()) {
                                            for (Tutor tutor : tutorList) {
                                                if (tutor.getEmail().equals(document.getString("tutorId"))) {
                                                    tutor.setStatus(document.getString("status"));
                                                }

                                            }
                                        }
                                        tutorsNearbyResultMutableLiveData.setValue(new TutorsNearbyResult(tutorList));
                                    } else {
                                        Log.w(TAG, "Error getting documents.", bookingTask.getException());
                                        tutorsNearbyResultMutableLiveData.setValue(new TutorsNearbyResult(R.string.get_tutor_failed));
                                    }
                                }

                        );
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        tutorsNearbyResultMutableLiveData.setValue(new TutorsNearbyResult(R.string.get_tutor_failed));
                    }
                });

    }

    public void getStudents(MutableLiveData<AppointmentResult> appointmentResultMutableLiveDataResult) {
        db.collection("Bookings").whereEqualTo("tutorId", user.getUserId()).get().
                addOnCompleteListener(bookingTask -> {
                            if (bookingTask.isSuccessful() && !bookingTask.getResult().isEmpty()) {
                                List<Student> studentList = new ArrayList<Student>();
                                db.collection("Users").whereEqualTo("isTutor", false).get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                for (QueryDocumentSnapshot bookingDoc : bookingTask.getResult()) {
                                                    for (QueryDocumentSnapshot studentDoc : task.getResult()) {
                                                        if (bookingDoc.getString("studentId").equals(studentDoc.getId())) {
                                                            Double distance = Utils.distance(user.getLatitude(),
                                                                    studentDoc.getDouble("latitude"),
                                                                    user.getLongitude(),
                                                                    studentDoc.getDouble("longitude"),
                                                                    user.getAltitude(),
                                                                    studentDoc.getDouble("altitude"));
                                                            Student student = new Student(studentDoc.getString("name"),
                                                                    studentDoc.getString("email"),
                                                                    studentDoc.getString("phoneNumber"),
                                                                    studentDoc.getString("subject"),
                                                                    studentDoc.getString("classNumber"),
                                                                    distance,
                                                                    bookingDoc.getString("status"));
                                                            studentList.add(student);
                                                            break;
                                                        }
                                                    }
                                                }
                                                appointmentResultMutableLiveDataResult.setValue(new AppointmentResult(studentList));
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                                appointmentResultMutableLiveDataResult.setValue(new AppointmentResult(R.string.get_appointments_failed));
                                            }
                                        });
                            } else {
                                Log.w(TAG, "Error getting documents.", bookingTask.getException());
                                appointmentResultMutableLiveDataResult.setValue(new AppointmentResult(R.string.get_appointments_failed));
                            }
                        }

                );
    }


    public void getSubjects(MutableLiveData<SubjectResult> subjectResultMutableLiveData) {
        db.collection("Subjects").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Subject> subjectList = new ArrayList<Subject>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Subject subject = new Subject(document.getId(), document.getString("Name"));
                            subjectList.add(subject);
                        }
                        subjectResultMutableLiveData.setValue(new SubjectResult(subjectList));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        subjectResultMutableLiveData.setValue(new SubjectResult(R.string.login_failed));
                    }
                });
    }
}