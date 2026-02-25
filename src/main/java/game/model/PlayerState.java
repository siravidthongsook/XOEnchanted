package game.model;

public class PlayerState {
    private static final int MAX_ENERGY = 5;

    private int score;
    private int energy;
    private boolean priorityTurn;

    public PlayerState() {
        this.score = 0;
        this.energy = 0;
        this.priorityTurn = false;
    }

    public int getScore() {
        return score;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isPriorityTurn() {
        return priorityTurn;
    }

    public void gainEnergy(int amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    public void spendEnergy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Energy amount cannot be negative");
        }
        if (energy < amount) {
            throw new IllegalStateException("Not enough energy");
        }
        energy -= amount;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public void setPriorityTurn(boolean priorityTurn) {
        this.priorityTurn = priorityTurn;
    }

    public int consumeTurnStartGain() {
        int gain = priorityTurn ? 2 : 1;
        priorityTurn = false;
        return gain;
    }
}
