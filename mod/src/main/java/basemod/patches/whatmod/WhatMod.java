package basemod.patches.whatmod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class WhatMod
{
	public static boolean enabled = true;

	private static final float BODY_TEXT_WIDTH = 280.0F * Settings.scale;
	private static final float TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;

	static void renderModTooltip(SpriteBatch sb, Class<?> cls)
	{
		renderModTooltip(sb, cls, Settings.WIDTH / 2f + 340.0f * Settings.scale, 700.0f * Settings.scale);
	}

	static void renderModTooltip(SpriteBatch sb, Class<?> cls, float x, float y)
	{
		String title = "What mod is this from?";
		String body;

		try {
			body = findModName(cls);
			if (body == null) {
				body = "Not modded content";
			}

			Field textHeight = TipHelper.class.getDeclaredField("textHeight");
			textHeight.setAccessible(true);

			Method renderTipBox = TipHelper.class.getDeclaredMethod("renderTipBox", float.class, float.class, SpriteBatch.class, String.class, String.class);
			renderTipBox.setAccessible(true);

			textHeight.setFloat(null, -FontHelper.getSmartHeight(FontHelper.tipBodyFont, body, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale);

			renderTipBox.invoke(null, x, y, sb, title, body);
		} catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	static URL findModURL(Class<?> cls)
	{
		URL locationURL = cls.getProtectionDomain().getCodeSource().getLocation();

		if (locationURL == null) {
			try {
				ClassPool pool = Loader.getClassPool();
				pool.childFirstLookup = true;
				CtClass ctCls = pool.get(cls.getName());
				String url = ctCls.getURL().getFile();
				int i = url.lastIndexOf('!');
				if (i >= 0) {
					url = url.substring(0, i);
				}
				if (url.endsWith(".jar")) {
					locationURL = new URL(url);
				}
			} catch (NotFoundException | MalformedURLException ignored) {
				return null;
			}
		}

		return locationURL;
	}

	public static String findModName(Class<?> cls)
	{
		URL locationURL = findModURL(cls);

		if (locationURL == null) {
			return "Unknown";
		}

		try {
			if (locationURL.equals(new File(Loader.STS_JAR).toURI().toURL())) {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "Unknown";
		}

		for (ModInfo modInfo : Loader.MODINFOS) {
			if (locationURL.equals(modInfo.jarURL)) {
				return modInfo.Name;
			}
		}

		return "Unknown";
	}

	public static String findModID(Class<?> cls)
	{
		URL locationURL = findModURL(cls);

		if (locationURL == null) {
			return "<unknown>";
		}

		try {
			if (locationURL.equals(new File(Loader.STS_JAR).toURI().toURL())) {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "<unknown>";
		}

		for (ModInfo modInfo : Loader.MODINFOS) {
			if (locationURL.equals(modInfo.jarURL)) {
				return modInfo.ID;
			}
		}

		return "<unknown>";
	}
}
