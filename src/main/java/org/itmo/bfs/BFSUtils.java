package org.itmo.bfs;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public final class BFSUtils {

    private BFSUtils() {}


    public static AtomicBoolean[] createAtomicBooleanArray(int size) {
        return IntStream.range(0, size).mapToObj(i -> new AtomicBoolean(false)).toArray(AtomicBoolean[]::new);
    }
}
