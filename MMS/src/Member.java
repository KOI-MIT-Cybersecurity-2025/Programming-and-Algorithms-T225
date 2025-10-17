package src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Member Class (Abstract "Model")
 * This is the base class for all member types. It defines the common
 * properties and behaviors that all members will share.
 * It's abstract, meaning you cannot create a generic "Member"; you must
 * create a specific type like RegularMember or PremiumMember.
 */
public abstract class Member {

    // --- ATTRIBUTES ---
    protected String memberId;
    protected String fullName;
    protected LocalDate joinDate;
    // This list will hold the history of performance records for the member.
    protected List<Performance> performanceHistory;

    // --- CONSTRUCTOR ---
    public Member(String memberId, String fullName, LocalDate joinDate) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.joinDate = joinDate;
        // Initialize the performance history list.
        this.performanceHistory = new ArrayList<>();
    }

    // --- ABSTRACT METHODS ---
    // This method must be implemented by all concrete subclasses (RegularMember,
    // PremiumMember).
    public abstract double calculateMonthlyFee();

    // This method converts the member's data to a CSV-formatted string for saving.
    public abstract String toCsvString();

    // --- CONCRETE METHODS ---

    /**
     * Adds a new performance record to this member's history.
     * This is the method that resolves the compilation error.
     * 
     * @param record The Performance object to add.
     */
    public void addPerformanceRecord(Performance record) {
        this.performanceHistory.add(record);
    }

    // --- GETTERS AND SETTERS ---
    public String getMemberId() {
        return memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public List<Performance> getPerformanceHistory() {
        return performanceHistory;
    }

    /**
     * Overrides the default toString method to provide a detailed, readable
     * description of the member, including their performance history.
     */
    @Override
    public String toString() {
        // Use a StringBuilder for efficient string concatenation.
        StringBuilder sb = new StringBuilder();
        // Add performance history to the output string.
        if (!performanceHistory.isEmpty()) {
            sb.append("\n  Performance History:");
            for (Performance p : performanceHistory) {
                sb.append("\n    - ").append(p.toString());
            }
        } else {
            sb.append("\n  Performance History: No records found.");
        }
        return sb.toString();
    }
}
