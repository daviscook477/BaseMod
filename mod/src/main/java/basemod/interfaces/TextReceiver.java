package basemod.interfaces;

public interface TextReceiver {
    String getCurrentText();
    void setText(String updatedText);
    boolean isDone();

    //If you need additional input for extra functionality.
    default boolean onKeyDown(int keycode) { return false; }
    default boolean onKeyUp(int keycode) { return false; }

    //Return true to absorb the input. Otherwise, these characters will be added to the text.
    default boolean onPushEnter() { return true; }
    default boolean onPushTab() { return true; }
    default boolean onPushBackspace() { return false; }

    boolean acceptCharacter(char c); //recommended to return "font.getData().hasGlyph(character)" to make sure it's a character that will be displayed properly.
    //Can also be used to just process certain characters as other input.
    default String getAppendedText(char c) { return String.valueOf(c); } //allows changing what is actually typed when characters are input

    default int getCharLimit() { return -1; } //no limit
}