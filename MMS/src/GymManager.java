package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GymManager Class (The "Controller")
 * This class handles all the core application logic. It manages the list of
 * members and is responsible for all operations like adding, deleting,
 * finding, saving, and loading. The UI class will delegate all work to this class.
 */
public class GymManager {

    private List<Member> members = new ArrayList<>();

    // --- Core CRUD Operations ---

    public void addMember(Member member) {
        members.add(member);
    }

    public boolean deleteMember(String memberId) {
        return members.removeIf(member -> member.getMemberId().equalsIgnoreCase(memberId));
    }

    public Member findMemberById(String memberId) {
        return members.stream()
                .filter(member -> member.getMemberId().equalsIgnoreCase(memberId))
                .findFirst()
                .orElse(null);
    }

    public List<Member> getAllMembers() {
        return members;
    }

    // --- NEW: Advanced Search and Filter Methods ---

    /**
     * Finds all members whose full name contains the given search term (case-insensitive).
     * @param name The search term for the member's name.
     * @return A List of matching Member objects. Returns an empty list if none found.
     */
    public List<Member> findMembersByName(String name) {
        return members.stream()
                .filter(member -> member.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filters members by their type (Regular or Premium).
     * @param type The member type to filter by ("Regular" or "Premium").
     * @return A List of matching Member objects.
     */
    public List<Member> findMembersByType(String type) {
        return members.stream()
                .filter(member -> {
                    if (type.equalsIgnoreCase("Regular")) {
                        return member instanceof RegularMember;
                    } else if (type.equalsIgnoreCase("Premium")) {
                        return member instanceof PremiumMember;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Finds all members who have a performance record matching the specified criteria.
     * @param month The month of the performance record (1-12).
     * @param year The year of the performance record.
     * @param goalAchieved The status of the goal (true for achieved, false for not achieved).
     * @return A List of matching Member objects.
     */
    public List<Member> findMembersByPerformance(int month, int year, boolean goalAchieved) {
        return members.stream()
                .filter(member -> member.getPerformanceHistory().stream()
                        .anyMatch(perf -> perf.getMonth() == month && perf.getYear() == year && perf.wasGoalAchieved() == goalAchieved))
                .collect(Collectors.toList());
    }


    // --- File Handling ---

    public void loadFromFile(String filename) {
        members.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] mainParts = line.split("\\|");
                String[] memberDetails = mainParts[0].split(",");

                String id = memberDetails[0].trim();
                String name = memberDetails[1].trim();
                String type = memberDetails[2].trim();
                LocalDate joinDate = LocalDate.parse(memberDetails[3].trim());

                Member member = null;
                if (type.equalsIgnoreCase("Regular")) {
                    member = new RegularMember(id, name, joinDate);
                } else if (type.equalsIgnoreCase("Premium")) {
                    double trainerFee = Double.parseDouble(memberDetails[4].trim());
                    member = new PremiumMember(id, name, joinDate, trainerFee);
                }

                if (member != null) {
                    if (mainParts.length > 1) {
                        for (int i = 1; i < mainParts.length; i++) {
                            String[] perfParts = mainParts[i].split(":");
                            int month = Integer.parseInt(perfParts[0]);
                            int year = Integer.parseInt(perfParts[1]);
                            boolean goalAchieved = Boolean.parseBoolean(perfParts[2]);
                            member.addPerformanceRecord(new Performance(month, year, goalAchieved));
                        }
                    }
                    members.add(member);
                }
            }
            System.out.println("Successfully loaded " + members.size() + " members from " + filename);
        } catch (IOException e) {
            System.err.println("Error: File not found or cannot be read. " + e.getMessage());
        } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: Data in the file is corrupt or incorrectly formatted. " + e.getMessage());
        }
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Member member : members) {
                writer.write(member.toCsvString());
                writer.newLine();
            }
            System.out.println("Successfully saved " + members.size() + " members to " + filename);
        } catch (IOException e) {
            System.err.println("Error: Could not write to file. " + e.getMessage());
        }
    }
}
