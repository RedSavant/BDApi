package fr.redsavant.bdapi.support;

import fr.redsavant.bdapi.display.PacketDisplayHandle;
import fr.redsavant.bdapi.packet.PacketDisplaySender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RecordingPacketDisplaySender implements PacketDisplaySender {

    public enum Type {
        SPAWN, METADATA, TELEPORT, DESTROY
    }

    public record Call(Type type, UUID viewer, int entityId) {
    }

    public final List<Call> calls = new ArrayList<>();

    @Override
    public void spawn(UUID viewer, PacketDisplayHandle display) {
        calls.add(new Call(Type.SPAWN, viewer, display.entityId()));
    }

    @Override
    public void metadata(UUID viewer, PacketDisplayHandle display) {
        calls.add(new Call(Type.METADATA, viewer, display.entityId()));
    }

    @Override
    public void teleport(UUID viewer, PacketDisplayHandle display) {
        calls.add(new Call(Type.TELEPORT, viewer, display.entityId()));
    }

    @Override
    public void destroy(UUID viewer, PacketDisplayHandle display) {
        calls.add(new Call(Type.DESTROY, viewer, display.entityId()));
    }

    public long count(Type type) {
        return calls.stream().filter(call -> call.type() == type).count();
    }

    public long countFor(Type type, UUID viewer) {
        return calls.stream().filter(call -> call.type() == type && call.viewer().equals(viewer)).count();
    }
}
