package src;

import java.time.LocalDate;

/**
 * Represents a regular gym member.
 * This class 'extends' the Member class, inheriting its properties and methods.
 * This is a core example of Inheritance.
 */
public class RegularMember extends Member {

    // A constant specific to this class.
    private static final double BASE_FEE = 50.0;

    /**
     * Constructor for a RegularMember.
     * It calls the parent Member's constructor using 'super()'.
     */
    public RegularMember(String memberId, String fullName, LocalDate joinDate) {
        super(memberId, fullName, joinDate);
    }

    /**
     * Provides a concrete implementation for the abstract method from the Member class.
     * This is required for the code to compile.
     */
    @Override
    public double calculateMonthlyFee() {
        return BASE_FEE;
    }

    @Override
    public String toString() {
        return "[Regular Member] " + super.toString() + String.format(", Monthly Fee: $%.2f", calculateMonthlyFee());
    }
}
