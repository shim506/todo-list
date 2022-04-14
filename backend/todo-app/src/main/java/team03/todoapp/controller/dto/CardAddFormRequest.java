package team03.todoapp.controller.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import team03.todoapp.controller.CardLocation;
import team03.todoapp.repository.domain.Card;

public class CardAddFormRequest {

    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private String writer;
    @NotNull
    private CardLocation currentLocation;

    public CardAddFormRequest(String title, String content, String writer,
        CardLocation currentLocation) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.currentLocation = currentLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public CardLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(CardLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Card toEntity() {
        return new Card(
            this.title,
            this.content,
            this.writer,
            this.currentLocation,
            LocalDateTime.now(),
            null);
    }

}