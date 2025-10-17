package src;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * PremiumMember Class (Concrete "Model")
 * This class represents a premium gym member with a personal trainer.
 * It extends the base Member class.
 */
public class PremiumMember extends Member {

    // --- CONSTANTS ---
    private static final double BASE_FEE = 80.0;
    private static final double PERFORMANCE_DISCOUNT = 0.10; // 10% discount

    // --- ATTRIBUTES ---
    private double personalTrainerFee;

    // --- CONSTRUCTOR ---
    public PremiumMember(String memberId, String fullName, LocalDate joinDate, double personalTrainerFee) {
        // Call the constructor of the parent class (Member).
        super(memberId, fullName, joinDate);
        this.personalTrainerFee = personalTrainerFee;
    }

    // --- GETTERS AND SETTERS ---
    public double getPersonalTrainerFee() {
        return personalTrainerFee;
    }

    public void setPersonalTrainerFee(double personalTrainerFee) {
        this.personalTrainerFee = personalTrainerFee;
    }

    // --- OVERRIDDEN METHODS ---

    /**
     * Provides the specific fee calculation for a premium member.
     * This demonstrates POLYMORPHISM: it checks the latest performance record
     * and applies a discount if the goal was achieved.
     * @return The calculated monthly fee.
     */
    @Override
    public double calculateMonthlyFee() {
        double totalFee = BASE_FEE + this.personalTrainerFee;

        // Check the latest performance record to see if a discount should be applied.
        if (!performanceHistory.isEmpty()) {
            Performance latestPerformance = performanceHistory.get(performanceHistory.size() - 1);
            if (latestPerformance.wasGoalAchieved()) {
                // Apply a discount if the last recorded goal was achieved.
                totalFee *= (1 - PERFORMANCE_DISCOUNT);
            }
        }
        return totalFee;
    }

    /**
     * NEW METHOD: Formats the PremiumMember's data into a CSV string for file storage.
     * This implementation is required by the abstract Member class and resolves the error.
     */
    @Override
    public String toCsvString() {
        String baseDetails = String.join(",", memberId, fullName, "Premium", joinDate.toString(), String.valueOf(personalTrainerFee));

        // Append performance data, separated by "|"
        String performanceDetails = performanceHistory.stream()
            .map(p -> String.format("%d:%d:%b", p.getMonth(), p.getYear(), p.wasGoalAchieved()))
            .collect(Collectors.joining("|"));

        if (!performanceDetails.isEmpty()) {
            return baseDetails + "|" + performanceDetails;
        }
        return baseDetails;
    }

    /**
     * Overrides the default toString to provide a clear description.
     */
    @Override
    public String toString() {
        return String.format("[Premium Member] Member ID: %s, Name: %s, Joined: %s, Monthly Fee: $%.2f",
                getMemberId(), getFullName(), getJoinDate(), calculateMonthlyFee())
                + super.toString(); // Append performance history from parent class
    }
}

