package game.engine;

import game.model.GameState;
import game.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SealLifecycleService {
    public void updateAtEndOfTurn(GameState state) {
        int currentTurn = state.getTotalTurnCount();
        List<Position> toUnseal = new ArrayList<>();

        // ค้นหาช่องที่หมดอายุแล้ว
        for (Map.Entry<Position, Integer> entry : state.getSealExpirations().entrySet()) {
            if (currentTurn >= entry.getValue()) {
                toUnseal.add(entry.getKey());
            }
        }

        // ปลดล็อกกลับเป็นช่องว่าง
        for (Position pos : toUnseal) {
            state.unsealCell(pos);
        }
    }
}