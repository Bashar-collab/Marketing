package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Comment;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CommentResponse {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private LocalDate date;
    private List<CommentResponse> replies;
}
