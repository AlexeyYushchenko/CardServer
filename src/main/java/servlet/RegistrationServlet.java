package servlet;

import constants.Constants;
import model.GsonFactory;
import model.User;
import repository.UserRepository;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

/**
 * RegistrationServlet
 * Методы:
 * post – осуществляет прием данных и производит регистрацию нового пользователя в системе.
 * Корректно обрабатывает существование пользователя в базе данных
 */

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        try (UserRepository repository = new UserRepository()) {
            UserServlet.addUser(writer, req.getParameter(LOGIN), req.getParameter(PASSWORD), req.getParameter(NAME), repository);
        } catch (Exception e) {
            writer.println("Registration failed: " + e.getMessage());
            resp.setStatus(400);
        }
    }

    private void setReqAndRespSettings(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding(Constants.UTF_8);
        resp.setCharacterEncoding(Constants.UTF_8);
        resp.setContentType(Constants.APPLICATION_JSON_CHARSET_UTF_8);
    }
}
