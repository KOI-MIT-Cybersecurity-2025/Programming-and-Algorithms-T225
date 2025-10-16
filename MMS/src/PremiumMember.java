package src;

import java.time.LocalDate;

/**
 * Represents a premium gym member, who may have a personal trainer.
 * This class also 'extends' Member, showing how multiple classes can inherit
 * from the same parent.
 */
public class PremiumMember extends Member {

    private static final double BASE_FEE = 80.0; // Premium members might have a different base fee
    private double personalTrainerFee;

    /**
     * Constructor for a PremiumMember.
     * It takes an additional parameter for the personal trainer fee.
     */
    public PremiumMember(String memberId, String fullName, LocalDate joinDate, double personalTrainerFee) {
        super(memberId, fullName, joinDate);
        this.personalTrainerFee = personalTrainerFee;
    }

    /**
     * A unique implementation of the monthly fee calculation for premium members.
     * It includes the base fee plus the personal trainer cost.
     */
    @Override
    public double calculateMonthlyFee() {
        // Example: A premium member might get a 10% discount if they achieved their goals last month.
        boolean achievedLastMonth = !performanceHistory.isEmpty() && performanceHistory.get(performanceHistory.size() - 1).wasGoalAchieved();
        double totalFee = BASE_FEE + personalTrainerFee;
        if (achievedLastMonth) {
            totalFee *= 0.90; // Apply a 10% discount
        }
        return totalFee;
    }

    public double getPersonalTrainerFee() {
        return personalTrainerFee;
    }

    public void setPersonalTrainerFee(double personalTrainerFee) {
        this.personalTrainerFee = personalTrainerFee;
    }

    @Override
    public String toString() {
        return "[Premium Member] " + super.toString() + String.format(", Monthly Fee: $%.2f", calculateMonthlyFee());
    }
}
