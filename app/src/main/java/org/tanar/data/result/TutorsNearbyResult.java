package org.tanar.data.result;

import androidx.annotation.Nullable;

import org.tanar.data.model.Tutor;

import java.util.List;

public class TutorsNearbyResult {
    private Integer error;
    private List<Tutor> tutorList;

    public TutorsNearbyResult(@Nullable Integer error) {
        this.error = error;
    }

    public TutorsNearbyResult(List<Tutor> tutorList) {
        this.tutorList = tutorList;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
