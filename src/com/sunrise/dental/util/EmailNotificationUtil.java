package com.sunrise.dental.util;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Patient;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Sends appointment confirmations through a configurable SMTP server. */
public final class EmailNotificationUtil {

    private static final String SMTP_HOST = System.getenv("SUNRISE_SMTP_HOST");
    private static final int SMTP_PORT = Integer.parseInt(System.getenv().getOrDefault("SUNRISE_SMTP_PORT", "587"));
    private static final String SMTP_USER = System.getenv("SUNRISE_SMTP_USER");
    private static final String SMTP_PASSWORD = System.getenv("SUNRISE_SMTP_PASSWORD");
    private static final String FROM_ADDRESS = System.getenv().getOrDefault("SUNRISE_SMTP_FROM", SMTP_USER);

    private EmailNotificationUtil() {}

    public static void sendAppointmentConfirmation(Patient patient, Appointment appointment) {
        if (!ValidationUtil.isValidEmail(patient.getEmail())) {
            System.out.println("Appointment email skipped: patient has no valid email address.");
            return;
        }
        if (isBlank(SMTP_HOST) || isBlank(SMTP_USER) || isBlank(SMTP_PASSWORD) || isBlank(FROM_ADDRESS)) {
            System.out.println("Appointment email skipped: SMTP environment variables are not configured.");
            return;
        }

        try (Socket initialSocket = SMTP_PORT == 465
                ? SSLSocketFactory.getDefault().createSocket(SMTP_HOST, SMTP_PORT)
                : new Socket(SMTP_HOST, SMTP_PORT)) {
            Socket socket = initialSocket;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
            expect(reader, 220);
            command(reader, writer, "EHLO sunrise-dental", 250);
            if (SMTP_PORT != 465) {
                command(reader, writer, "STARTTLS", 220);
                SSLSocket tlsSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault())
                        .createSocket(socket, SMTP_HOST, SMTP_PORT, true);
                tlsSocket.startHandshake();
                socket = tlsSocket;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
                command(reader, writer, "EHLO sunrise-dental", 250);
            }
            command(reader, writer, "AUTH LOGIN", 334);
            command(reader, writer, Base64.getEncoder().encodeToString(SMTP_USER.getBytes(StandardCharsets.UTF_8)), 334);
            command(reader, writer, Base64.getEncoder().encodeToString(SMTP_PASSWORD.getBytes(StandardCharsets.UTF_8)), 235);
            command(reader, writer, "MAIL FROM:<" + FROM_ADDRESS + ">", 250);
            command(reader, writer, "RCPT TO:<" + patient.getEmail() + ">", 250);
            command(reader, writer, "DATA", 354);
            writer.write("From: " + FROM_ADDRESS + "\r\n");
            writer.write("To: " + patient.getEmail() + "\r\n");
            writer.write("Subject: Sunrise Dental appointment confirmation\r\n\r\n");
            writer.write("Dear " + patient.getFullName() + ",\r\n\r\n");
            writer.write("Your appointment " + appointment.getApptNumber() + " has been added for "
                    + appointment.getApptDate() + " at " + appointment.getApptTime() + ".\r\n\r\n");
            writer.write("Regards,\r\nSunrise Dental Clinic\r\n.\r\n");
            writer.flush();
            expect(reader, 250);
            command(reader, writer, "QUIT", 221);
        } catch (IOException e) {
            System.err.println("Appointment email could not be sent: " + e.getMessage());
        }
    }

    private static void command(BufferedReader reader, BufferedWriter writer, String value, int expectedCode) throws IOException {
        writer.write(value + "\r\n");
        writer.flush();
        expect(reader, expectedCode);
    }

    private static void expect(BufferedReader reader, int expectedCode) throws IOException {
        String response = reader.readLine();
        if (response == null || response.length() < 3 || Integer.parseInt(response.substring(0, 3)) != expectedCode) {
            throw new IOException("SMTP response was not " + expectedCode + ": " + response);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}