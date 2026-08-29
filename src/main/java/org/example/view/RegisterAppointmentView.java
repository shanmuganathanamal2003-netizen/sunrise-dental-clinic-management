package org.example.view;

import org.example.model.User;

/**
 * RegisterAppointmentView - Alias for AddAppointmentView
 * (Maintained for backward compatibility)
 */
public class RegisterAppointmentView extends
        AddAppointmentView {

    public RegisterAppointmentView(User user) {
        super(user);
    }
}

