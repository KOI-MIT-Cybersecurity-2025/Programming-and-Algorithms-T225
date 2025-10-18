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
     * ADDED: Implements the abstract method from the parent Member class.
     */
    @Override
    public String getMemberType() {
        return "Premium";
    }

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

    // The toString() method is now inherited directly from the Member class.
}

