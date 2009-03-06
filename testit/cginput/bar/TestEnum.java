package bar;

public enum TestEnum {
	ONE(-10, "minus ten"), TWO(0, "zero"), THREE(10, "plus ten");

	private int value;

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the sValue
	 */
	public String getSValue() {
		return sValue;
	}

	private String sValue;

	private TestEnum(int val, String sVal) {
		value = val;
		sValue = sVal;
	}

	public boolean isFirstEnumValue() {
		int aaa = 2;
		while (aaa < 10) {
			aaa *= aaa;
		}
		if (ordinal() == 0) {
			return true;
		} else {
			return false;
		}
	}

}
