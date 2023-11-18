package com.example.jejuairbnb.domain;

import com.example.jejuairbnb.shared.domain.TimeStamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "rating")
    private Float rating = 0.0f;

    @Column(name = "description")
    private String description;

    @Column(name = "img")
    private String img;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY 로딩으로 변경
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}
