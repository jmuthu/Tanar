package org.tanar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.tanar.data.Repository;
import org.tanar.data.model.Student;
import org.tanar.data.model.Tutor;
import org.tanar.data.result.AppointmentResult;
import org.tanar.data.result.BookingResult;
import org.tanar.data.result.StatusResult;
import org.tanar.data.result.TutorsNearbyResult;

public class Bookings extends AppCompatActivity {

    private ListView students;
    private final MutableLiveData<AppointmentResult> appointmentResultMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<StatusResult> statusResultMutableLiveData = new MutableLiveData<>();
    private Repository repository;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        repository = Repository.getInstance();
        repository.getStudents(appointmentResultMutableLiveData);
        ImageView settings=(ImageView) findViewById(R.id.left_icon2);

        students = findViewById(R.id.studentRequests);

        appointmentResultMutableLiveData.observe(this, studentResult -> {
            if (studentResult == null) {
                return;
            }
            if (studentResult.getError() != null) {
                Toast.makeText(this, "Failed to fetch from Database.", Toast.LENGTH_LONG).show();
            }
            if (studentResult.getStudentList() != null) {
                adapter = new AppointmentAdapter(this, studentResult.getStudentList());

                // after passing this array list to our adapter
                // class we are setting our adapter to our list view.
                students.setAdapter(adapter);
            }

        });

        statusResultMutableLiveData.observe(this, statusResult ->  {
            if (statusResult == null) {
                return;
            }
            if (statusResult.getError() != null) {
                Toast.makeText(this, statusResult.getError(), Toast.LENGTH_LONG).show();
            } else {
                Student stu = adapter.getItem(statusResult.getPosition());
                stu.setStatus(statusResult.getStatus());
                adapter.notifyDataSetChanged();
            }

        });

        students.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student)parent.getItemAtPosition(position);

                // on the item click on our list view.
                //tutor.getEmail()
                //student email : loggedIn
                if (!student.getStatus().equals("Pending")) {
                    return;
                }

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(view.getContext());
                View mView = layoutInflaterAndroid.inflate(R.layout.appointmentconfirmation, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(view.getContext());
                alertDialogBuilderUserInput.setView(mView);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Approved", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                repository.updateStatus(student.getBookingId(),position, "Approved" ,statusResultMutableLiveData);
                            }
                        })

                        .setNegativeButton("Denied",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        repository.updateStatus(student.getBookingId(),position, "Denied" ,statusResultMutableLiveData);
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Bookings.this, TutorSetting.class);
                startActivity(i);
            }
        });

    }
}