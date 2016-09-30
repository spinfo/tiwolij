package tiwolij.domain;

public class BaseEntity {

	public Object get(String field) {
		Object result = null;

		try {
			result = getClass().getDeclaredField(field).get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
