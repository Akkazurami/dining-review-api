package com.example.DiningReview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="REVIEW")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="DISPLAY_NAME")
    private String displayName;

    @Column(name="RESTAURANT_ID")
    private Integer restaurantId;

    @Column(name="PEANUT_SCORE")
    private Double peanutScore;

    @Column(name="EGG_SCORE")
    private Double eggScore;

    @Column(name="DAIRY_SCORE")
    private Double dairyScore;

    @Column(name="COMMENT")
    private String comment;

    @Column(name="REVIEW_STATUS")
    @Enumerated(value = EnumType.STRING)
    private ReviewStatus reviewStatus;
}
