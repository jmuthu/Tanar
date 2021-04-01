package org.tanar.data.result;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private String displayName;
    @Nullable
    private Integer error;

    public LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    public LoginResult(@Nullable String displayName) {
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