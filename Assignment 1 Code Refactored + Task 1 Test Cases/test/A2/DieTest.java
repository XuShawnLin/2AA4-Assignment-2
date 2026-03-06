package A2;

import org.junit.jupiter.api.Test;

import A2.Die;

import static org.junit.jupiter.api.Assertions.*;

public class DieTest {

    @Test
    void roll_isBetween1And6() {
        Die die = new Die();
        for (int i = 0; i < 200; i++) {
            int value = die.Roll();
            assertTrue(value >= 1 && value <= 6);
        }
    }

    @Test
    void getValue_matchesLastRoll() {
        Die die = new Die();
        int value = die.Roll();
        assertEquals(value, die.getValue());
    }
}
