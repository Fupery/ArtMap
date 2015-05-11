package me.Fupery.Artiste.Command.Utils;

public class Conditions {

	private Condition[] conditions;
	private int count;

	public Conditions() {

		count = 0;
		conditions = new Condition[5];
	}

	public void add(boolean condition, String error) {

		conditions[count] = new Condition(condition, error);
		count++;

		if (count == conditions.length) {

			Condition[] c = new Condition[conditions.length + 5];

			System.arraycopy(conditions, 0, c, 0, conditions.length);

			conditions = c;
		}
	}

	public String evaluate() {

		for (Condition c : conditions)

			if (!c.getCondition())

				return c.getError();

		return null;
	}

	public String[] get() {

		String[] a = new String[conditions.length];

		for (int i = 0; i < conditions.length; i++)

			if (conditions[i] != null && !conditions[i].getCondition())

				a[i] = conditions[i].getError();

		return a;
	}

	public void clear() {

		conditions = null;
	}

	class Condition {

		private boolean condition;
		private String error;

		public Condition(boolean condition, String error) {

			this.condition = condition;
			this.error = error;
		}

		public boolean getCondition() {
			return condition;
		}

		public String getError() {
			return error;
		}
	}
}
