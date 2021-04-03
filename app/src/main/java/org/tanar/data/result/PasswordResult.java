package org.tanar.data.result;

import androidx.annotation.Nullable;

public class PasswordResult {


    @Nullable
    private Integer error;

    public PasswordResult() {

    }

    public PasswordResult(@Nullable Integer error) {
        this.error = error;
    }

    @Nullable
    public Integer getError() {
        return error;
    }



}
