package com.springboot.backend.address.userapp.users_backend.models;

import java.time.LocalDate;

public class FindAllPostsDTO {
    private Long id;
    private String content;
    private String title;
    private LocalDate postDate;
    private UserFindAllPostsDTO user;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDate getPostDate() {
        return postDate;
    }
    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }
    public UserFindAllPostsDTO getUser() {
        return user;
    }
    public void setUser(UserFindAllPostsDTO user) {
        this.user = user;
    }
    
    
}
