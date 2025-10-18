package src;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * RegularMember Class (Concrete "Model")
 * UPDATED: Fee calculation now depends on MembershipStatus.
 */
public class RegularMember extends Member {

    private static final double BASE_FEE = 50.0;
    private static final double FROZEN_FEE = 10.0; // A nominal fee for frozen accounts

    public RegularMember(String memberId, String fullName, LocalDate joinDate) {
        super(memberId, fullName, joinDate);
    }

    @Override
    public double calculateMonthlyFee() {
        if (this.status == MembershipStatus.FROZEN) {
            return FROZEN_FEE;
        }
        return BASE_FEE;
    }

    /**
     * ADDED: Implements the abstract method from the parent Member class.
     */
    @Override
    public String getMemberType() {
        return "Regular";
    }

    @Override
    public String toCsvString() {
        // Core details including status
        String baseDetails = String.join(",",
                memberId,
                fullName,
                "Regular",
                joinDate.toString(),
                status.toString());

        // Performance history is appended if it exists
        String performanceDetails = performanceHistory.stream()
                .map(p -> String.format("%d;%d;%b", p.getMonth(), p.getYear(), p.wasGoalAchieved()))
                .collect(Collectors.joining("|"));

        return performanceDetails.isEmpty() ? baseDetails : baseDetails + "," + performanceDetails;
    }

    // The toString() method is now inherited directly from the Member class.
}

