package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.model.Appointment;
import org.example.service.AppointmentService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Small embedded REST/HTTP web service exposing appointment data as JSON.
 * Runs alongside the existing Swing desktop app, reusing AppointmentService.
 * This satisfies the "distributed application with web services" requirement
 * without altering any existing Swing screens or business logic.
 */
public class ApiServer {

    private final AppointmentService appointmentService;

    public ApiServer() {
        this.appointmentService = new AppointmentService();
    }

    public void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/appointments", new AppointmentsHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("[Web Service] REST API started at http://localhost:" + port + "/api/appointments");
        } catch (IOException e) {
            System.out.println("[Web Service] Could not start API server: " + e.getMessage());
        }
    }

    private class AppointmentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            String response;
            try {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
                response = buildResponse(params);
            } catch (SQLException e) {
                response = "{\"error\": \"Database error: " + escapeJson(e.getMessage()) + "\"}";
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String buildResponse(Map<String, String> params) throws SQLException {
            if (params.containsKey("id")) {
                int apptNo = Integer.parseInt(params.get("id"));
                Appointment a = appointmentService.getAppointmentByNumber(apptNo);
                return a != null ? appointmentToJson(a) : "{\"error\": \"Appointment not found\"}";
            }

            List<Appointment> list;
            if (params.containsKey("status")) {
                list = appointmentService.getAppointmentsByStatus(params.get("status"));
            } else if ("today".equalsIgnoreCase(params.get("filter"))) {
                String today = LocalDate.now().toString();
                list = appointmentService.getAllAppointments().stream()
                        .filter(a -> today.equals(a.getAppointmentDate()))
                        .toList();
            } else {
                list = appointmentService.getAllAppointments();
            }
            return appointmentListToJson(list);
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new java.util.HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    private String appointmentListToJson(List<Appointment> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(appointmentToJson(list.get(i)));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String appointmentToJson(Appointment a) {
        return "{"
                + "\"appointmentNumber\": " + a.getAppointmentNumber() + ","
                + "\"patientName\": \"" + escapeJson(a.getPatientName()) + "\","
                + "\"dentistName\": \"" + escapeJson(a.getDentistName()) + "\","
                + "\"treatmentType\": \"" + escapeJson(a.getTreatmentType()) + "\","
                + "\"appointmentDate\": \"" + escapeJson(a.getAppointmentDate()) + "\","
                + "\"appointmentTime\": \"" + escapeJson(a.getAppointmentTime()) + "\","
                + "\"status\": \"" + escapeJson(a.getStatus()) + "\""
                + "}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}