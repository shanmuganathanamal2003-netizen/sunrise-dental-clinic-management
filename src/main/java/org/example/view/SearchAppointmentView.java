package org.example.view;

import org.example.model.User;

/**
 * SearchAppointmentView - Alias for unified PatientHistoryView
 * (Merged Patient Search and Clinical Records Screen)
 */
public class SearchAppointmentView extends PatientHistoryView {

    public SearchAppointmentView(User user) {
        super(user);
    }

    public SearchAppointmentView(User user, int preloadedPatientId) {
        super(user, preloadedPatientId);
    }
}

