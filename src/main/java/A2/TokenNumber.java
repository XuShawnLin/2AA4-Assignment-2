package A2;

/**
 * Enumeration representing a set of token numbers.
 */
public enum TokenNumber {
	T2(2),
	T3(3),
	T4(4),
	T5(5),
	T6(6),
	T8(8),
	T9(9),
	T10(10),
	T11(11),
	T12(12);

	private final int value;

	TokenNumber(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
