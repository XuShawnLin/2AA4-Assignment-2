package A2;

/**
 * Class representing a die in the game.
 */
public class Die {
	private int value;

	/**
	 * Constructor for Die class.
	 */
	public Die() {
		this.value = 1;
	}

	/**
	 * Rolls the die and returns the result.
	 * @return The result of the die roll.
	 */
	public int Roll() {
		this.value = (int)(Math.random() * 6) + 1;
		return this.value;
	}

	public int getValue() {
		return value;
	}
}
