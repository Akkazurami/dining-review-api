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
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="DISPLAY_NAME")
    private String displayName;

    @Column(name="CITY")
    private String city;

    @Column(name="STATE")
    private String state;

    @Column(name="ZIP_CODE")
    private String zipCode;

    @Column(name="PEANUT_ALLERGY")
    private Boolean peanutAllergy;

    @Column(name="EGG_ALLERGY")
    private Boolean eggAllergy;

    @Column(name="DAIRY_ALLERGY")
    private Boolean dairyAllergy;
}
