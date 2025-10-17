package src;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * RegularMember Class (Concrete "Model")
 * This class represents a standard gym member and extends the base Member class.
 */
public class RegularMember extends Member {

    // --- CONSTANTS ---
    private static final double BASE_FEE = 50.0;

    // --- CONSTRUCTOR ---
    public RegularMember(String memberId, String fullName, LocalDate joinDate) {
        // Call the constructor of the parent class (Member).
        super(memberId, fullName, joinDate);
    }

    // --- OVERRIDDEN METHODS ---

    /**
     * Provides the specific fee calculation for a regular member.
     * @return The flat base fee.
     */
    @Override
    public double calculateMonthlyFee() {
        return BASE_FEE;
    }

    /**
     * Formats the RegularMember's data into a CSV string for file storage.
     * This implementation is required by the abstract Member class and resolves the error.
     */
    @Override
    public String toCsvString() {
        String baseDetails = String.join(",", memberId, fullName, "Regular", joinDate.toString());
        
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
        return String.format("[Regular Member] Member ID: %s, Name: %s, Joined: %s, Monthly Fee: $%.2f",
                getMemberId(), getFullName(), getJoinDate(), calculateMonthlyFee())
                + super.toString(); // Append performance history from parent class
    }
}

