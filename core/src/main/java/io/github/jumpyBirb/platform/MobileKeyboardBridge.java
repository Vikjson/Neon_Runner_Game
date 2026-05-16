package io.github.jumpyBirb.platform;

public interface MobileKeyboardBridge {

    void focusKeyboard();
    String getInputText();

    void enableKeyboard();
    void disableKeyboard();

}
