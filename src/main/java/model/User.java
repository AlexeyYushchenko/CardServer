package model;

import java.time.LocalDate;
import java.util.Objects;

public class User {
    private int id;
    private String login;
    private String password;
    private String name;
    private LocalDate regDate;

    public User() {
    }

    public User(String login, String password, String name, LocalDate regDate) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.regDate = regDate;
    }

    public User(int id, String login, String password, String name, LocalDate regDate) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.regDate = regDate;
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getRegDate() {
        return regDate;
    }

    public User setRegDate(LocalDate regDate) {
        this.regDate = regDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(login, user.login) && Objects.equals(password, user.password) && Objects.equals(name, user.name) && Objects.equals(regDate, user.regDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, name, regDate);
    }

    @Override
    public String toString() {
        return "edu.yushchenko.cardfx.model.User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", regDate=" + regDate +
                '}';
    }
}
