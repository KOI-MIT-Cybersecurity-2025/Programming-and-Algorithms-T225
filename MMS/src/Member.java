package src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Member Class (Abstract "Model")
 * Represents the core properties and behaviors common to all gym members.
 * UPDATED: Now includes MembershipStatus.
 */
public abstract class Member {

    protected String memberId;
    protected String fullName;
    protected LocalDate joinDate;
    protected List<Performance> performanceHistory;
    protected MembershipStatus status; // ADDED: Member's current status

    public Member(String memberId, String fullName, LocalDate joinDate) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.joinDate = joinDate;
        this.performanceHistory = new ArrayList<>();
        this.status = MembershipStatus.ACTIVE; // Default to ACTIVE
    }

    // Getters
    public String getMemberId() {
        return memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public List<Performance> getPerformanceHistory() {
        return performanceHistory;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    // Public methods
    public void addPerformanceRecord(Performance record) {
        this.performanceHistory.add(record);
    }

    // Abstract methods to be implemented by subclasses
    public abstract double calculateMonthlyFee();

    public abstract String toCsvString();

    public abstract String getMemberType(); // ADDED: Forces subclasses to identify their type

    /**
     * UPDATED: Provides a single, complete multi-line representation for any member
     * type, removing duplication.
     */
    @Override
    public String toString() {
        String performanceInfo = performanceHistory.isEmpty()
                ? "None"
                : performanceHistory.size() + " record(s)";

        return String.format(
                "Member Type: %s\nMember ID: %s\nName: %s\nJoined: %s\nMonthly Fee: $%.2f\nStatus: %s\nPerformance: %s",
                getMemberType(), // Calls the new abstract method
                memberId,
                fullName,
                joinDate,
                calculateMonthlyFee(),
                status,
                performanceInfo);
    }
}

