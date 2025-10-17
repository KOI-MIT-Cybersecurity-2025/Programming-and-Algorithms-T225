package src;

/**
 * Performance Class (The "Model")
 * This is a simple data class that encapsulates all the information
 * related to a single month's performance for a member.
 */
public class Performance {

    // --- ATTRIBUTES ---
    private int month;
    private int year;
    private boolean goalAchieved;

    // --- CONSTRUCTOR ---
    public Performance(int month, int year, boolean goalAchieved) {
        // Basic validation for month and year
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be a realistic value.");
        }
        this.month = month;
        this.year = year;
        this.goalAchieved = goalAchieved;
    }

    // --- GETTERS ---
    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean wasGoalAchieved() {
        return goalAchieved;
    }

    /**
     * Overrides the default toString method to provide a clear, readable
     * description of the performance record.
     * Example output: "10/2025 - Goal Achieved: Yes"
     */
    @Override
    public String toString() {
        return String.format("%d/%d - Goal Achieved: %s",
                month, year, (goalAchieved ? "Yes" : "No"));
    }
}