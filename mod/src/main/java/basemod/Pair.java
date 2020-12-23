package basemod;

import java.util.Objects;

public class Pair<K, V>
{
	private final K key;
	private final V value;

	public Pair(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	public K getKey()
	{
		return key;
	}

	public V getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return key + "=" + value;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Pair)) {
			return false;
		}

		Pair<K, V> other = (Pair<K, V>) o;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(key, value);
	}
}
