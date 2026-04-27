package com.bibliotech;

import com.bibliotech.db.JsonDatabase;
import com.bibliotech.exception.LibraryException;
import com.bibliotech.model.*;
import com.bibliotech.repository.*;
import com.bibliotech.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static ResourceService resourceService;
    private static MemberService memberService;
    private static LoanService loanService;

    public static void main(String[] args) {
        init();
        showBanner();
        
        while (true) {
            System.out.println("\nSelect Role:");
            System.out.println("1. Librarian");
            System.out.println("2. Member (Student/Teacher)");
            System.out.println("0. Exit");
            System.out.print("> ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> librarianMenu();
                case "2" -> memberMenu();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void showBanner() {
        try {
            List<String> logoLines = Files.readAllLines(Path.of("logo.txt"));
            List<String> nameLines = Files.readAllLines(Path.of("name.txt"));

            int nameOffset = 3;
            String spacing = "%-90s";

            int maxLines = Math.max(logoLines.size(), nameLines.size() + nameOffset);

            for (int i = 0; i < maxLines; i++) {
                String name = "";

                if (i >= nameOffset && (i - nameOffset) < nameLines.size()) {
                    name = nameLines.get(i - nameOffset);
                }

                String logo = i < logoLines.size() ? logoLines.get(i) : "";

                System.out.printf(spacing + " %s%n", name, logo);
            }
        } catch (IOException e) {
            System.out.println("=== BiblioTech System ===");
        }
    }

    private static void init() {
        JsonDatabase db = new JsonDatabase();
        
        ResourceRepository resourceRepo = new JsonResourceRepository(db);
        MemberRepository memberRepo = new JsonMemberRepository(db);
        LoanRepository loanRepo = new JsonLoanRepository(db);
        SanctionRepository sanctionRepo = new JsonSanctionRepository(db);

        resourceService = new ResourceServiceImpl(resourceRepo);
        
        MemberValidator memberValidator = new MemberValidatorImpl();
        memberService = new MemberServiceImpl(memberRepo, memberValidator);
        
        SanctionService sanctionService = new SanctionServiceImpl(sanctionRepo);
        LoanTrackerService loanTracker = new LoanTrackerServiceImpl();
        LoanValidator loanValidator = new LoanValidatorImpl(sanctionService);
        
        loanService = new LoanServiceImpl(resourceService, memberService, loanRepo, loanValidator, loanTracker, sanctionService);
    }

    private static void librarianMenu() {
        while (true) {
            System.out.println("\n--- Librarian Menu ---");
            System.out.println("1. Register Resource");
            System.out.println("2. Register Member");
            System.out.println("3. Process Return");
            System.out.println("4. Search Resources");
            System.out.println("5. Loan History");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> registerResource();
                    case "2" -> registerMember();
                    case "3" -> processReturn();
                    case "4" -> searchResources();
                    case "5" -> showHistory();
                    case "0" -> { return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void memberMenu() {
        System.out.print("Enter your DNI: ");
        String dni = scanner.nextLine();
        if (memberService.findByDni(dni).isEmpty()) {
            System.out.println("Member not found.");
            return;
        }

        while (true) {
            System.out.println("\n--- Member Menu (" + dni + ") ---");
            System.out.println("1. Search Resources");
            System.out.println("2. Request Loan");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> searchResources();
                    case "2" -> requestLoan(dni);
                    case "0" -> { return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void registerResource() throws Exception {
        System.out.println("Type: 1. Physical Book, 2. E-Book");
        String type = scanner.nextLine();
        
        System.out.print("ISBN: "); String isbn = scanner.nextLine();
        System.out.print("Title: "); String title = scanner.nextLine();
        System.out.print("Author: "); String author = scanner.nextLine();
        System.out.print("Year: "); int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Category: 1. FICTION, 2. NON_FICTION, 3. SCIENCE, 4. TECHNOLOGY, 5. HISTORY, 6. ART");
        Category cat = Category.values()[Integer.parseInt(scanner.nextLine()) - 1];

        Resource resource;
        if (type.equals("1")) {
            System.out.print("Shelf Location: "); String loc = scanner.nextLine();
            resource = new PhysicalBook(isbn, title, author, year, cat, loc);
        } else {
            System.out.print("Format: "); String fmt = scanner.nextLine();
            System.out.print("Size (MB): "); double size = Double.parseDouble(scanner.nextLine());
            resource = new EBook(isbn, title, author, year, cat, fmt, size);
        }
        resourceService.registerResource(resource);
        System.out.println("Resource registered!");
    }

    private static void registerMember() throws Exception {
        System.out.println("Type: 1. Student, 2. Teacher");
        String type = scanner.nextLine();
        System.out.print("DNI: "); String dni = scanner.nextLine();
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();

        Member member = type.equals("1") ? new Student(dni, name, email) : new Teacher(dni, name, email);
        memberService.registerMember(member);
        System.out.println("Member registered!");
    }

    private static void processReturn() throws LibraryException {
        System.out.print("Enter Resource ISBN: ");
        String isbn = scanner.nextLine();
        long delay = loanService.returnResource(isbn);
        if (delay > 0) {
            System.out.println("Return processed with " + delay + " days of delay. Sanction applied.");
        } else {
            System.out.println("Return processed successfully!");
        }
    }

    private static void searchResources() {
        System.out.print("Title filter (empty for all): "); String title = scanner.nextLine();
        System.out.print("Author filter (empty for all): "); String author = scanner.nextLine();
        
        SearchCriteria criteria = new SearchCriteria(
            title.isEmpty() ? null : title,
            author.isEmpty() ? null : author,
            null, null
        );
        
        List<Resource> results = resourceService.search(criteria);
        if (results.isEmpty()) {
            System.out.println("No resources found.");
        } else {
            results.forEach(r -> System.out.println("[" + r.id() + "] " + r.title() + " by " + r.author()));
        }
    }

    private static void requestLoan(String dni) throws LibraryException {
        System.out.print("Enter Resource ISBN: ");
        String isbn = scanner.nextLine();
        Loan loan = loanService.registerLoan(isbn, dni);
        System.out.println("Loan registered! Due date: " + loan.dueDate());
    }

    private static void showHistory() {
        List<Loan> history = loanService.getHistory();
        if (history.isEmpty()) {
            System.out.println("No loan history.");
        } else {
            history.forEach(l -> System.out.println("DNI: " + l.memberDni() + " | ISBN: " + l.isbn() + " | Due: " + l.dueDate() + " | Returned: " + l.returnDate().orElse(null)));
        }
    }
}
