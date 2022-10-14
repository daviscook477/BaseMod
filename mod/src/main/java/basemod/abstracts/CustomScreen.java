package basemod.abstracts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public abstract class CustomScreen
{
	private Method openCache = null;

	public abstract AbstractDungeon.CurrentScreen curScreen();

	public void open(Object... args)
	{
		if (openCache == null) {
			Optional<Method> opt = Arrays.stream(this.getClass().getDeclaredMethods())
					.filter(m -> "open".equals(m.getName()))
					.filter(m -> !Arrays.equals(new Object[] { Object[].class }, m.getParameterTypes()))
					.findFirst();
			opt.ifPresent(method -> openCache = method);
			if (openCache == null) {
				throw new RuntimeException("Unable to find CustomScreen open method");
			}
			openCache.setAccessible(true);
		}

		try {
			openCache.invoke(this, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public abstract void reopen();
	public abstract void close();

	public abstract void update();
	public abstract void render(SpriteBatch sb);

	public boolean allowOpenDeck()
	{
		return false;
	}
	public boolean allowOpenMap()
	{
		return false;
	}

	public abstract void openingSettings();
	public void openingDeck() {}
	public void openingMap() {}
}
