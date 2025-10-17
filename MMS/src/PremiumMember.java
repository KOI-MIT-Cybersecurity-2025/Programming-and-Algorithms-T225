package src;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * PremiumMember Class (Concrete "Model")
 * UPDATED: Fee calculation and data formats now depend on MembershipStatus.
 */
public class PremiumMember extends Member {

    private static final double BASE_FEE = 80.0;
    private static final double FROZEN_FEE = 15.0; // A higher frozen fee for premium members
    private static final double PERFORMANCE_DISCOUNT = 0.10; // 10% discount

    private double personalTrainerFee;

    public PremiumMember(String memberId, String fullName, LocalDate joinDate, double personalTrainerFee) {
        super(memberId, fullName, joinDate);
        this.personalTrainerFee = personalTrainerFee;
    }

    public double getPersonalTrainerFee() {
        return personalTrainerFee;
    }

    public void setPersonalTrainerFee(double fee) {
        this.personalTrainerFee = fee;
    }

    /**
     * Calculates the monthly fee for a premium member.
     * - If the status is FROZEN, a reduced fee is charged.
     * - If ACTIVE, the fee includes the personal trainer fee and a potential
     * discount for good performance.
     * 
     * @return The calculated monthly fee.
     */
    @Override
    public double calculateMonthlyFee() {
        if (this.status == MembershipStatus.FROZEN) {
            return FROZEN_FEE;
        }

        double totalFee = BASE_FEE + this.personalTrainerFee;

        // Apply discount if they have performance history and achieved the latest goal
        if (!performanceHistory.isEmpty()) {
            Performance latestPerformance = performanceHistory.get(performanceHistory.size() - 1);
            if (latestPerformance.wasGoalAchieved()) {
                totalFee *= (1 - PERFORMANCE_DISCOUNT);
            }
        }
        return totalFee;
    }

    /**
     * Converts the member's data into a single string for saving to a CSV file.
     * Includes the membership status and personal trainer fee.
     * 
     * @return A CSV-formatted string.
     */
    @Override
    public String toCsvString() {
        // Core details including status and the extra personal trainer fee
        String baseDetails = String.join(",",
                memberId,
                fullName,
                "Premium",
                joinDate.toString(),
                status.toString(),
                String.valueOf(personalTrainerFee));

        // Performance history is appended if it exists
        String performanceDetails = performanceHistory.stream()
                .map(p -> String.format("%d;%d;%b", p.getMonth(), p.getYear(), p.wasGoalAchieved()))
                .collect(Collectors.joining("|"));

        return performanceDetails.isEmpty() ? baseDetails : baseDetails + "," + performanceDetails;
    }

    /**
     * Provides a user-friendly string representation of the PremiumMember object.
     * 
     * @return A descriptive string for display in the UI.
     */
    @Override
    public String toString() {
        return String.format("[Premium Member] Member ID: %s, Name: %s, Joined: %s, Monthly Fee: $%.2f",
                getMemberId(), getFullName(), getJoinDate(), calculateMonthlyFee())
                + super.toString(); // Appends status and performance history
    }
}