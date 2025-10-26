package src;

import java.util.Comparator;

/**
 * A Comparator class used to sort Member objects by their full name.
 * This is an example of the Strategy Pattern, allowing "plug-in" sorting logic.
 */
public class MemberNameComparator implements Comparator<Member> {

    /**
     * Compares two Member objects based on their full name (case-insensitive).
     * @param m1 The first member to be compared.
     * @param m2 The second member to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Member m1, Member m2) {
        return m1.getFullName().compareToIgnoreCase(m2.getFullName());
    }
}

