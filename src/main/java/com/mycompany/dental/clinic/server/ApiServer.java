package com.mycompany.dental.clinic.server;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.DentistController;
import com.mycompany.dental.clinic.controller.UserController;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import com.mycompany.dental.clinic.model.Dentist;
import com.mycompany.dental.clinic.model.TreatmentType;
import com.mycompany.dental.clinic.model.User;
import com.mycompany.dental.clinic.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ApiServer {

    private static final String APPOINTMENTS_PATH = "/api/appointments";
    private static final String DENTISTS_PATH = "/api/dentists";
    private static final String TREATMENT_TYPES_PATH = "/api/treatment-types";

    // Matches any localhost dev-server port (Vite auto-bumps 5173 -> 5174 -> ... when busy)
    private static final Pattern ALLOWED_ORIGIN_PATTERN = Pattern.compile("^http://localhost:\\d+$");

    private final UserController userController = new UserController();
    private final AppoinmentController appointmentController = new AppoinmentController();
    private final DentistController dentistController = new DentistController();

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/login", this::handleLogin);
        server.createContext(APPOINTMENTS_PATH, this::handleAppointments);
        server.createContext(DENTISTS_PATH, this::handleDentists);
        server.createContext(TREATMENT_TYPES_PATH, this::handleTreatmentTypes);
        server.setExecutor(null);
        server.start();
        System.out.println("API server listening on port " + port);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
            return;
        }

        try {
            String body = readBody(exchange);
            Map<String, String> fields = JsonUtil.parseFlatObject(body);
            String username = fields.get("username");
            String password = fields.get("password");

            if (username == null || password == null) {
                sendJson(exchange, 400, "{\"message\":\"username and password are required\"}");
                return;
            }

            User user = userController.login(username, password);
            if (user == null) {
                sendJson(exchange, 401, "{\"message\":\"Invalid username or password\"}");
                return;
            }

            String json = String.format(
                    "{\"userId\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"role\":\"%s\"}",
                    user.getUserId(),
                    JsonUtil.escape(user.getUsername()),
                    JsonUtil.escape(user.getFullName()),
                    JsonUtil.escape(user.getRole())
            );
            sendJson(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"message\":\"Internal server error\"}");
        }
    }

    private void handleAppointments(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        if ("POST".equalsIgnoreCase(method) && path.equals(APPOINTMENTS_PATH)) {
            handleCreateAppointment(exchange);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && path.startsWith(APPOINTMENTS_PATH + "/")) {
            String appointmentNumber = path.substring((APPOINTMENTS_PATH + "/").length());
            handleGetAppointment(exchange, appointmentNumber);
            return;
        }

        sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
    }

    private void handleCreateAppointment(HttpExchange exchange) throws IOException {
        try {
            String body = readBody(exchange);
            Map<String, String> fields = JsonUtil.parseFlatObject(body);

            String patientName = fields.get("patientName");
            String address = fields.get("address");
            String contactNumber = fields.get("contactNumber");
            String dentistId = fields.get("dentistId");
            String treatmentId = fields.get("treatmentId");
            String appointmentDate = fields.get("appointmentDate");
            String appointmentTime = fields.get("appointmentTime");
            String userId = fields.get("userId");

            if (patientName == null || address == null || contactNumber == null || dentistId == null
                    || treatmentId == null || appointmentDate == null || appointmentTime == null
                    || userId == null) {
                sendJson(exchange, 400,
                        "{\"message\":\"All appointment fields, dentistId, treatmentId and userId are required\"}");
                return;
            }

            AppointmentDetails created = appointmentController.register(
                    patientName, address, contactNumber, Integer.parseInt(dentistId), Integer.parseInt(treatmentId),
                    appointmentDate, appointmentTime, Integer.parseInt(userId)
            );

            sendJson(exchange, 201, toJson(created));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"message\":\"Internal server error\"}");
        }
    }

    private void handleGetAppointment(HttpExchange exchange, String appointmentNumber) throws IOException {
        try {
            AppointmentDetails found = appointmentController.findByAppointmentNumber(appointmentNumber);
            if (found == null) {
                sendJson(exchange, 404, "{\"message\":\"Appointment not found\"}");
                return;
            }

            sendJson(exchange, 200, toJson(found));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"message\":\"Invalid appointment number\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"message\":\"Internal server error\"}");
        }
    }

    private void handleDentists(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
            return;
        }

        try {
            String query = getQueryParam(exchange, "query");
            List<Dentist> dentists = dentistController.search(query);
            String json = dentists.stream().map(this::toJson).collect(Collectors.joining(",", "[", "]"));
            sendJson(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"message\":\"Internal server error\"}");
        }
    }

    private void handleTreatmentTypes(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, "{\"message\":\"Method not allowed\"}");
            return;
        }

        try {
            List<TreatmentType> types = appointmentController.listTreatmentTypes();
            String json = types.stream().map(this::toJson).collect(Collectors.joining(",", "[", "]"));
            sendJson(exchange, 200, json);
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"message\":\"Internal server error\"}");
        }
    }

    private String toJson(Dentist d) {
        return String.format(
                "{\"dentistId\":%d,\"name\":\"%s\",\"specialization\":\"%s\"}",
                d.getDentistId(),
                JsonUtil.escape(d.getName()),
                JsonUtil.escape(d.getSpecialization())
        );
    }

    private String toJson(TreatmentType t) {
        return String.format(
                "{\"treatmentId\":%d,\"treatmentName\":\"%s\"}",
                t.getTreatmentId(),
                JsonUtil.escape(t.getTreatmentName())
        );
    }

    private String getQueryParam(HttpExchange exchange, String key) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery == null) {
            return null;
        }

        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) {
                continue;
            }
            String pairKey = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            if (pairKey.equals(key)) {
                return URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            }
        }

        return null;
    }

    private String toJson(AppointmentDetails d) {
        return String.format(
                "{\"appointmentNumber\":\"%s\",\"patientName\":\"%s\",\"address\":\"%s\","
                + "\"contactNumber\":\"%s\",\"dentistName\":\"%s\",\"treatmentType\":\"%s\","
                + "\"appointmentDate\":\"%s\",\"appointmentTime\":\"%s\"}",
                JsonUtil.escape(d.getAppointmentNumber()),
                JsonUtil.escape(d.getPatientName()),
                JsonUtil.escape(d.getAddress()),
                JsonUtil.escape(d.getContactNumber()),
                JsonUtil.escape(d.getDentistName()),
                JsonUtil.escape(d.getTreatmentType()),
                d.getAppointmentDate(),
                d.getAppointmentTime()
        );
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void addCorsHeaders(HttpExchange exchange) {

        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (origin != null && ALLOWED_ORIGIN_PATTERN.matcher(origin).matches()) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
        }

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization"
        );
    }

    private void sendJson(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        addCorsHeaders(exchange);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
