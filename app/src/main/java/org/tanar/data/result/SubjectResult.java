package org.tanar.data.result;

import androidx.annotation.Nullable;

import org.tanar.data.model.Subject;
import org.tanar.data.model.Tutor;

import java.util.List;

public class SubjectResult {
    private Integer error;
    private List<Subject> subjectList;

    public SubjectResult(@Nullable Integer error) {
        this.error = error;
    }

    public SubjectResult(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
