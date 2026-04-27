package com.bibliotech.db;

import com.bibliotech.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabase {
    private static final String FILE_PATH = "src/main/java/com/bibliotech/db/database.json";
    private final ObjectMapper mapper;
    private DatabaseContent content;

    public JsonDatabase() {
        this.mapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        load();
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            try {
                this.content = mapper.readValue(file, DatabaseContent.class);
            } catch (IOException e) {
                System.err.println("Error loading database: " + e.getMessage());
                this.content = createEmptyContent();
            }
        } else {
            this.content = createEmptyContent();
        }
    }

    private DatabaseContent createEmptyContent() {
        return new DatabaseContent(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public synchronized void save() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), content);
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    public List<Resource> getResources() { return content.resources(); }
    public List<Member> getMembers() { return content.members(); }
    public List<Loan> getLoans() { return content.loans(); }
    public List<Sanction> getSanctions() { return content.sanctions(); }

    private record DatabaseContent(
            List<Resource> resources,
            List<Member> members,
            List<Loan> loans,
            List<Sanction> sanctions
    ) {}
}
