package com.example.DiningReview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="RESTAURANT")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="ZIP_CODE")
    private String zipCode;

    @Column(name="PEANUT_SCORE")
    private Double peanutScore;

    @Column(name="EGG_SCORE")
    private Double eggScore;
    
    @Column(name="DAIRY_SCORE")
    private Double dairyScore;

    @Column(name="REVIEW_SCORE")
    private Double reviewScore;

}
