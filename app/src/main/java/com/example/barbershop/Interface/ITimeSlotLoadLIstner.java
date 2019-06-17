package com.example.barbershop.Interface;

import com.example.barbershop.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadLIstner {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
