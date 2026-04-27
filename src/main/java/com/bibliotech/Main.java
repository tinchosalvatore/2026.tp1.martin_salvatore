package com.bibliotech;

import com.bibliotech.db.JsonDatabase;
import com.bibliotech.model.Category;
import com.bibliotech.model.PhysicalBook;
import com.bibliotech.model.Resource;
import com.bibliotech.repository.JsonResourceRepository;
import com.bibliotech.repository.ResourceRepository;
import com.bibliotech.service.ResourceService;
import com.bibliotech.service.ResourceServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("BiblioTech System Initialized with JSON Persistence");

        // 1. Initialize Database
        JsonDatabase db = new JsonDatabase();

        // 2. Initialize Repositories
        ResourceRepository resourceRepo = new JsonResourceRepository(db);

        // 3. Initialize Services
        ResourceService resourceService = new ResourceServiceImpl(resourceRepo);

        // 4. Test Persistence
        try {
            List<Resource> existing = resourceService.search(new com.bibliotech.model.SearchCriteria(null, null, null, null));
            System.out.println("Current resources in DB: " + existing.size());

            if (existing.isEmpty()) {
                System.out.println("Adding test book...");
                Resource book = new PhysicalBook(
                        "978-3-16-148410-0",
                        "The Pragmatic Programmer",
                        "Andrew Hunt",
                        1999,
                        Category.TECHNOLOGY,
                        "A-123"
                );
                resourceService.registerResource(book);
                System.out.println("Book added and saved to JSON.");
            } else {
                System.out.println("First book found: " + existing.get(0).title());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
