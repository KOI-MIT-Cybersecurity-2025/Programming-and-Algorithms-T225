package src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Member Class (The "Model")
 * This is the blueprint for all member types in the gym.
 * It defines common attributes and behaviors.
 * The 'abstract' keyword means you cannot create a direct instance of Member.
 */
public abstract class Member {
    // Attributes are 'protected' so they are accessible by subclasses.
    protected String memberId;
    protected String fullName;
    protected LocalDate joinDate;
    protected List<Performance> performanceHistory;

    /**
     * Constructor for the Member class.
     * @param memberId A unique identifier for the member.
     * @param fullName The full name of the member.
     * @param joinDate The date the member joined the gym.
     */
    public Member(String memberId, String fullName, LocalDate joinDate) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.joinDate = joinDate;
        this.performanceHistory = new ArrayList<>(); // Initialize the performance list
    }

    // --- Abstract Method ---
    /**
     * Calculates the monthly fee for the member.
     * This method is 'abstract' because the calculation is different for each
     * type of member. Each subclass MUST provide its own implementation.
     * This is a core part of Polymorphism.
     * @return The calculated monthly fee as a double.
     */
    public abstract double calculateMonthlyFee();

    // --- Getters and Setters ---
    // Standard methods to access and modify the object's state.

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

    public void addPerformance(Performance performance) {
        this.performanceHistory.add(performance);
    }

    public List<Performance> getPerformanceHistory() {
        return performanceHistory;
    }

    @Override
    public String toString() {
        return "Member ID: " + memberId + ", Name: " + fullName + ", Joined: " + joinDate;
    }
}
