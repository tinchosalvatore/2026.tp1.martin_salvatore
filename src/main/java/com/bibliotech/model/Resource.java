package com.bibliotech.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PhysicalBook.class, name = "PHYSICAL"),
    @JsonSubTypes.Type(value = EBook.class, name = "EBOOK")
})
public interface Resource extends Identifiable<String> {
    String isbn();
    String title();
    String author();
    int year();
    Category category();

    @Override
    default String id() {
        return isbn();
    }
}
