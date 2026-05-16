package io.github.jumpyBirb.gwt;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import io.github.jumpyBirb.Main;
import io.github.jumpyBirb.platform.MobileKeyboardBridge;

/**
 * Launches the GWT application.
 */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        // Resizable application, uses available space in browser with no padding:
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
        cfg.padVertical = 0;
        cfg.padHorizontal = 0;
        return cfg;
        // If you want a fixed size application, comment out the above resizable section,
        // and uncomment below:
        //return new GwtApplicationConfiguration(640, 480);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new Main(new HtmlKeyboardBridge());
    }

    public static native void focusMobileKeyboard() /*-{
        $wnd.focusMobileInput();
    }-*/;

    public static native String getMobileInputText() /*-{
        return $wnd.getMobileInputText();
    }-*/;

    public static native void enableMobileKeyboard() /*-{
        $wnd.enableMobileKeyboard();
    }-*/;

    public static native void disableMobileKeyboard() /*-{
        $wnd.disableMobileKeyboard();
    }-*/;

    private static class HtmlKeyboardBridge
        implements MobileKeyboardBridge {

        @Override
        public void focusKeyboard() {
            focusMobileKeyboard();
        }

        @Override
        public String getInputText() {
            return getMobileInputText();
        }

        @Override
        public void enableKeyboard() {
            enableMobileKeyboard();
        }

        @Override
        public void disableKeyboard() {
            disableMobileKeyboard();
        }
    }

}

