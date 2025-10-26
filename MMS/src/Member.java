package src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Member Class (The "Model")
 * Serves as the base for all member types.
 *
 * INDIVIDUAL PROJECT UPDATES:
 * - Implements Comparable<Member> to allow for natural sorting by Member ID.
 * - Centralizes the logic for toString() and getMemberType().
 */
public abstract class Member implements Comparable<Member> {

    protected String memberId;
    protected String fullName;
    protected LocalDate joinDate;
    protected MembershipStatus status;
    protected List<Performance> performanceHistory;

    public Member(String memberId, String fullName, LocalDate joinDate) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be null or empty.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }
        this.memberId = memberId;
        this.fullName = fullName;
        this.joinDate = joinDate;
        this.status = MembershipStatus.ACTIVE; // Default status
        this.performanceHistory = new ArrayList<>();
    }

    // --- Abstract Methods (Must be implemented by subclasses) ---

    public abstract double calculateMonthlyFee();

    public abstract String toCsvString();
    
    public abstract String getMemberType();

    // --- Concrete Methods (Inherited by all subclasses) ---

    /**
     * NEW: Implementation of the Comparable interface.
     * Allows sorting members by their ID (natural order).
     * @param other The other member to compare against.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Member other) {
        return this.memberId.compareTo(other.memberId);
    }

    /**
     * Provides a detailed, multi-line string representation of the member.
     * This is used for display in the text-based UI.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s Member] Member ID: %s\n", getMemberType(), memberId));
        sb.append(String.format("  Name: %s\n", fullName));
        sb.append(String.format("  Joined: %s\n", joinDate));
        sb.append(String.format("  Status: %s\n", status));
        sb.append(String.format("  Monthly Fee: $%.2f\n", calculateMonthlyFee()));

        if (performanceHistory.isEmpty()) {
            sb.append("  Performance: None");
        } else {
            sb.append(String.format("  Performance: %d record(s)", performanceHistory.size()));
        }
        return sb.toString();
    }

    // --- Getters and Setters ---

    public String getMemberId() {
        return memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            this.fullName = fullName;
        }
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public List<Performance> getPerformanceHistory() {
        return performanceHistory;
    }

    public void addPerformanceRecord(Performance record) {
        // Optional: Add logic to prevent duplicate month/year entries
        this.performanceHistory.add(record);
    }
}

