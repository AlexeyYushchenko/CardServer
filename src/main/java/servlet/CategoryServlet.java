package servlet;

import constants.Constants;
import model.Category;
import model.GsonFactory;
import repository.CategoryRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String USER_ID = "userId";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String id = req.getParameter(ID);
        String userId = req.getParameter(USER_ID);

        try (CategoryRepository repository = new CategoryRepository()) {
            if (id != null) {
                Category category = repository.getById(Integer.parseInt(id));
                if (category != null)
                    writer.println(GsonFactory.getGson().toJson(category));
                else {
                    throw new IllegalArgumentException("No such category with id=" + id);
                }

            } else if (userId != null) {
                List<Category> categories = repository.getCategories(Integer.parseInt(userId));
                if (!categories.isEmpty())
                    writer.println(GsonFactory.getGson().toJson(categories));
                else {
                    throw new IllegalArgumentException("No category with userId=" + userId);
                }

            } else {
                List<Category> categories = repository.getCategories();
                writer.println(GsonFactory.getGson().toJson(categories));
            }
        } catch (Exception e) {
            writer.println("Get failed: " + e.getMessage());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String name = req.getParameter(NAME);
        String userId = req.getParameter(USER_ID);

        try (CategoryRepository repository = new CategoryRepository()) {
            if (name != null && !name.isEmpty() && userId != null) {
                Category category = new Category(name, Integer.parseInt(userId));
                if (repository.add(category)) {
                    writer.println(GsonFactory.getGson().toJson(category));
                } else {
                    throw new IllegalArgumentException("Category has already exists.");
                }
            } else {
                throw new IllegalArgumentException("Not enough data to add.");
            }
        } catch (Exception e) {
            writer.println("Add failed: " + e.getMessage());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String idString = req.getParameter(ID);
        String name = req.getParameter(NAME);
        String userIdString = req.getParameter(USER_ID);

        try (CategoryRepository repository = new CategoryRepository()) {
            Category category = repository.getById(Integer.parseInt(idString));
            if (category != null) {
                if (name != null) category.setName(name);
                if (userIdString != null) category.setUserId(Integer.parseInt(userIdString));

                if (!repository.update(category)) throw new IllegalArgumentException("update on db level failed.");

                writer.println(GsonFactory.getGson().toJson(category));
            } else {
                throw new IllegalArgumentException("Category by id=" + idString + " not found.");
            }
        } catch (Exception e) {
            writer.println("Update failed: " + e.getMessage());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String idString = req.getParameter(ID);

        try (CategoryRepository repository = new CategoryRepository()) {
            Category category = repository.getById(Integer.parseInt(idString));
            if (category != null && repository.delete(category)) {
                writer.println(GsonFactory.getGson().toJson(category));
            } else {
                throw new IllegalArgumentException("Category by id=" + idString + " not found.");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Delete failed: " + e.getMessage());
        }
    }

    private void setReqAndRespSettings(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding(Constants.UTF_8);
        resp.setCharacterEncoding(Constants.UTF_8);
        resp.setContentType(Constants.APPLICATION_JSON_CHARSET_UTF_8);
    }
}
