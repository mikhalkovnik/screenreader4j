package ru.mikhalkovnik.sr4j;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;

import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sun.jna.platform.win32.WinDef.HWND;
import static com.sun.jna.platform.win32.WinDef.RECT;

public class NativeWindow {

    /**
     * Handle Window (HWND) of window
     */
    public final HWND handle;

    /**
     * Title of window
     */
    public final String title;

    /**
     * Position of window
     */
    public final Rectangle pos;

    /**
     * Creates {@link NativeWindow} from handmade properties
     *
     * @param handle handle window (HWND)
     * @param title  title
     * @param pos    position
     */
    public NativeWindow(HWND handle, String title, Rectangle pos) {
        this.handle = handle;
        this.title = title;
        this.pos = pos;
    }

    /**
     * Creates {@link NativeWindow} from {@link DesktopWindow}
     *
     * @param window {@link DesktopWindow}
     */
    public NativeWindow(DesktopWindow window) {
        handle = window.getHWND();
        title = window.getTitle();

        RECT rect = new RECT();
        User32.INSTANCE.GetClientRect(handle, rect);

        int x = rect.left;
        int y = rect.top;
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;

        pos = new Rectangle(x, y, w, h);
    }

    /**
     * Finds all windows that are visible and correspond to the filter
     *
     * @param filter filter for filtering windows
     * @return {@link List} of {@link NativeWindow}
     */
    public static List<NativeWindow> find(Predicate<NativeWindow> filter) {
        return WindowUtils
                .getAllWindows(true)
                .parallelStream()
                .map(NativeWindow::new)
                .filter(window -> !window.title.isEmpty())
                .filter(window -> window.pos.width > 0 && window.pos.height > 0)
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NativeWindow that = (NativeWindow) o;
        return Objects.equals(handle, that.handle) &&
                Objects.equals(title, that.title) &&
                Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handle, title, pos);
    }

    @Override
    public String toString() {
        return "NativeWindow{" +
                "handle=" + handle +
                ", title='" + title + '\'' +
                ", x=" + pos.x +
                ", y=" + pos.y +
                ", width=" + pos.width +
                ", height=" + pos.height +
                '}';
    }
}
