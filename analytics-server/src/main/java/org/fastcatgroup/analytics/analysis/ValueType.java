package org.fastcatgroup.analytics.analysis;

public interface ValueType<T> {

	public T getValue(String stringValue);

	public T defaultValue();
}
