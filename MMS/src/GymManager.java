package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GymManager Class (The "Controller")
 * This class handles all the core application logic.
 * UPDATED: Includes advanced search and filter methods.
 */
public class GymManager {

    private List<Member> members;

    public GymManager() {
        this.members = new ArrayList<>();
    }

    // --- Core Member Management ---

    public void addMember(Member member) {
        this.members.add(member);
    }

    public Member findMemberById(String memberId) {
        return members.stream()
                .filter(m -> m.getMemberId().equalsIgnoreCase(memberId))
                .findFirst()
                .orElse(null);
    }

    public boolean deleteMember(String memberId) {
        return members.removeIf(m -> m.getMemberId().equalsIgnoreCase(memberId));
    }

    public List<Member> getAllMembers() {
        return this.members;
    }

    // --- Advanced Search and Filter Methods (NEW) ---

    public List<Member> findMembersByName(String name) {
        return members.stream()
                .filter(m -> m.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Member> filterMembersByType(String type) {
        if (type.equalsIgnoreCase("Regular")) {
            return members.stream()
                    .filter(m -> m instanceof RegularMember)
                    .collect(Collectors.toList());
        } else if (type.equalsIgnoreCase("Premium")) {
            return members.stream()
                    .filter(m -> m instanceof PremiumMember)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(); // Return empty list for invalid type
    }

    public List<Member> findMembersByPerformance(int month, int year, boolean goalAchieved) {
        return members.stream()
                .filter(m -> m.getPerformanceHistory().stream()
                        .anyMatch(p -> p.getMonth() == month && p.getYear() == year && p.wasGoalAchieved() == goalAchieved))
                .collect(Collectors.toList());
    }


    // --- File Handling ---

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

    /**
     * UPDATED: This method is now backward-compatible. It can load CSV files
     * with or without the MembershipStatus field.
     */
    public void loadFromFile(String filename) {
        members.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                String id = parts[0].trim();
                String name = parts[1].trim();
                String type = parts[2].trim();
                LocalDate joinDate = LocalDate.parse(parts[3].trim());

                Member member;

                if (type.equalsIgnoreCase("Regular")) {
                    member = new RegularMember(id, name, joinDate);
                    // Check if status and performance data is present (new format)
                    if (parts.length > 4) {
                        member.setStatus(MembershipStatus.valueOf(parts[4].trim().toUpperCase()));
                        if (parts.length > 5) {
                            parseAndAddPerformance(member, parts[5]);
                        }
                    }
                } else { // Premium
                    // Old format has fee at index 4. New format has status at 4 and fee at 5.
                    double trainerFee;
                    if (parts.length > 5) { // This indicates the new format with status
                        trainerFee = Double.parseDouble(parts[5].trim());
                        member = new PremiumMember(id, name, joinDate, trainerFee);
                        member.setStatus(MembershipStatus.valueOf(parts[4].trim().toUpperCase()));
                        if (parts.length > 6) {
                            parseAndAddPerformance(member, parts[6]);
                        }
                    } else { // Assume old format without status
                        trainerFee = Double.parseDouble(parts[4].trim());
                        member = new PremiumMember(id, name, joinDate, trainerFee);
                        // Status defaults to ACTIVE in the constructor, which is correct for old data
                    }
                }
                members.add(member);
            }
            System.out.println("Successfully loaded " + members.size() + " members from " + filename);
        } catch (IOException e) {
            System.err.println("Error: File not found or cannot be read. " + e.getMessage());
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            System.err.println("Error: Data in the file is corrupt or incorrectly formatted. " + e.getMessage());
        }
    }

    /**
     * A private helper method to parse performance data from a string and add it to a member.
     * This avoids code duplication in the loadFromFile method.
     */
    private void parseAndAddPerformance(Member member, String performanceData) {
        String[] performanceRecords = performanceData.split("\\|");
        for (String recordStr : performanceRecords) {
            if(recordStr.trim().isEmpty()) continue;
            String[] recordParts = recordStr.split(";");
            int month = Integer.parseInt(recordParts[0]);
            int year = Integer.parseInt(recordParts[1]);
            boolean achieved = Boolean.parseBoolean(recordParts[2]);
            member.addPerformanceRecord(new Performance(month, year, achieved));
        }
    }
}

