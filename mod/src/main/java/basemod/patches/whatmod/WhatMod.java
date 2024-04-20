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
import java.util.LinkedHashSet;
import java.util.Set;

public class WhatMod
{
	public static boolean enabled = true;

	static void renderModTooltip(SpriteBatch sb, Class<?>... cls)
	{
		renderModTooltip(sb, Settings.WIDTH / 2f + 340.0f * Settings.scale, Settings.HEIGHT / 2f + 160f * Settings.yScale, cls);
	}

	static void renderModTooltip(SpriteBatch sb, Class<?> cls, float x, float y)
	{
		renderModTooltip(sb, x, y, cls);
	}

	private static String getBody(Class<?>... cls)
	{
		Set<String> modNames = new LinkedHashSet<>();
		for (Class<?> c : cls) {
			String name = findModName(c);
			if (name == null) {
				name = "Not modded content";
			}
			modNames.add(name);
		}
		return String.join(" NL + ", modNames);
	}

	static void renderModTooltip(SpriteBatch sb, float x, float y, Class<?>... cls)
	{
		float BODY_TEXT_WIDTH = 280.0F * Settings.scale;
		float TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;

		String title = "What mod is this from?";

		try {
			String body = getBody(cls);

			Field textHeight = TipHelper.class.getDeclaredField("textHeight");
			textHeight.setAccessible(true);

			Method renderTipBox = TipHelper.class.getDeclaredMethod("renderTipBox", float.class, float.class, SpriteBatch.class, String.class, String.class);
			renderTipBox.setAccessible(true);

			float height = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, body, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
			textHeight.setFloat(null, height - 7.0f * Settings.scale);

			renderTipBox.invoke(null, x, y, sb, title, body);
		} catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	static void renderModTooltipBottomLeft(SpriteBatch sb, Class<?>... cls)
	{
		renderModTooltipBottomLeft(sb, Settings.WIDTH / 2f + 340.0f * Settings.scale, Settings.HEIGHT / 2f + 160f * Settings.yScale, cls);
	}

	static void renderModTooltipBottomLeft(SpriteBatch sb, float x, float y, Class<?>... cls)
	{
		float BODY_TEXT_WIDTH = 280.0F * Settings.scale;
		float TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;

		String body = getBody(cls);

		float height = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, body, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);

		renderModTooltip(sb, x, y + height, cls);
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
