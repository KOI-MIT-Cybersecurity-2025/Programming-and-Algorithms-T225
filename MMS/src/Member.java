package src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Member Abstract Class (The "Model" Foundation)
 * UPDATED: Now includes MembershipStatus and methods to manage performance history.
 * This is the parent class for all member types.
 */
public abstract class Member {

    protected String memberId;
    protected String fullName;
    protected LocalDate joinDate;
    protected MembershipStatus status; // The new status field
    protected List<Performance> performanceHistory;

    public Member(String memberId, String fullName, LocalDate joinDate) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.joinDate = joinDate;
        this.status = MembershipStatus.ACTIVE; // Default status for new members
        this.performanceHistory = new ArrayList<>();
    }

    // --- Abstract Methods (must be implemented by subclasses) ---

    public abstract double calculateMonthlyFee();
    public abstract String toCsvString();


    // --- Getters and Setters ---

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
    
    public MembershipStatus getStatus() {
        return status;
    }
    
    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    // --- Performance History Methods ---

    public List<Performance> getPerformanceHistory() {
        return performanceHistory;
    }
    
    public void setPerformanceHistory(List<Performance> history) {
        this.performanceHistory = history;
    }

    public void addPerformanceRecord(Performance record) {
        this.performanceHistory.add(record);
    }
    
    /**
     * Provides a base toString() method that subclasses can append to.
     * This part includes the status and a summary of performance history.
     */
    @Override
    public String toString() {
        String performanceSummary = performanceHistory.isEmpty() ? "\n  Performance History: None"
            : "\n  Performance History: " + performanceHistory.size() + " record(s).";
        
        return String.format("\n  Status: %s", this.status) + performanceSummary;
    }
}

