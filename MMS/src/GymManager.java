package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GymManager Class (The "Controller")
 * This class handles all the business logic for managing members.
 * It acts as an intermediary between the UI (View) and the data (Model).
 */
public class GymManager {
    // The main data structure for holding all member objects.
    // We use 'List<Member>' to allow for polymorphism.
    // We instantiate it as 'ArrayList' as justified in our design phase.
    private List<Member> members;

    public GymManager() {
        this.members = new ArrayList<>();
    }

    /**
     * Adds a new member to the system.
     * @param member The Member object to add (can be RegularMember or PremiumMember).
     */
    public void addMember(Member member) {
        // You could add a check here to prevent duplicate member IDs.
        members.add(member);
    }

    /**
     * Finds a member by their unique ID.
     * @param memberId The ID to search for.
     * @return The Member object if found, otherwise null.
     */
    public Member findMemberById(String memberId) {
        // Using Java Streams for a modern and concise search.
        return members.stream()
                      .filter(m -> m.getMemberId().equalsIgnoreCase(memberId))
                      .findFirst()
                      .orElse(null);
    }

    /**
     * Deletes a member from the system using their ID.
     * @param memberId The ID of the member to delete.
     * @return true if a member was deleted, false otherwise.
     */
    public boolean deleteMember(String memberId) {
        return members.removeIf(member -> member.getMemberId().equalsIgnoreCase(memberId));
    }

    /**
     * Returns the entire list of members.
     * @return A List containing all members.
     */
    public List<Member> getAllMembers() {
        return members;
    }

    /**
     * Loads member data from a specified CSV file.
     * This method demonstrates file input and exception handling.
     * @param filename The path to the CSV file (e.g., "member_data.csv").
     */
    public void loadFromFile(String filename) {
        members.clear(); // Clear current list before loading from file
        // Using a "try-with-resources" block to automatically close the reader.
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // Skip malformed lines

                String id = parts[0].trim();
                String name = parts[1].trim();
                String type = parts[2].trim();
                LocalDate joinDate = LocalDate.parse(parts[3].trim());

                if (type.equalsIgnoreCase("Regular")) {
                    members.add(new RegularMember(id, name, joinDate));
                } else if (type.equalsIgnoreCase("Premium") && parts.length >= 5) {
                    double trainerFee = Double.parseDouble(parts[4].trim());
                    members.add(new PremiumMember(id, name, joinDate, trainerFee));
                }
            }
            System.out.println("Successfully loaded " + members.size() + " members from " + filename);
        } catch (IOException e) {
            System.err.println("Error: File not found or cannot be read. " + e.getMessage());
        } catch (DateTimeParseException | NumberFormatException e) {
            System.err.println("Error: Data in the file is corrupt or incorrectly formatted. " + e.getMessage());
        }
    }

    /**
     * Saves the current list of members to a specified CSV file.
     * This method demonstrates file output.
     * @param filename The path to the file where data will be saved.
     */
    public void saveToFile(String filename) {
        // Using "try-with-resources" to automatically close the writer.
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Member member : members) {
                // This part demonstrates polymorphism in file handling.
                // We check the actual class of the member object to save its specific data.
                if (member instanceof RegularMember) {
                    writer.println(String.format("%s,%s,Regular,%s",
                        member.getMemberId(), member.getFullName(), member.getJoinDate()));
                } else if (member instanceof PremiumMember) {
                    // Cast the member to PremiumMember to access the getPersonalTrainerFee method.
                    PremiumMember premium = (PremiumMember) member;
                    writer.println(String.format("%s,%s,Premium,%s,%.2f",
                        member.getMemberId(), member.getFullName(), member.getJoinDate(), premium.getPersonalTrainerFee()));
                }
            }
            System.out.println("Successfully saved " + members.size() + " members to " + filename);
        } catch (IOException e) {
            System.err.println("Error: Could not write to file. " + e.getMessage());
        }
    }
}
