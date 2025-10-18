package src;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GymApplication {

    private static GymManager manager = new GymManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // --- UPDATED: Automatically load the new default data file ---
        System.out.println("Welcome to the Member Management System.");
        System.out.println("Attempting to load default data from gym_records.csv...");
        manager.loadFromFile("gym_records.csv");
        // --- END UPDATE ---

        int choice = 0;
        while (choice != 9) {
            displayMainMenu();
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                processMainMenuChoice(choice);
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n===== Member Management System =====");
        System.out.println("1. Load another data file");
        System.out.println("2. View all members");
        System.out.println("3. Add a new member");
        System.out.println("4. Update a member's status (Freeze/Activate)");
        System.out.println("5. Add a performance record");
        System.out.println("6. Delete a member");
        System.out.println("7. Search / Filter Members...");
        System.out.println("8. Save records to a new file");
        System.out.println("9. Exit and Save"); 
        System.out.println("====================================");
        System.out.print("Please choose an option: ");
    }

    private static void processMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleLoadFile();
                break;
            case 2:
                handleViewAllMembers();
                break;
            case 3:
                handleAddMember();
                break;
            case 4:
                handleUpdateStatus();
                break;
            case 5:
                handleAddPerformanceRecord();
                break;
            case 6:
                handleDeleteMember();
                break;
            case 7:
                handleSearchMenu();
                break;
            case 8:
                handleSaveToFile();
                break;
            case 9:
                manager.saveToFile("gym_records.csv");
                System.out.println("Exiting... Data saved to gym_records.csv.");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Handles updating a member's status to ACTIVE or FROZEN.
     */
    private static void handleUpdateStatus() {
        System.out.print("Enter the Member ID to update status: ");
        String memberId = scanner.nextLine();
        Member member = manager.findMemberById(memberId);

        if (member == null) {
            System.err.println("Member not found.");
            return;
        }
        System.out.println("Current status for " + member.getFullName() + " is: " + member.getStatus());
        System.out.print("Enter new status (ACTIVE/FROZEN): ");
        String statusInput = scanner.nextLine().toUpperCase();
        try {
            MembershipStatus newStatus = MembershipStatus.valueOf(statusInput);
            member.setStatus(newStatus);
            System.out.println("Status updated successfully!");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status. Please enter 'ACTIVE' or 'FROZEN'.");
        }
    }

    private static void handleLoadFile() {
        System.out.print("Enter filename to load (e.g., members_backup.csv): ");
        String filename = scanner.nextLine();
        manager.loadFromFile(filename);
    }

    private static void handleViewAllMembers() {
        List<Member> members = manager.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("There are no members in the system.");
        } else {
            System.out.println("\n--- All Members ---");
            members.forEach(member -> {
                System.out.println(member);
                System.out.println("-------------------");
            });
        }
    }

    private static void handleAddMember() {
        System.out.print("Enter Member Type (Regular/Premium): ");
        String type = scanner.nextLine();
        if (!type.equalsIgnoreCase("Regular") && !type.equalsIgnoreCase("Premium")) {
            System.err.println("Invalid member type. Please enter 'Regular' or 'Premium'.");
            return;
        }
        System.out.print("Enter Member ID (e.g., M011): ");
        String id = scanner.nextLine();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        Member member;
        if (type.equalsIgnoreCase("Regular")) {
            member = new RegularMember(id, name, LocalDate.now());
            manager.addMember(member);
            System.out.println("Regular member added successfully!");
        } else { // Premium
            try {
                System.out.print("Enter Personal Trainer Fee: ");
                double fee = scanner.nextDouble();
                scanner.nextLine();
                member = new PremiumMember(id, name, LocalDate.now(), fee);
                manager.addMember(member);
                System.out.println("Premium member added successfully!");
            } catch (InputMismatchException e) {
                System.err.println("Invalid fee. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleAddPerformanceRecord() {
        System.out.print("Enter Member ID for performance record: ");
        String memberId = scanner.nextLine();
        Member member = manager.findMemberById(memberId);

        if (member == null) {
            System.err.println("Member not found.");
            return;
        }

        try {
            System.out.print("Enter performance month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Enter performance year: ");
            int year = scanner.nextInt();
            System.out.print("Was the monthly goal achieved? (true/false): ");
            boolean achieved = scanner.nextBoolean();
            scanner.nextLine();

            member.addPerformanceRecord(new Performance(month, year, achieved));
            System.out.println("Performance record added for " + member.getFullName());

        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter the correct data type.");
            scanner.nextLine();
        }
    }

    private static void handleDeleteMember() {
        System.out.print("Enter Member ID to delete: ");
        String memberId = scanner.nextLine();
        if (manager.deleteMember(memberId)) {
            System.out.println("Member deleted successfully.");
        } else {
            System.err.println("Member not found.");
        }
    }

    private static void handleSaveToFile() {
        System.out.print("Enter filename to save to (e.g., members_backup.csv): ");
        String filename = scanner.nextLine();
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Filename cannot be empty. Save cancelled.");
            return;
        }
        manager.saveToFile(filename);
    }

    private static void handleSearchMenu() {
        int choice = 0;
        while (choice != 4) {
            System.out.println("\n--- Search & Filter Menu ---");
            System.out.println("1. Search by Name");
            System.out.println("2. Filter by Member Type");
            System.out.println("3. Filter by Performance");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        handleSearchByName();
                        break;
                    case 2:
                        handleFilterByType();
                        break;
                    case 3:
                        handleFilterByPerformance();
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleSearchByName() {
        System.out.print("Enter name to search for: ");
        String name = scanner.nextLine();
        displaySearchResults(manager.findMembersByName(name), "Search Results for '" + name + "'");
    }

    private static void handleFilterByType() {
        System.out.print("Enter type to filter (Regular/Premium): ");
        String type = scanner.nextLine();
        displaySearchResults(manager.filterMembersByType(type), "Filter Results for Type: " + type);
    }

    private static void handleFilterByPerformance() {
        try {
            System.out.print("Enter month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Enter year: ");
            int year = scanner.nextInt();
            System.out.print("Filter by goal achieved? (true/false): ");
            boolean achieved = scanner.nextBoolean();
            scanner.nextLine();

            String status = achieved ? "Achieved Goal" : "Did Not Achieve Goal";
            displaySearchResults(manager.findMembersByPerformance(month, year, achieved),
                    "Filter Results for Performance: " + status + " in " + month + "/" + year);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input.");
            scanner.nextLine();
        }
    }

    private static void displaySearchResults(List<Member> results, String header) {
        System.out.println("\n--- " + header + " ---");
        if (results.isEmpty()) {
            System.out.println("No members found matching your criteria.");
        } else {
            results.forEach(member -> {
                System.out.println(member);
                System.out.println("-------------------");
            });
        }
    }
}
