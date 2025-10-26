package src;

import java.util.Comparator;

/**
 * A Comparator class used to sort Member objects by their join date.
 * Sorts from earliest join date to latest.
 */
public class MemberJoinDateComparator implements Comparator<Member> {

    /**
     * Compares two Member objects based on their join date.
     * @param m1 The first member to be compared.
     * @param m2 The second member to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Member m1, Member m2) {
        // LocalDate.compareTo naturally sorts from earliest to latest
        return m1.getJoinDate().compareTo(m2.getJoinDate());
    }
}

