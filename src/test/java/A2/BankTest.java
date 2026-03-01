package test.java.A2;

import org.junit.jupiter.api.Test;

import main.java.A2.Bank;
import main.java.A2.ResourceType;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    @Test
    void constructor_add19ofEachResource() {
        Bank bank = new Bank();
        for (ResourceType item : ResourceType.values()) {
            assertEquals(19, bank.resourceList.get(item));
        }
    }

    @Test
    void hasResource_trueWhenEnough() {
        Bank bank = new Bank();
        assertTrue(bank.hasResource(ResourceType.BRICK, 5));
    }

    @Test
    void hasResource_falseWhenNotEnough() {
        Bank bank = new Bank();
        assertFalse(bank.hasResource(ResourceType.ORE, 25));
    }

    @Test
    void giveResource_decreasesWhenAvailable() {
        Bank bank = new Bank();
        assertTrue(bank.giveResource(ResourceType.WOOL, 3));
        assertEquals(16, bank.resourceList.get(ResourceType.WOOL));
    }

    @Test
    void giveResource_failsWhenNotavailable() {
        Bank bank = new Bank();
        assertFalse(bank.giveResource(ResourceType.GRAIN, 50));
        assertEquals(19, bank.resourceList.get(ResourceType.GRAIN));
    }
}
