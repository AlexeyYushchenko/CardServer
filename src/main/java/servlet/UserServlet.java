package servlet;

import com.google.gson.Gson;
import constants.Constants;
import model.GsonFactory;
import model.User;
import repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String REG_DATE = "regDate";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String id = req.getParameter(ID);
        String login = req.getParameter(LOGIN);
        String password = req.getParameter(PASSWORD);

        try (UserRepository repository = new UserRepository()) {
            if (id != null) {
                User user = repository.getById(Integer.parseInt(id));
                if (user != null)
                    writer.println(GsonFactory.getGson().toJson(user));
                else {
                    throw new IllegalArgumentException("No such user with id=" + id);
                }

            } else if (login != null && password != null) {
                User user = repository.getUserByLoginAndPassword(login, password);
                if (user != null)
                    writer.println(GsonFactory.getGson().toJson(user));
                else {
                    throw new IllegalArgumentException("Wrong login or password or there is no user with login = " + login);
                }

            } else {
                List<User> users = repository.getUsers();
                writer.println(GsonFactory.getGson().toJson(users));
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String login = req.getParameter(LOGIN);
        String password = req.getParameter(PASSWORD);
        String name = req.getParameter(NAME);

        try (UserRepository repository = new UserRepository()) {
            addUser(writer, login, password, name, repository);
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Registration failed: " + e.getMessage());
        }
    }

    static void addUser(PrintWriter writer, String login, String password, String name, UserRepository repository) {
        if (login != null && password != null && name != null) {
            User user = new User(login, password, name, LocalDate.now());
            if (repository.add(user)) {
                writer.println(GsonFactory.getGson().toJson(user));
            } else {
                throw new IllegalArgumentException("User has already exists.");
            }
        } else {
            throw new IllegalArgumentException("Not enough data to register a user.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String idString = req.getParameter(ID);
        String login = req.getParameter(LOGIN);
        String password = req.getParameter(PASSWORD);
        String name = req.getParameter(NAME);
        String regDateString = req.getParameter(REG_DATE);

        try (UserRepository repository = new UserRepository()) {
            User user = repository.getById(Integer.parseInt(idString));
            if (user != null) {
                if (login != null) user.setLogin(login);
                if (password != null) user.setPassword(password);
                if (name != null) user.setName(name);
                if (regDateString != null) user.setRegDate(Date.valueOf(regDateString).toLocalDate());
                if (!repository.update(user)) throw new IllegalArgumentException();
                writer.println(GsonFactory.getGson().toJson(user));
            } else {
                throw new IllegalArgumentException("User by id=" + idString + " not found.");
            }
        } catch (Exception e) {
            writer.println("Failed to update user: " + e.getMessage());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String idString = req.getParameter(ID);
        try (UserRepository repository = new UserRepository()) {
            if (idString != null) {
                User user = repository.getById(Integer.parseInt(idString));
                if (user != null) {
                    if (repository.delete(user)) {
                        writer.println(GsonFactory.getGson().toJson(user));
                    } else {
                        throw new IllegalArgumentException("DB can't delete user.");
                    }
                } else {
                    throw new IllegalArgumentException("User by id=" + idString + " not found.");
                }
            } else {
                throw new IllegalArgumentException("'id' parameter is null.");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Failed to delete user: " + e.getMessage());
        }
    }

    private void setReqAndRespSettings(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding(Constants.UTF_8);
        resp.setCharacterEncoding(Constants.UTF_8);
        resp.setContentType(Constants.APPLICATION_JSON_CHARSET_UTF_8);
    }
}
