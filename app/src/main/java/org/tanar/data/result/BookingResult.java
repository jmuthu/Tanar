package org.tanar.data.result;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class BookingResult {

    @Nullable
    private Integer error;

    public BookingResult(@Nullable Integer error) {
        this.error = error;
    }

    public BookingResult() {

    }

    @Nullable
    public Integer getError() {
        return error;
    }
}