package fr.redsavant.bdapi.packet;

import java.util.concurrent.atomic.AtomicInteger;

public final class PacketEntityIdAllocator {

    private static final int DEFAULT_BASE = 1_000_000_000;

    private final int base;
    private final AtomicInteger counter;

    public PacketEntityIdAllocator() {
        this(DEFAULT_BASE);
    }

    public PacketEntityIdAllocator(int base) {
        this.base = base;
        this.counter = new AtomicInteger(base);
    }

    /**
     * Allocates the next entity id.
     *
     * <p>Ids are handed out in increasing order and never recycled: a reused id would make the
     * client apply the updates and the destroy packets of a new display to an older one that is
     * still tracked.
     *
     * @return a fresh entity id
     * @throws IllegalStateException if the range is exhausted
     */
    public int next() {
        return counter.updateAndGet(current -> {
            if (current == Integer.MAX_VALUE) {
                throw new IllegalStateException("Packet entity id range starting at " + base + " is exhausted");
            }
            return current + 1;
        });
    }

    /**
     * @return how many ids can still be allocated
     */
    public int remaining() {
        return Integer.MAX_VALUE - base - counter.get();
    }
}
