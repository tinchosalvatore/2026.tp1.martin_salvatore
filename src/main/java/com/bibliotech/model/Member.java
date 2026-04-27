package com.bibliotech.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Student.class, name = "STUDENT"),
    @JsonSubTypes.Type(value = Teacher.class, name = "TEACHER")
})
public interface Member extends Identifiable<String> {
    String dni();
    String name();
    String email();
    int maxLoans();

    @Override
    default String id() {
        return dni();
    }
}
