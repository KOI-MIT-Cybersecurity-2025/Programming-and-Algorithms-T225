package src;

/**
 * A simple data class to hold performance information for a member for a given month.
 * This demonstrates Encapsulation, bundling data (month, year, achieved) with
 * methods that operate on that data.
 */
public class Performance {
    private int month;
    private int year;
    private boolean goalsAchieved;

    public Performance(int month, int year, boolean goalsAchieved) {
        this.month = month;
        this.year = year;
        this.goalsAchieved = goalsAchieved;
    }

    public boolean wasGoalAchieved() {
        return goalsAchieved;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Performance [" + month + "/" + year + ", Goals Achieved: " + goalsAchieved + "]";
    }
}
