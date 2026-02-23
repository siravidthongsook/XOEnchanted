# XO Enchanted - Grading Rubric Compliance Checklist

**Target Score: 70/70**

---

## Core Program & Technical Implementation (35 pts)

### 1. Overall Performance (10 pts)
- [ ] **10 pts**: Program runs correctly with no bugs
- [ ] **Requirement**: All game rules implemented correctly
- [ ] **Test**: Play multiple full games with all skills
- [ ] **Verify**: Energy system, scoring, frozen/overheat mechanics work

**Status**: ⚠️ Pending implementation

---

### 2. JUnit Testing (5 pts)
- [ ] **5 pts**: Full test coverage with ability to explain logic
- [ ] **Required Tests**:
  - [ ] All 24 scoring line detection
  - [ ] Energy system (gain, cap, priority, penalty, overheat)
  - [ ] All 5 skills (Seal, Shift, Disrupt, Double Place)
  - [ ] Frozen rule (3 turns → frozen → immunity)
  - [ ] Victory conditions (3 points, 24 turns, sudden death)
  - [ ] Multi-line scoring (choose 1 line only)
  - [ ] Action validation (energy cost, valid positions)
- [ ] **Target**: >90% coverage for `game.engine` and `game.model`

**Implementation Guide**:
```java
// Example test structure
@Test
void testEnergyOverheat() {
    // Given player at 5 energy for 2 consecutive turns
    // When starting 3rd turn
    // Then energy resets to 3
}

@Test
void testFrozenImmunity() {
    // Given piece frozen (not moved for 3 owner turns)
    // When opponent uses Disrupt on frozen piece
    // Then action should be invalid
}
```

**Status**: ⚠️ Test framework ready, tests not written

---

### 3. UI/Beauty - Application (5 pts)
- [ ] **5 pts**: Menu system + >10 components + styled
- [ ] **Required Components** (minimum 10):

**Main Menu Screen:**
1. Title/Logo (Label or ImageView)
2. New Game Button
3. Load Game Button
4. Rules Button
5. Settings Button
6. Exit Button

**Game Screen:**
7. 4×4 Grid (16 Buttons or Canvas with click regions)
8. Player X Info Panel (HBox/VBox with Labels)
9. Player O Info Panel (HBox/VBox with Labels)
10. Action Selection Panel (5 Buttons: Place, Seal, Shift, Disrupt, Double)
11. Turn Indicator Label
12. Game Log (TextArea or ListView)
13. Menu/Pause Button
14. Score Display (Labels)
15. Energy Display (ProgressBar or custom graphic)

**Additional Screens:**
16. Settings Dialog (Volume sliders, theme selection)
17. Rules Dialog (ScrollPane with rule text)
18. Game Over Dialog (Winner announcement, scores)

- [ ] **Styling**: CSS file for colors, fonts, hover effects
- [ ] **Layout**: Use FXML or programmatic layouts (BorderPane, GridPane, etc.)

**Status**: ❌ Not specified in current spec

---

### 4. UI/Beauty - Game Graphics & Sound (5 pts)
- [ ] **5 pts**: Both graphics and sound
- [ ] **4 pts**: Graphics only

**Graphics Requirements:**
- [ ] Custom cell rendering (not just default buttons)
- [ ] Visual states: Empty, X piece, O piece, Sealed, Frozen
- [ ] Animation for:
  - [ ] Piece placement
  - [ ] Scoring line highlight
  - [ ] Piece removal (when line clears)
  - [ ] Energy gain indicator
  - [ ] Skill activation effects
- [ ] Particle effects (optional but impressive)

**Sound Requirements:**
- [ ] Background music (loopable, adjustable volume)
- [ ] Sound effects for:
  - [ ] Piece placement
  - [ ] Skill activation (different sound per skill)
  - [ ] Scoring (celebratory sound)
  - [ ] Invalid action (error beep)
  - [ ] Game over
- [ ] Use JavaFX MediaPlayer or AudioClip

**Implementation**:
```java
// Sound manager
public class SoundManager {
    private final AudioClip placeSound;
    private final AudioClip scoreSound;
    private final MediaPlayer bgMusic;

    public void playPlaceSound() { ... }
    public void playSkillSound(SkillType skill) { ... }
}
```

**Status**: ❌ Not specified in current spec

---

### 5. Difficulty Level (5 pts)
- [ ] **5 pts**: Complex game (Paint, Battle City level)
- [ ] **4 pts**: Medium complexity (Winamp, media player)

**Your Game Complexity**: ✅ **Expected 4-5 pts**
- Complex rule engine (energy, skills, frozen, overheat)
- Strategic gameplay (not just random moves)
- Dynamic board state changes
- Multiple victory conditions

**To reach 5 pts**, consider adding:
- [ ] AI opponent with minimax/strategy
- [ ] Online multiplayer
- [ ] Replay system with scrubbing
- [ ] Advanced animations/effects

**Status**: ✅ Likely 4 pts minimum

---

### 6. JAR File Execution (5 pts)
- [ ] Export runnable JAR file
- [ ] Test in clean folder (no IDE dependencies)
- [ ] Include assets (sounds, images) in JAR

**build.gradle additions needed**:
```gradle
jar {
    manifest {
        attributes 'Main-Class': 'game.ui.XOEnchantedApp'
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Or use JavaFX jlink/jpackage for better distribution
```

**Test checklist**:
- [ ] Run `./gradlew jar`
- [ ] Copy JAR to new folder
- [ ] Run `java -jar XOEnchanted.jar`
- [ ] Verify all features work (sounds, images, etc.)

**Status**: ⚠️ Build config present, JAR task needs verification

---

## OOP Principles & Design (30 pts)

**CRITICAL**: Must be able to explain each principle during presentation!

### 7. Inheritance (6 pts)
- [ ] **Present in spec**: ✅ Yes
  - `ActionValidator` (abstract class)
  - `RuleEngine` (abstract class)
- [ ] **Must explain**:
  - Why abstract classes used?
  - What do subclasses override?
  - Benefits of inheritance here?

**Example Implementation**:
```java
// Base validator
public abstract class ActionValidator {
    public abstract void validate(GameState state, Action action);

    protected boolean hasEnoughEnergy(PlayerState player, Action action) {
        return player.energy() >= action.getEnergyCost();
    }
}

// Concrete validators
public class PlaceActionValidator extends ActionValidator {
    @Override
    public void validate(GameState state, Action action) {
        PlaceAction place = (PlaceAction) action;
        if (!state.board().isEmpty(place.position())) {
            throw new IllegalActionException("Cell not empty");
        }
    }
}

public class ShiftActionValidator extends ActionValidator {
    @Override
    public void validate(GameState state, Action action) {
        ShiftAction shift = (ShiftAction) action;
        if (!hasEnoughEnergy(state.getCurrentPlayerState(), action)) {
            throw new IllegalActionException("Not enough energy");
        }
        // ... more validation
    }
}
```

**Presentation Answer**:
> "We use inheritance for ActionValidator to share common validation logic (like energy checks) while allowing each action type to define its specific validation rules. RuleEngine uses inheritance to separate turn phases (start, action, end) with a template method pattern."

**Status**: ✅ Specified, needs implementation

---

### 8. Interface (3 pts)
- [ ] **Present in spec**: ✅ Yes
  - `GameEngine`
  - `Board`
  - `Action` (sealed interface)
  - `Cell` (sealed interface)
  - `GameResult` (sealed interface)
- [ ] **Must explain**:
  - Why interfaces instead of classes?
  - What's the contract?
  - Who implements them?

**Presentation Answer**:
> "We use interfaces to define contracts without implementation. For example, GameEngine interface allows different implementations (local game, networked game, AI game) without changing the UI code. Sealed interfaces like Action ensure type safety—only specific action types are allowed."

**Status**: ✅ Specified, needs implementation

---

### 9. Polymorphism (6 pts)
- [ ] **Present in spec**: ✅ Yes
  - Action types (PlaceAction, ShiftAction, etc.)
  - Cell types (Empty, Piece, Sealed)
  - GameResult types (Victory, SuddenDeath)
- [ ] **Must demonstrate**:
  - Same method, different behavior
  - Runtime type resolution

**Example**:
```java
// Polymorphic action handling
public GameState executeAction(GameState state, Action action) {
    // Single method handles all action types polymorphically
    return switch (action) {
        case PlaceAction place -> handlePlace(state, place);
        case ShiftAction shift -> handleShift(state, shift);
        case DisruptAction disrupt -> handleDisrupt(state, disrupt);
        case SealAction seal -> handleSeal(state, seal);
        case DoublePlaceAction doublePlace -> handleDoublePlace(state, doublePlace);
    };
}

// Or with visitor pattern
public interface ActionHandler {
    GameState handle(PlaceAction action);
    GameState handle(ShiftAction action);
    // ... etc
}
```

**Presentation Answer**:
> "We use polymorphism extensively. The Action interface allows executeAction() to handle any action type uniformly. At runtime, the correct handler is called based on the actual action type (Place, Shift, etc.). This makes adding new actions easy—just create a new Action subtype."

**Status**: ✅ Specified, needs implementation

---

### 10. Access Modifiers (2 pts)
- [ ] Proper use of `public`, `private`, `protected`
- [ ] Encapsulation of internal state
- [ ] Package-private where appropriate

**Guidelines**:
```java
public class GameEngineImpl implements GameEngine {
    private final ActionValidator validator;  // private dependencies
    private final RuleEngine ruleEngine;

    public GameEngineImpl() { ... }  // public constructor

    @Override
    public GameState executeAction(...) { ... }  // public interface

    private void logAction(Action action) { ... }  // private helper
}

// Package-private for internal use
record InternalState(...) { }  // no modifier = package-private
```

**Checklist**:
- [ ] No unnecessary `public` methods
- [ ] All fields are `private` or `private final`
- [ ] Helper methods are `private`
- [ ] Only API methods are `public`

**Status**: ⚠️ Needs review during implementation

---

### 11. Overall Design (5 pts)
- [ ] Clean architecture (model-engine-ui separation)
- [ ] Low coupling, high cohesion
- [ ] SOLID principles followed
- [ ] Can explain design decisions

**Key Points to Explain**:
1. **Immutability**: Why GameState is immutable
   > "Immutable state prevents bugs from unintended mutations and makes game state history/replay trivial."

2. **Separation**: Why model ≠ engine ≠ UI
   > "Model is pure data, engine is pure logic, UI is pure presentation. We can test engine without JavaFX, and swap UI (CLI/GUI) without touching logic."

3. **Sealed types**: Why use sealed interfaces
   > "Sealed interfaces give exhaustive pattern matching. Compiler ensures we handle all action types—can't forget a case."

4. **Records**: Why use records
   > "Records are immutable by default, have built-in equals/hashCode, and make code concise. Perfect for data transfer objects."

**Status**: ✅ Strong foundation in spec

---

### 12. Coding Style (3 pts)
- [ ] Follow Java naming conventions
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
- [ ] Consistent formatting (indentation, braces)
- [ ] Meaningful names (no `x`, `temp`, `data`)
- [ ] Comments for complex logic only (code should be self-documenting)

**Checklist**:
```java
// Good
public class GameEngineImpl implements GameEngine {
    private static final int MAX_TURNS = 24;
    private final ActionValidator actionValidator;

    public GameState executeAction(GameState currentState, Action action) {
        validateAction(currentState, action);
        return applyAction(currentState, action);
    }
}

// Bad
public class gameengine {
    int x = 24;  // what is x?
    ActionValidator av;  // unclear abbreviation

    public GameState exec(GameState s, Action a) { ... }
}
```

**Status**: ✅ Spec shows good style, enforce in code review

---

## Submission & Presentation (10 pts)

### 13. Report (10 pts)
**Required Contents**:
- [ ] **User Manual**
  - [ ] How to start game
  - [ ] How to play (with screenshots)
  - [ ] How to use each skill
  - [ ] Victory conditions
- [ ] **UML Diagrams**
  - [ ] Class diagram (all classes, interfaces, relationships)
  - [ ] Sequence diagram (sample game turn flow)
  - [ ] Package diagram (showing architecture)
- [ ] **JavaDoc**
  - [ ] Generate with `./gradlew javadoc`
  - [ ] Host on GitHub Pages or include HTML in submission
  - [ ] Link in report

**File Structure**:
```
docs/
├── UserManual.md (or .pdf)
├── uml/
│   ├── class-diagram.png
│   ├── sequence-diagram.png
│   └── package-diagram.png
└── javadoc/ (generated HTML)
```

**Tools for UML**:
- PlantUML (text-based, version-controllable)
- IntelliJ IDEA (Diagrams → Show Diagram)
- draw.io / Lucidchart

**Status**: ❌ Not created yet

---

### 14. GitHub Submission (5 pts)
- [ ] **Code**: All source files in `src/`
- [ ] **JAR**: Runnable JAR in `build/libs/` or `release/`
- [ ] **Report**: In `docs/` folder
- [ ] **UML**: In `docs/uml/` folder

**Deductions**: -1 per missing item

**README.md** should include:
```markdown
# XO Enchanted 4x4

## How to Run
```bash
java -jar XOEnchanted.jar
# or
./gradlew run
```

## Documentation
- [User Manual](docs/UserManual.md)
- [JavaDoc](docs/javadoc/index.html)
- [UML Diagrams](docs/uml/)

## Building
```bash
./gradlew build
./gradlew jar
```
```

**Status**: ⚠️ Partial (code structure ready, deliverables pending)

---

### 15. Video Presentation (0 pts, but missing = 0 total!)
- [ ] **Maximum 10 minutes**
- [ ] **Show**:
  - [ ] Running application (all features)
  - [ ] Code walkthrough (OOP principles)
  - [ ] Test execution (JUnit results)
- [ ] **Explain**:
  - [ ] Why you made design choices
  - [ ] How inheritance/interface/polymorphism used
  - [ ] How game rules implemented
- [ ] **Recording tips**:
  - Use OBS Studio or Zoom recording
  - Test audio before final recording
  - Practice to stay under 10 minutes
  - Show code + running app side-by-side

**Script Outline**:
1. (0:00-1:00) Intro & game demo
2. (1:00-3:00) Code architecture & OOP principles
3. (3:00-5:00) Key implementation details
4. (5:00-7:00) Test demonstration
5. (7:00-10:00) Challenges & conclusion

**Status**: ❌ Not recorded

---

## Summary Score Projection

| Category | Max | Current Status | Notes |
|----------|-----|----------------|-------|
| **Performance** | 10 | ⚠️ TBD | Need full implementation |
| **JUnit Tests** | 5 | ✅ 4-5 | Framework ready, write tests |
| **UI/App** | 5 | ❌ 0-3 | Need menu + 10+ components |
| **UI/Game** | 5 | ❌ 0-3 | Need graphics + sound |
| **Difficulty** | 5 | ✅ 4-5 | Complex game logic |
| **JAR** | 5 | ⚠️ 3-5 | Need to verify export |
| **Inheritance** | 6 | ✅ 6 | Well specified |
| **Interface** | 3 | ✅ 3 | Well specified |
| **Polymorphism** | 6 | ✅ 6 | Well specified |
| **Access Modifiers** | 2 | ⚠️ 1-2 | Need code review |
| **Design** | 5 | ✅ 4-5 | Strong architecture |
| **Coding Style** | 3 | ⚠️ 2-3 | Enforce in impl |
| **Report** | 10 | ❌ 0 | Need user manual + UML |
| **GitHub** | 5 | ⚠️ 2-3 | Missing deliverables |
| **Video** | 0 | ❌ 0 | MUST CREATE |

**Current Projection: 35-50 / 70**

---

## Action Plan to Reach 70/70

### Critical Path (Must Do):
1. ✅ **Implement core engine** (Performance: 10 pts)
2. ✅ **Write comprehensive tests** (Testing: 5 pts)
3. ⚠️ **Build full UI with 10+ components** (UI/App: 5 pts)
4. ⚠️ **Add graphics + sound** (UI/Game: 5 pts)
5. ❌ **Create user manual + UML** (Report: 10 pts)
6. ❌ **Export JAR + test** (JAR: 5 pts)
7. ❌ **Record video < 10 min** (Required: infinite pts)

### High Priority Additions:

**1. Expand SPECIFICATION.md Section 4 (Phase 3)**
Add detailed UI component breakdown with JavaFX specifics

**2. Create docs/ folder structure**
```
docs/
├── UserManual.md
├── DeveloperGuide.md
├── uml/
└── javadoc/
```

**3. Add sound requirements to spec**
Include `javafx.media` module and sound effect list

**4. Set up JavaDoc generation**
```gradle
javadoc {
    options.addStringOption('Xdoclint:none', '-quiet')
    destinationDir = file("docs/javadoc")
}
```

**5. Create UML diagram sources**
Use PlantUML files alongside implementation

---

**This checklist should be reviewed weekly during development to track progress toward full score.**
