package fr.redsavant.bdapi.packet;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketEntityIdAllocatorTest {

    @Test
    void producesUniqueIds() {
        PacketEntityIdAllocator allocator = new PacketEntityIdAllocator(0);
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            assertTrue(seen.add(allocator.next()));
        }
        assertEquals(1000, seen.size());
    }

    @Test
    void incrementsMonotonically() {
        PacketEntityIdAllocator allocator = new PacketEntityIdAllocator(10);
        assertEquals(11, allocator.next());
        assertEquals(12, allocator.next());
    }

    @Test
    void failsInsteadOfHandingOutADuplicateWhenExhausted() {
        PacketEntityIdAllocator allocator = new PacketEntityIdAllocator(Integer.MAX_VALUE - 1);

        assertEquals(Integer.MAX_VALUE, allocator.next());

        // Recycling the id would make the client apply the packets of a new display to the
        // previous one, so the allocator gives up instead.
        assertThrows(IllegalStateException.class, allocator::next);
        assertThrows(IllegalStateException.class, allocator::next);
    }

    @Test
    void defaultBaseLeavesTheServerEntityIdRangeAlone() {
        PacketEntityIdAllocator allocator = new PacketEntityIdAllocator();
        int before = allocator.remaining();

        int id = allocator.next();

        assertTrue(id >= 1_000_000_000);
        assertEquals(before - 1, allocator.remaining());
    }
}
