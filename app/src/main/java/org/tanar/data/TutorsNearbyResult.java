package org.tanar.data;

import androidx.annotation.Nullable;

import org.tanar.data.model.Tutor;

import java.util.List;

public class TutorsNearbyResult {
    private Integer error;
    private List<Tutor> tutorList;

    TutorsNearbyResult(@Nullable Integer error) {
        this.error = error;
    }

    TutorsNearbyResult(List<Tutor> tutorList) {
        this.tutorList = tutorList;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
