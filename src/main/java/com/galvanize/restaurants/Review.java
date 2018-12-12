package com.galvanize.restaurants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
@Entity
final class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_generator")
    @SequenceGenerator(name = "review_generator", sequenceName = "review_sequence")
    private final Long id;
    private String comment;
    @JsonCreator
    public Review(@JsonProperty("id")Long id, @JsonProperty("comment")String comment) {
        this.comment=comment;

        this.id=id;
    }
    public Review(){
        this(Long.MIN_VALUE, null);
    }



    public Long getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }
}
