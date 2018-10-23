package basemod;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import java.awt.event.KeyEvent;

public class ConsoleInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.BACKSPACE: {
			if (DevConsole.currentText.length() > 0)
			{
				DevConsole.currentText = DevConsole.currentText.substring(0, DevConsole.currentText.length()-1);
				if (AutoComplete.enabled) {
					AutoComplete.suggest(true);
				}
				return true;
			}
			return false;
		}
		case Keys.ENTER: {
			if (DevConsole.currentText.length() > 0) {
				DevConsole.execute();
				if (AutoComplete.enabled) {
					AutoComplete.reset();
					AutoComplete.suggest(false);
				}
			}
			return true;
		}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// For some reason the validation procedure thinks uppercase D is invalid
		// uppercase K and H as well
		if (character == 'D' || character == 'K' || character == 'H') {
			DevConsole.currentText += character;

			if (AutoComplete.enabled) {
				AutoComplete.suggest(false);
			}
			return true;
		}

		Character.UnicodeBlock block = Character.UnicodeBlock.of(character);

		boolean badBlock = (block == null || block == Character.UnicodeBlock.SPECIALS);
		boolean isToggle = (character == DevConsole.toggleKey);
		boolean isControl = (Character.isISOControl(character) || character == KeyEvent.CHAR_UNDEFINED);
		if (badBlock || isToggle || isControl) {
			return false;
		}

		DevConsole.currentText += character;

		if (AutoComplete.enabled) {
			AutoComplete.suggest(false);
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}