package servlet;

import constants.Constants;
import model.Card;
import model.GsonFactory;
import repository.CardRepository;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/card")
public class CardServlet extends HttpServlet {

    public static final String CATEGORY_ID = "categoryId";
    public static final String ID = "id";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String id = req.getParameter(ID);
        String categoryId = req.getParameter(CATEGORY_ID);

        try (CardRepository repository = new CardRepository()) {
            if (id != null) {
                Card card = repository.getById(Integer.parseInt(id));
                if (card != null)
                    writeCard(resp, card);
                else {
                    throw new IllegalArgumentException("No card found with id=" + id);
                }

            } else if (categoryId != null) {
                List<Card> cards = repository.getByCategoryId(Integer.parseInt(categoryId));
                if (!cards.isEmpty())
                    writer.println(GsonFactory.getGson().toJson(cards));
                else {
                    throw new IllegalArgumentException("No card with categoryId=" + categoryId);
                }

            } else {
                List<Card> cards = repository.getCards();
                writer.println(GsonFactory.getGson().toJson(cards));
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String question = req.getParameter(QUESTION);
        String answer = req.getParameter(ANSWER);
        String categoryIdString = req.getParameter(CATEGORY_ID);

        try (CardRepository repository = new CardRepository()) {
            if (question != null && answer != null && categoryIdString != null) {
                Card card = new Card(question, answer, Integer.parseInt(categoryIdString), LocalDate.now());
                if (repository.add(card)) {
                    writeCard(resp, card);
                } else {
                    throw new IllegalArgumentException("Card has already exists.");
                }
            } else {
                throw new IllegalArgumentException("Not enough data to add.");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Add failed: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        try (CardRepository repository = new CardRepository()) {
            Card update = GsonFactory.getGson().fromJson(req.getReader(), Card.class);
            if (update != null && repository.update(update)) {
                writeCard(resp, update);
            } else {
                throw new IllegalArgumentException("received 'null' as card to update.");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Update failed: " + e.getMessage());
        }

//        String idString = req.getParameter(ID);
//        String question = req.getParameter(QUESTION);
//        String answer = req.getParameter(ANSWER);
//        String categoryIdString = req.getParameter(CATEGORY_ID);
//        String creationDate = req.getParameter(CREATION_DATE);
//
//        if (idString != null) {
//            try (CardRepository repository = new CardRepository()) {
//                Card card = repository.getById(Integer.parseInt(idString));
//                if (card != null) {
//                    if (question != null) card.setQuestion(question);
//                    if (answer != null) card.setAnswer(answer);
//                    if (categoryIdString != null) card.setCategoryId(Integer.parseInt(categoryIdString));
//                    if (creationDate != null) card.setCreationDate(LocalDate.parse(creationDate));
//
//                    if (!repository.update(card)) throw new IllegalArgumentException();
//
//                    writer.println(gson.toJson(card));
//                } else {
//                    writer.println("Card by id=" + idString + " not found.");
//                    resp.setStatus(400);
//                }
//            } catch (Exception e) {
//                writer.println("Failed to update card: " + e.getMessage());
//                resp.setStatus(400);
//            }
//        } else {
//            writer.println("Update failed.");
//            resp.setStatus(400);
//        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setReqAndRespSettings(req, resp);
        PrintWriter writer = resp.getWriter();

        String idString = req.getParameter(ID);

        try (CardRepository repository = new CardRepository()) {
            Card card = repository.getById(Integer.parseInt(idString));
            if (card != null && repository.delete(card)) {
                writeCard(resp, card);
            } else {
                throw new IllegalArgumentException("db can't delete card");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            writer.println("Failed to delete card: " + e.getMessage());
        }
    }

    private void setReqAndRespSettings(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding(Constants.UTF_8);
        resp.setCharacterEncoding(Constants.UTF_8);
        resp.setContentType(Constants.APPLICATION_JSON_CHARSET_UTF_8);
    }

    private void writeCard(HttpServletResponse resp, Card card) throws IOException {
        resp.setCharacterEncoding(Constants.UTF_8);
        resp.setContentType(Constants.APPLICATION_JSON_CHARSET_UTF_8);

        resp.getWriter().println(GsonFactory.getGson().toJson(card));
    }
}
