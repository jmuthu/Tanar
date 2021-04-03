package org.tanar.data.result;

import androidx.annotation.Nullable;

import org.tanar.data.model.Student;
import org.tanar.data.model.Tutor;

import java.util.List;

public class AppointmentResult {
    @Nullable
    private Integer error;
    @Nullable
    private List<Student> studentList;

    public AppointmentResult(@Nullable Integer error) {
        this.error = error;
    }

    public AppointmentResult(List<Student> studentList) {
        this.studentList = studentList;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
    @Nullable
    public List<Student> getStudentList() {return studentList;}
}

