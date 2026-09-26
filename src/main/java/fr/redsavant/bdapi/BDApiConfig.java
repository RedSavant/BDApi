package fr.redsavant.bdapi;

import fr.redsavant.bdapi.display.DisplayBackend;
import fr.redsavant.bdapi.packet.PacketDisplaySender;

public final class BDApiConfig {

    private final DisplayBackend defaultBackend;
    private final PacketDisplaySender packetSender;

    private BDApiConfig(Builder builder) {
        this.defaultBackend = builder.defaultBackend;
        this.packetSender = builder.packetSender;
    }

    public static BDApiConfig defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public DisplayBackend defaultBackend() {
        return defaultBackend;
    }

    public PacketDisplaySender packetSender() {
        return packetSender;
    }

    public static final class Builder {

        private DisplayBackend defaultBackend = DisplayBackend.PAPER;
        private PacketDisplaySender packetSender;

        public Builder defaultBackend(DisplayBackend defaultBackend) {
            this.defaultBackend = defaultBackend;
            return this;
        }

        public Builder packetSender(PacketDisplaySender packetSender) {
            this.packetSender = packetSender;
            return this;
        }

        public BDApiConfig build() {
            return new BDApiConfig(this);
        }
    }
}
