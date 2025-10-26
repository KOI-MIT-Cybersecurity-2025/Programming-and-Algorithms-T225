package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap; // NEW: For efficient searching
import java.util.List;
import java.util.Map; // NEW: For efficient searching
import java.util.stream.Collectors;

/**
 * Controller Class ("The Engine")
 * Manages all member data and business logic.
 *
 * INDIVIDUAL PROJECT UPDATES:
 * - Uses a HashMap (memberMap) for O(1) searching by ID.
 * - Provides sorting methods using Comparators.
 */
public class GymManager {

    // Main list for storing members. Used for sequential access, iteration, sorting.
    private List<Member> memberList;
    
    // NEW: HashMap for O(1) access by ID.
    // This is a major performance improvement for searching.
    private Map<String, Member> memberMap;

    public GymManager() {
        this.memberList = new ArrayList<>();
        this.memberMap = new HashMap<>();
    }

    /**
     * Adds a new member to both the list and the map.
     * Ensures data is consistent in both structures.
     */
    public void addMember(Member member) {
        if (member != null && !memberMap.containsKey(member.getMemberId())) {
            memberList.add(member);
            memberMap.put(member.getMemberId(), member);
        }
    }

    /**
     * Deletes a member from both the list and the map.
     * @param memberId The ID of the member to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteMember(String memberId) {
        Member memberToRemove = memberMap.get(memberId);
        if (memberToRemove != null) {
            memberList.remove(memberToRemove);
            memberMap.remove(memberId);
            return true;
        }
        return false;
    }

    /**
     * NEW: Efficiently finds a member by ID using the HashMap.
     * This is an O(1) (constant time) operation.
     * * @param memberId The ID of the member to find.
     * @return The Member object, or null if not found.
     */
    public Member findMemberById(String memberId) {
        return memberMap.get(memberId); // Much faster than iterating the list!
    }

    /**
     * Finds members by name using a linear search on the list.
     * This is an O(n) operation, as it must check every member.
     * Uses Java Streams for a clean implementation.
     *
     * @param name The search term (case-insensitive).
     * @return A list of matching Member objects.
     */
    public List<Member> findMembersByName(String name) {
        String lowerCaseName = name.toLowerCase();
        return memberList.stream()
                .filter(member -> member.getFullName().toLowerCase().contains(lowerCaseName))
                .collect(Collectors.toList());
    }

    /**
     * NEW: Sorts the list of members by their natural order (Member ID).
     * This uses the `compareTo` method defined in the Member class.
     * The algorithm is Timsort (O(n log n)).
     */
    public void sortMembersById() {
        Collections.sort(memberList);
    }

    /**
     * NEW: Sorts the list of members by name.
     * This uses the external MemberNameComparator class.
     * The algorithm is Timsort (O(n log n)).
     */
    public void sortMembersByName() {
        memberList.sort(new MemberNameComparator());
    }
    
    /**
     * NEW: Sorts the list of members by their join date.
     * This uses the external MemberJoinDateComparator class.
     * The algorithm is Timsort (O(n log n)).
     */
    public void sortMembersByJoinDate() {
        memberList.sort(new MemberJoinDateComparator());
    }


    /**
     * Filters members by type (Regular or Premium).
     * @param type The string "Regular" or "Premium".
     * @return A list of matching members.
     */
    public List<Member> filterMembersByType(String type) {
        Class<?> targetClass = type.equalsIgnoreCase("Regular") ? RegularMember.class : PremiumMember.class;
        return memberList.stream()
                .filter(member -> targetClass.isInstance(member))
                .collect(Collectors.toList());
    }

    /**
     * Filters members by their performance in a specific month/year.
     * @param month The month (1-12).
     * @param year The year.
     * @param achievedGoal Whether the goal was achieved.
     * @return A list of matching members.
     */
    public List<Member> findMembersByPerformance(int month, int year, boolean achievedGoal) {
        return memberList.stream()
                .filter(member -> member.getPerformanceHistory().stream()
                        .anyMatch(p -> p.getMonth() == month && p.getYear() == year
                                && p.wasGoalAchieved() == achievedGoal))
                .collect(Collectors.toList());
    }

    /**
     * Returns an unmodifiable view of the member list.
     * @return A list of all members.
     */
    public List<Member> getAllMembers() {
        return Collections.unmodifiableList(memberList);
    }

    /**
     * Saves the current member list to a CSV file.
     * @param filename The name of the file to save to.
     */
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Member member : memberList) {
                writer.write(member.toCsvString());
                writer.newLine();
            }
            System.out.println("Successfully saved " + memberList.size() + " members to " + filename);
        } catch (IOException e) {
            System.err.println("Error: Could not write to file. " + e.getMessage());
        }
    }

    /**
     * Loads member data from a CSV file.
     * UPDATED: Now populates both the list and the map for consistency.
     * This "smart" loader can handle both old (pre-status) and new data formats.
     */
    public void loadFromFile(String filename) {
        // Clear current data structures before loading
        memberList.clear();
        memberMap.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // Skip malformed lines

                String id = parts[0].trim();
                String name = parts[1].trim();
                String type = parts[2].trim();
                LocalDate joinDate = LocalDate.parse(parts[3].trim());
                
                // --- Smart Loader Logic ---
                // Defaults for optional fields
                MembershipStatus status = MembershipStatus.ACTIVE;
                double trainerFee = 0.0;
                String performanceString = null;
                
                if (type.equalsIgnoreCase("Regular")) {
                    // Format: ID,Name,Type,Date,[Status],[Performance]
                    if (parts.length >= 5) status = MembershipStatus.valueOf(parts[4].trim().toUpperCase());
                    if (parts.length >= 6) performanceString = parts[5].trim();
                    
                    RegularMember member = new RegularMember(id, name, joinDate);
                    member.setStatus(status);
                    if (performanceString != null) parsePerformanceString(member, performanceString);
                    addMember(member); // Use addMember to update both list and map

                } else if (type.equalsIgnoreCase("Premium")) {
                    // Format: ID,Name,Type,Date,[Status],[Fee],[Performance]
                    if (parts.length >= 5) status = MembershipStatus.valueOf(parts[4].trim().toUpperCase());
                    if (parts.length >= 6) trainerFee = Double.parseDouble(parts[5].trim());
                    if (parts.length >= 7) performanceString = parts[6].trim();
                    
                    PremiumMember member = new PremiumMember(id, name, joinDate, trainerFee);
                    member.setStatus(status);
                    if (performanceString != null) parsePerformanceString(member, performanceString);
                    addMember(member); // Use addMember to update both list and map
                }
            }
            System.out.println("Successfully loaded " + memberList.size() + " members from " + filename);
        } catch (IOException e) {
            System.err.println("Error: File not found or cannot be read. " + e.getMessage());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.err.println("Error: Data in the file is corrupt or incorrectly formatted. " + e.getMessage());
        }
    }

    /**
     * Helper method to parse the performance string (e.g., "10;2025;true|11;2025;false")
     */
    private void parsePerformanceString(Member member, String performanceString) {
        if (performanceString == null || performanceString.isEmpty()) {
            return;
        }
        try {
            String[] records = performanceString.split("\\|");
            for (String record : records) {
                String[] parts = record.split(";");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt(parts[1]);
                boolean achieved = Boolean.parseBoolean(parts[2]);
                member.addPerformanceRecord(new Performance(month, year, achieved));
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not parse performance data for member " + member.getMemberId());
        }
    }
}

