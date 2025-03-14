package com.springboot.backend.address.userapp.users_backend.models;

import jakarta.validation.constraints.NotBlank;

public class PostRequestDTO {

    @NotBlank
    private String content;

    @NotBlank
    private String title;

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

}
