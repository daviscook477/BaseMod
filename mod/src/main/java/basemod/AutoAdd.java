package basemod;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.clapper.util.classutil.*;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class AutoAdd
{
	private ClassFinder finder;
	private List<ClassFilter> filters;
	private ClassPool pool;
	private Boolean defaultSeenOverride = null;

	public AutoAdd(String modID)
	{
		finder = new ClassFinder();
		try {
			for (ModInfo info : Loader.MODINFOS) {
				if (info != null && modID != null && modID.equals(info.ID) && info.jarURL != null) {
					finder.add(new File(info.jarURL.toURI()));
				}
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		filters = new ArrayList<>();
		pool = Loader.getClassPool();
	}

	public AutoAdd overrideClassPool(ClassPool pool)
	{
		this.pool = pool;
		return this;
	}

	public AutoAdd setDefaultSeen(boolean seen)
	{
		defaultSeenOverride = seen;
		return this;
	}

	public AutoAdd filter(ClassFilter filter)
	{
		filters.add(filter);
		return this;
	}

	public AutoAdd packageFilter(String packageName)
	{
		return filter(new PackageFilter(packageName));
	}

	public AutoAdd packageFilter(Class<?> packageClass)
	{
		return filter(new PackageFilter(packageClass));
	}

	public AutoAdd notPackageFilter(String packageName) {
		return filter(new NotPackageFilter(packageName));
	}

	public AutoAdd notPackageFilter(Class<?> packageClass) {
		return filter(new NotPackageFilter(packageClass));
	}

	public <T> Collection<CtClass> findClasses(Class<T> type)
	{
		try {
			List<ClassFilter> tmp = new ArrayList<>();
			tmp.addAll(Arrays.asList(
					new NotClassFilter(new InterfaceOnlyClassFilter()),
					new NotClassFilter(new AbstractClassFilter()),
					new ClassModifiersClassFilter(Modifier.PUBLIC)
			));
			tmp.addAll(filters);
			ClassFilter filter = new AndClassFilter(tmp.toArray(new ClassFilter[0]));
			Collection<ClassInfo> foundClasses = new ArrayList<>();
			finder.findClasses(foundClasses, filter);

			Collection<CtClass> ret = new ArrayList<>();
			for (ClassInfo classInfo : foundClasses) {
				CtClass ctClass = pool.get(classInfo.getClassName());

				CtClass ctSuperClass = ctClass;
				do {
					if (ctSuperClass.getName().equals(type.getName())) {
						ret.add(ctClass);
						break;
					}
					ctSuperClass = ctSuperClass.getSuperclass();
				} while (ctSuperClass != null);
			}

			return ret;
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void any(Class<T> type, BiConsumer<Info, T> add)
	{
		try {
			Collection<CtClass> foundClasses = findClasses(type);

			for (CtClass ctClass : foundClasses) {
				Info info = new Info(ctClass);
				if (info.ignore) {
					continue;
				}

				T t = type.cast(pool.getClassLoader().loadClass(ctClass.getName()).newInstance());
				add.accept(info, t);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void cards()
	{
		any(
				AbstractCard.class,
				(info, card) -> {
					BaseMod.addCard(card);
					if (info.seen) {
						UnlockTracker.unlockCard(card.cardID);
					}
				}
		);
	}

	public class Info
	{
		private static final boolean DEFAULT_IGNORE = false;
		private static final boolean DEFAULT_SEEN = false;

		private Info(CtClass ctClass)
		{
			ignore = ctClass.hasAnnotation(Ignore.class) || DEFAULT_IGNORE;
			if (ctClass.hasAnnotation(NotSeen.class)) {
				seen = false;
			} else if (ctClass.hasAnnotation(Seen.class)) {
				seen = true;
			} else if (defaultSeenOverride != null) {
				seen = defaultSeenOverride;
			} else {
				seen = DEFAULT_SEEN;
			}
		}

		public final boolean ignore;
		public final boolean seen;
	}

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Ignore {}

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Seen {}

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NotSeen {}

	public static class PackageFilter implements ClassFilter
	{
		private final String packageName;

		public PackageFilter(String pkgName)
		{
			packageName = pkgName;
		}

		public PackageFilter(Class<?> cls)
		{
			this(cls.getPackage().getName());
		}

		@Override
		public boolean accept(ClassInfo classInfo, ClassFinder classFinder)
		{
			return classInfo.getClassName().startsWith(packageName);
		}
	}

	public static class NotPackageFilter extends PackageFilter {
		public NotPackageFilter(String pkgName) {
			super(pkgName);
		}

		public NotPackageFilter(Class<?> cls) {
			super(cls);
		}

		@Override
		public boolean accept(ClassInfo classInfo, ClassFinder classFinder) {
			return !super.accept(classInfo, classFinder);
		}
	}
}
