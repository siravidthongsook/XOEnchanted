# XO Enchanted 4×4 — Game & Technical Specification

## 1. Game Overview

**XO Enchanted** is a strategic 4×4 grid game for 2 players (X and O) with energy management, special skills, and dynamic board state.

### Victory Conditions
- First to **3 points** wins
- If 24 total turns elapse: highest score wins
- If tied after 24 turns: **Sudden Death** (next point wins)

---

## 2. Game Rules

### 2.1 Board
- 4×4 grid (16 cells)
- Cells can be: Empty, X, O, or Sealed

### 2.2 Scoring Lines
A **scoring line** = 3 contiguous cells of the same player in:
- Horizontal (4 lines per row = 8 total)
- Vertical (4 lines per column = 8 total)
- Diagonal (4 main diagonals + 4 anti-diagonals = 8 total)

**Total: 24 possible scoring lines**

### 2.3 Energy System
| Aspect | Value |
|--------|-------|
| Starting Energy | First player: 2, Second player: 3 |
| Gain per Turn | +1 (normal), +2 (Priority Turn) |
| Max Energy | 5 |
| Scoring Bonus | +1 when you score |
| Disrupt Penalty | Skip +1 gain next turn |

### 2.4 Turn Structure

**Step A: Gain Energy**
1. If Disrupt penalty active → skip gain, clear penalty
2. Else if Priority Turn → gain +2
3. Else → gain +1
4. Cap at 5 max

**Step B: Choose ONE Action**
- Place 1 piece (costs 0 energy)
- OR use a Skill (costs energy)

**Step C: Scoring Check**
If 3-in-a-row formed:
1. +1 point to current player
2. If multiple lines exist, player **chooses 1 line**
3. Remove only the 3 cells of chosen line
4. +1 energy bonus (cap at 5)
5. Opponent gets **Priority Turn** flag

### 2.5 Multi-Line Scoring Rule
- **Only 1 point** awarded per turn, regardless of lines formed
- Player selects **1 line** to clear
- Other lines remain (unless they share cells with cleared line)

**Example:**
```
. X .
X X X
. X .
```
If horizontal chosen → middle row cleared → vertical breaks naturally

### 2.6 Skills

| Skill | Cost | Effect |
|-------|------|--------|
| **Seal** | 2 | Block 1 empty cell for 1 turn (cannot place/shift into) |
| **Shift** | 2 | Move your piece 1 cell orthogonally to adjacent empty |
| **Disrupt** | 3 | Remove 1 opponent piece (unless Frozen). Next turn: no +1 energy gain |
| **Double Place** | 4 | Place 2 pieces (must not be orthogonally adjacent). Max 1 point per turn still applies |

### 2.7 Frozen Rule
If a piece is **not shifted** for 3 consecutive turns of its owner:
- It becomes **Frozen** for 1 turn
- Cannot be targeted by **Disrupt** while frozen
- Auto-unfreezes after 1 turn

### 2.8 Overheat Rule
If a player ends their turn at **5 energy** for **2 consecutive turns**:
- At start of 3rd turn, energy resets to **3**

### 2.9 Priority Turn
When a player scores:
- Opponent receives **Priority Turn** flag
- On their next turn: gains +2 energy instead of +1
- Flag clears after use

**Purpose:** Prevent snowballing advantage

---

## 3. Technical Architecture

### 3.1 Package Structure
```
src/main/java/game/
├── model/          # Data classes (immutable where possible)
├── engine/         # Game logic & rules
└── ui/             # JavaFX controllers & views

src/test/java/game/
└── engine/         # Unit tests for game logic
```

### 3.2 Core Interfaces

#### 3.2.1 GameEngine Interface
```java
package game.engine;

public interface GameEngine {
    /**
     * Start a new game
     * @return initial GameState
     */
    GameState startGame();

    /**
     * Execute a player action
     * @param action the action to perform
     * @return new GameState after action
     * @throws IllegalActionException if action invalid
     */
    GameState executeAction(GameState currentState, Action action)
        throws IllegalActionException;

    /**
     * Get all valid actions for current player
     */
    List<Action> getValidActions(GameState state);

    /**
     * Check if game is over and return result
     */
    Optional<GameResult> checkGameOver(GameState state);
}
```

#### 3.2.2 Action Interface (Sealed/Marker)
```java
package game.engine;

public sealed interface Action permits
    PlaceAction, ShiftAction, DisruptAction,
    SealAction, DoublePlaceAction {

    PlayerId getPlayer();
    int getEnergyCost();
}

// Implementations
public record PlaceAction(PlayerId player, Position position)
    implements Action {
    @Override public int getEnergyCost() { return 0; }
}

public record ShiftAction(PlayerId player, Position from, Position to)
    implements Action {
    @Override public int getEnergyCost() { return 2; }
}

public record DisruptAction(PlayerId player, Position target)
    implements Action {
    @Override public int getEnergyCost() { return 3; }
}

public record SealAction(PlayerId player, Position target)
    implements Action {
    @Override public int getEnergyCost() { return 2; }
}

public record DoublePlaceAction(PlayerId player, Position pos1, Position pos2)
    implements Action {
    @Override public int getEnergyCost() { return 4; }
}
```

#### 3.2.3 GameState (Immutable)
```java
package game.model;

public record GameState(
    Board board,
    PlayerState xState,
    PlayerState oState,
    PlayerId currentPlayer,
    int totalTurns,
    GamePhase phase
) {
    public PlayerState getCurrentPlayerState() {
        return currentPlayer == PlayerId.X ? xState : oState;
    }

    public PlayerState getOpponentState() {
        return currentPlayer == PlayerId.X ? oState : xState;
    }
}
```

#### 3.2.4 Board Interface
```java
package game.model;

public interface Board {
    /**
     * Get cell at position
     */
    Cell getCell(Position pos);

    /**
     * Get all pieces of a player
     */
    List<Position> getPieces(PlayerId player);

    /**
     * Check if position is valid and empty
     */
    boolean isEmpty(Position pos);

    /**
     * Detect all scoring lines for a player
     */
    List<ScoringLine> detectScoringLines(PlayerId player);

    /**
     * Create new board with cell updated
     */
    Board withCell(Position pos, Cell newCell);

    /**
     * Create new board with positions removed
     */
    Board withCellsRemoved(List<Position> positions);
}
```

#### 3.2.5 PlayerState (Immutable)
```java
package game.model;

public record PlayerState(
    int score,
    int energy,
    int ownerTurnCount,         // tracks turns for frozen logic
    boolean hasPriorityTurn,
    boolean hasDisruptPenalty,
    int consecutiveTurnsAtMaxEnergy
) {
    public static final int MAX_ENERGY = 5;
    public static final int WINNING_SCORE = 3;

    public PlayerState withEnergyGain(int amount) {
        return new PlayerState(
            score,
            Math.min(energy + amount, MAX_ENERGY),
            ownerTurnCount,
            hasPriorityTurn,
            hasDisruptPenalty,
            energy + amount >= MAX_ENERGY ? consecutiveTurnsAtMaxEnergy + 1 : 0
        );
    }

    public PlayerState withScore(int newScore) {
        return new PlayerState(
            newScore,
            Math.min(energy + 1, MAX_ENERGY), // scoring bonus
            ownerTurnCount,
            hasPriorityTurn,
            hasDisruptPenalty,
            consecutiveTurnsAtMaxEnergy
        );
    }
}
```

#### 3.2.6 Cell & Position
```java
package game.model;

public record Position(int row, int col) {
    public static final int BOARD_SIZE = 4;

    public boolean isValid() {
        return row >= 0 && row < BOARD_SIZE &&
               col >= 0 && col < BOARD_SIZE;
    }

    public List<Position> getOrthogonalNeighbors() {
        // return up, down, left, right if valid
    }
}

public sealed interface Cell {
    record Empty() implements Cell {}
    record Piece(PlayerId owner, int lastMovedTurn, boolean frozen) implements Cell {}
    record Sealed(int expiresAtTurn) implements Cell {}
}
```

#### 3.2.7 ScoringLine
```java
package game.model;

public record ScoringLine(
    Position pos1,
    Position pos2,
    Position pos3,
    LineType type
) {
    public enum LineType { HORIZONTAL, VERTICAL, DIAGONAL }

    public List<Position> getPositions() {
        return List.of(pos1, pos2, pos3);
    }
}
```

#### 3.2.8 GameResult
```java
package game.model;

public sealed interface GameResult {
    record Victory(PlayerId winner, VictoryType type) implements GameResult {}
    record SuddenDeath() implements GameResult {}

    enum VictoryType {
        SCORE_LIMIT,      // reached 3 points
        TURN_LIMIT,       // higher score after 24 turns
        SUDDEN_DEATH      // scored in sudden death
    }
}
```

### 3.3 Core Abstract Classes

#### 3.3.1 ActionValidator
```java
package game.engine;

public abstract class ActionValidator {
    /**
     * Validate if action is legal in current state
     * @throws IllegalActionException with reason if invalid
     */
    public abstract void validate(GameState state, Action action)
        throws IllegalActionException;

    /**
     * Check basic energy requirement
     */
    protected boolean hasEnoughEnergy(PlayerState player, Action action) {
        return player.energy() >= action.getEnergyCost();
    }
}
```

#### 3.3.2 RuleEngine
```java
package game.engine;

public abstract class RuleEngine {
    /**
     * Apply turn start rules (energy gain, overheats, etc.)
     */
    public abstract GameState applyTurnStartRules(GameState state);

    /**
     * Apply action and return intermediate state
     */
    public abstract GameState applyAction(GameState state, Action action);

    /**
     * Apply turn end rules (scoring, freezing, etc.)
     */
    public abstract GameState applyTurnEndRules(GameState state);

    /**
     * Full turn execution pipeline
     */
    public GameState executeTurn(GameState state, Action action) {
        GameState afterStart = applyTurnStartRules(state);
        GameState afterAction = applyAction(afterStart, action);
        GameState afterEnd = applyTurnEndRules(afterAction);
        return afterEnd;
    }
}
```

### 3.4 Key Enums

```java
package game.model;

public enum PlayerId {
    X, O;

    public PlayerId opponent() {
        return this == X ? O : X;
    }
}

public enum GamePhase {
    NORMAL,         // turns 1-24
    SUDDEN_DEATH    // after turn 24 if tied
}
```

---

## 4. Implementation Checklist

### Phase 1: Core Model
- [ ] Implement all model classes (immutable)
- [ ] Write unit tests for model invariants
- [ ] Implement Board with line detection

### Phase 2: Game Engine
- [ ] Implement ActionValidator
- [ ] Implement RuleEngine with all rules
- [ ] Implement GameEngine interface
- [ ] Write comprehensive engine tests (all rules + edge cases)

### Phase 3: UI
- [ ] Design board grid view
- [ ] Design player info panels (score, energy)
- [ ] Implement action selection UI
- [ ] Add visual feedback (scoring lines, frozen pieces)
- [ ] Implement game flow controller

### Phase 4: Polish
- [ ] Add game replay/history
- [ ] Add undo functionality (for testing)
- [ ] Add AI opponent (optional)
- [ ] Polish animations & transitions

---

## 5. Testing Requirements

### Critical Test Scenarios
1. **Energy Management**
   - Starting energy asymmetry (2 vs 3)
   - Energy cap at 5
   - Priority turn +2 gain
   - Disrupt penalty (skip gain)
   - Overheat reset (2 consecutive ends at 5 → reset to 3)

2. **Scoring**
   - Multi-line detection
   - Player chooses 1 line
   - Only 1 point per turn
   - Energy bonus on scoring
   - Opponent gets priority turn

3. **Skills**
   - Seal expiration (1 turn)
   - Shift validation (adjacent, empty)
   - Disrupt with frozen immunity
   - Double place non-adjacency

4. **Frozen Logic**
   - Piece frozen after 3 owner turns unmoved
   - Frozen immunity to Disrupt
   - Unfreezes after 1 turn

5. **Game End**
   - 3 points win
   - 24 turns → highest score
   - Sudden death after tie

---

## 6. Collaboration Guidelines

### Code Style
- Use **immutable data structures** for game state
- All mutations return **new instances**
- Prefer **records** for simple data classes
- Use **sealed interfaces** for action types

### Git Workflow
- Branch naming: `feature/action-validation`, `fix/frozen-logic`
- Commit messages: `feat:`, `fix:`, `test:`, `docs:`
- PR requires: passing tests + code review

### Communication
- Document **all rule edge cases** in test names
- Use JavaDoc for public APIs
- Flag ambiguous rules with `// TODO: clarify with team`

---

## 7. Open Questions / Clarifications Needed

1. **Seal duration**: Spec says "1 turn" — is this 1 full round (both players) or 1 player turn?
2. **Frozen timing**: Does frozen status check at turn START or END?
3. **Overheat energy reset**: Does it happen at start of 3rd turn (before energy gain) or after?
4. **Sudden death**: Are all rules same, or any modifications?
5. **Double Place scoring**: If both pieces create separate lines, still max 1 point and choose 1 line?

---

## 8. Dependencies

### Build Configuration (build.gradle)
```gradle
plugins {
    id 'java'
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.1.0'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

javafx {
    version = "17.0.2"
    modules = ['javafx.controls', 'javafx.fxml']
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.2'
    testImplementation 'org.assertj:assertj-core:3.24.2'
}

test {
    useJUnitPlatform()
}

application {
    mainClass = 'game.ui.XOEnchantedApp'
}
```

---

## 9. Success Metrics

- [ ] All 24 scoring lines correctly detected
- [ ] All 5 skills work per specification
- [ ] Energy system respects all rules (cap, gains, penalties)
- [ ] Frozen/Overheat mechanics work correctly
- [ ] Game ends correctly under all victory conditions
- [ ] UI is intuitive and shows all relevant state
- [ ] Test coverage >90% for engine package

---

**Document Version:** 1.0
**Last Updated:** 2026-02-22
**Owner:** XO Enchanted Dev Team
