package com.student.management.dto;

import com.student.management.model.LeaveStatus;
import jakarta.validation.constraints.NotNull;

public class LeaveStatusUpdate {
    @NotNull
    private LeaveStatus status;

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
}

