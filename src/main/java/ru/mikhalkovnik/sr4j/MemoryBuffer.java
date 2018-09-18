package ru.mikhalkovnik.sr4j;

import com.sun.jna.Memory;

public class MemoryBuffer extends Memory {

    /**
     * An int array to be used for data storage
     */
    public final int[] array;

    /**
     * Creates and allocates {@link MemoryBuffer#array}
     * @param size size of {@link MemoryBuffer#array}
     */
    public MemoryBuffer(int size) {
        super(size * 4);

        array = new int[size];
    }

    /**
     * Reads data into {@link MemoryBuffer#array}
     */
    public void read() {
        super.read(0, array, 0, array.length);
    }
}
