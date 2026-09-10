package fr.redsavant.bdapi.packet;

import java.util.concurrent.atomic.AtomicInteger;

public final class PacketEntityIdAllocator {

    private static final int DEFAULT_BASE = 2_000_000_000;

    private final AtomicInteger counter;

    public PacketEntityIdAllocator() {
        this(DEFAULT_BASE);
    }

    public PacketEntityIdAllocator(int base) {
        this.counter = new AtomicInteger(base);
    }

    public int next() {
        return counter.incrementAndGet();
    }
}
