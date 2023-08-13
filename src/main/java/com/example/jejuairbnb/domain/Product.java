package com.example.jejuairbnb.domain;

import com.example.jejuairbnb.shared.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "position")
    private String position;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private int price;

    @Column(name = "img")
    private String img;

    private Long commentCount;

    private Double commentAvg;

    private Long reservationCount;

    private Double commentMax;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(
            mappedBy = "product",
            fetch=FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    private List<Comment> comment = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            fetch=FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    private List<Reservation> reservations = new ArrayList<>();
}
