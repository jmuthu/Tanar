package org.tanar.data;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class CreateUserResult {
    @Nullable
    private String displayName;
    @Nullable
    private Integer error;

    CreateUserResult(@Nullable Integer error) {
        this.error = error;
    }

    CreateUserResult(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getSuccess() {
        return displayName;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}