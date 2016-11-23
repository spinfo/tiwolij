package tiwolij.util;

public class DataContainer {

	protected Object label;
	protected Object data;

	public DataContainer(Object label, Object data) {
		this.label = label;
		this.data = data;
	}

	public Object get(String field) {
		Object result = null;

		try {
			result = getClass().getDeclaredField(field).get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public void set(String field, String value) {
		try {
			getClass().getDeclaredField(field).set(this, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
