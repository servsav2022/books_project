package com.servsav.books_project.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pricebooks")
public class PriceBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "price")
    private Double price;

    @OneToOne
    @JoinColumn(name = "pricebook_id", referencedColumnName = "id") // Имя столбца, который будет хранить внешний ключ
    private Book book;

}