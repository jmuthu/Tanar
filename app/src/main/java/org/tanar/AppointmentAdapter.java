package org.tanar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tanar.data.model.Student;
import org.tanar.data.model.Tutor;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Student> {


    // constructor for our list view adapter.
    public AppointmentAdapter(@NonNull Context context, List<Student> studentList) {
        super(context, 0,studentList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.bookinglistlayout, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Student student = getItem(position);

        // initializing our UI components of list view item.
        TextView stuPhone = listitemView.findViewById(R.id.studentPhoneNumber);
        TextView stuName = listitemView.findViewById(R.id.studentName);
        TextView message = listitemView.findViewById(R.id.studentMessage);
        TextView emailid = listitemView.findViewById(R.id.studentEmailid);
        TextView distance = listitemView.findViewById(R.id.studentDistance);
        ImageView bookingStatus = listitemView.findViewById(R.id.bookStatus);
        Drawable myDrawable = listitemView.getResources().getDrawable(R.drawable.notif);
        if (student.getStatus().equals("Approved")) {
            myDrawable = listitemView.getResources().getDrawable(R.drawable.approved);
        } else if (student.getStatus().equals("Denied")) {
            myDrawable = listitemView.getResources().getDrawable(R.drawable.denied);
        }
        bookingStatus.setImageDrawable(myDrawable);


        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        stuName.setText(student.getName());
        stuPhone.setText(student.getPhoneNumber());
        emailid.setText(student.getEmail());
        message.setText(student.getMessage());
        String dist = String.format("%.2f", student.getDistance());
        distance.setText(dist);

        return listitemView;
    }
}