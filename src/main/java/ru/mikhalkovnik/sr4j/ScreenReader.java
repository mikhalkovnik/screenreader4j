package ru.mikhalkovnik.sr4j;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;

import java.util.Objects;

import static com.sun.jna.platform.win32.WinDef.*;
import static com.sun.jna.platform.win32.WinGDI.*;
import static com.sun.jna.platform.win32.WinGDI.BI_RGB;
import static com.sun.jna.platform.win32.WinGDI.DIB_RGB_COLORS;

public class ScreenReader {

    /**
     * Instance of {@link User32}
     */
    private static final User32 USER_32 = User32.INSTANCE;

    /**
     * Instance of {@link GDI32}
     */
    private static final GDI32 GDI_32 = GDI32.INSTANCE;

    /**
     * Count of {@link HBITMAP} BI planes
     */
    private static final int BMI_BI_PLANES = 1;

    /**
     * {@link HBITMAP} BI bit count
     */
    private static final int BMI_BI_BIT_COUNT = 32;

    /**
     * {@link HBITMAP} BI compression strategy
     */
    private static final int BMI_BI_COMPRESSION = BI_RGB;

    /**
     * Handle Window (HWND) of captured window
     */
    private HWND handle;

    /**
     * Handle Device Context (HDC) of captured window
     */
    private HDC hdcWindow;

    /**
     * Handle Device Context (HDC) of memory device context
     */
    private HDC hdcMemDC;

    /**
     * Width of captured window
     */
    private int width;

    /**
     * Height of captured window
     */
    private int height;

    /**
     * {@link HBITMAP} of captured window
     */
    private HBITMAP hbitmap;

    /**
     * {@link BITMAPINFO} of {@link HBITMAP}
     */
    private BITMAPINFO bmi;

    /**
     * {@link MemoryBuffer} for reusing memory
     */
    private MemoryBuffer memoryBuffer;

    /**
     * Creates empty {@link ScreenReader} that not targeted to any {@link NativeWindow}
     */
    public ScreenReader() {
        bmi = new BITMAPINFO();
        bmi.bmiHeader.biPlanes = BMI_BI_PLANES;
        bmi.bmiHeader.biBitCount = BMI_BI_BIT_COUNT;
        bmi.bmiHeader.biCompression = BMI_BI_COMPRESSION;
    }

    /**
     * Targets {@link ScreenReader} to {@link NativeWindow}
     *
     * @param window {@link NativeWindow} to target
     */
    public void target(NativeWindow window) {
        Objects.requireNonNull(window, "window must no be null");

        handle = window.handle;

        hdcWindow = USER_32.GetDC(handle);
        hdcMemDC = GDI_32.CreateCompatibleDC(hdcWindow);

        width = window.pos.width;
        height = window.pos.height;

        hbitmap = GDI_32.CreateCompatibleBitmap(hdcWindow, width, height);

        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;

        memoryBuffer = new MemoryBuffer(width * height);
    }

    /**
     * Reads pixels of target window to buffer
     *
     * @see ScreenReader#getBuffer()
     */
    public void capture() {
        WinNT.HANDLE hOld = GDI_32.SelectObject(hdcMemDC, hbitmap);
        GDI_32.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, 0x00CC0020);
        GDI_32.SelectObject(hdcMemDC, hOld);
        GDI_32.GetDIBits(hdcWindow, hbitmap, 0, height, memoryBuffer, bmi, DIB_RGB_COLORS);
        memoryBuffer.read();
    }

    /**
     * Releases native allocated memory
     */
    public void release() {
        GDI_32.DeleteObject(hbitmap);
        USER_32.ReleaseDC(handle, hdcWindow);
    }

    /**
     * Provides an int array that contains pixel data of {@link NativeWindow}
     *
     * @return int array (all of that arrays are unique for another {@link ScreenReader#capture()} method call)
     */
    public int[] getBuffer() {
        return memoryBuffer.array;
    }
}
