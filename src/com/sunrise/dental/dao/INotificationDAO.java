package com.sunrise.dental.dao;

import com.sunrise.dental.model.Notification;

import java.util.List;

public interface INotificationDAO {

    void createPatientRegisteredNotification(String patientName, String patientNumber);

    List<Notification> findUnreadByUser(int userId);

    boolean markAsRead(int notificationId, int userId);
}
