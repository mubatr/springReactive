package com.learnreactivespring.learnreactivespring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// ItemCapped will be used to create a capped collection (fixed size collection) in MongoDB.
@Document // Equivalent to @Entity for Relational Databases.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped {

    @Id
    private String id;
    private String description;
    private Double price;
}
