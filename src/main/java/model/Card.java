package model;

import java.time.LocalDate;
import java.util.Objects;

public class Card {
    private int id;
    private String question;
    private String answer;
    private int categoryId;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    public Card() {
    }

    public Card(String question, String answer, int categoryId, LocalDate creationDate) {
        this.question = question;
        this.answer = answer;
        this.categoryId = categoryId;
        this.creationDate = creationDate;
    }

    public Card(int id, String question, String answer, int categoryId, LocalDate creationDate) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.categoryId = categoryId;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public Card setId(int id) {
        this.id = id;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public Card setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public Card setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public Card setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Card setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && categoryId == card.categoryId && Objects.equals(question, card.question) && Objects.equals(answer, card.answer) && Objects.equals(creationDate, card.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answer, categoryId, creationDate);
    }

    @Override
    public String toString() {
        return "edu.yushchenko.cardfx.model.Card{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", categoryId=" + categoryId +
                ", creationDate=" + creationDate +
                '}';
    }
}
