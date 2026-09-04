package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.INotificationDAO;
import com.sunrise.dental.dao.impl.NotificationDAOImpl;
import com.sunrise.dental.model.Notification;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/notifications")
public class NotificationServlet extends HttpServlet {

    private INotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        notificationDAO = new NotificationDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getEligibleUser(request.getSession(false));
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(JsonUtil.createErrorResponse("Notifications are available to admins and dentists only."));
            return;
        }

        List<Notification> notifications = notificationDAO.findUnreadByUser(user.getUserId());
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            json.append("{\"id\":").append(notification.getNotificationId())
                .append(",\"title\":\"").append(JsonUtil.escape(notification.getTitle()))
                .append("\",\"message\":\"").append(JsonUtil.escape(notification.getMessage()))
                .append("\",\"createdAt\":\"").append(JsonUtil.escape(String.valueOf(notification.getCreatedAt())))
                .append("\"}");
            if (i < notifications.size() - 1) json.append(",");
        }
        json.append("]");
        out.print(json);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getEligibleUser(request.getSession(false));
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(JsonUtil.createErrorResponse("Unauthorized."));
            return;
        }

        String rawId = request.getParameter("notificationId");
        try {
            int notificationId = Integer.parseInt(rawId);
            boolean marked = notificationDAO.markAsRead(notificationId, user.getUserId());
            out.print(marked ? JsonUtil.createSuccessResponse("Notification marked as read.")
                             : JsonUtil.createErrorResponse("Notification was already read."));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(JsonUtil.createErrorResponse("A valid notification id is required."));
        }
    }

    private User getEligibleUser(HttpSession session) {
        if (session == null) return null;
        Object value = session.getAttribute("user");
        if (!(value instanceof User)) return null;
        User user = (User) value;
        return user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.DENTIST ? user : null;
    }
}
