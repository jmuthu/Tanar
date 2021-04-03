package org.tanar.data.result;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class BookingResult {

    @Nullable
    private Integer error;
    private int position;

    public BookingResult(@Nullable Integer error) {
        this.error = error;
    }

    public BookingResult(int position) {
        this.position = position;
    }

    @Nullable
    public Integer getError() {
        return error;
    }

    public int getPosition() {
        return position;
    }
}