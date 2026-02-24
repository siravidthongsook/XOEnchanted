package game.model;

public class PlayerState {
    private static final int MAX_ENERGY = 10;

    private int score;
    private int energy;
    private boolean priorityTurn;
    private boolean skipNextEnergyGain; // เพิ่มตัวแปรสำหรับ Penalty ของ Disrupt

    public PlayerState() {
        this.score = 0;
        this.energy = 0;
        this.priorityTurn = false;
        this.skipNextEnergyGain = false;
    }

    public int getScore() { return score; }
    public int getEnergy() { return energy; }
    public boolean isPriorityTurn() { return priorityTurn; }
    public void setPriorityTurn(boolean priorityTurn) { this.priorityTurn = priorityTurn; }

    public void setSkipNextEnergyGain(boolean skip) {
        this.skipNextEnergyGain = skip;
    }

    public void gainEnergy(int amount) {
        this.energy = Math.min(this.energy + amount, MAX_ENERGY);
    }

    public void spendEnergy(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Energy amount cannot be negative");
        if (energy < amount) throw new IllegalStateException("Not enough energy");
        energy -= amount;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public int consumeTurnStartGain() {
        // ถ้าติด Penalty จาก Disrupt ให้งดรับ Energy
        if (skipNextEnergyGain) {
            skipNextEnergyGain = false;
            priorityTurn = false;
            return 0;
        }
        int gain = priorityTurn ? 2 : 1;
        priorityTurn = false;
        return gain;
    }
}