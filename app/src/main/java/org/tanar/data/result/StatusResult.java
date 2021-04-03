package org.tanar.data.result;

import androidx.annotation.Nullable;

public class StatusResult {
    @Nullable
    private Integer error;
    private int position;
    private String status;

    public StatusResult(@Nullable Integer error) {
        this.error = error;
    }

    public StatusResult(String status, int position){
        this.status=status;
        this.position=position;
    }
    @Nullable
    public Integer getError() {
        return error;
    }

    public int getPosition() {
        return position;

    }
    public String getStatus(){
        return status;
    }


}