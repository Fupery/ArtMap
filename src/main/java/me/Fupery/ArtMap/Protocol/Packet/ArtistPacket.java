package me.Fupery.ArtMap.Protocol.Packet;

public class ArtistPacket {

    ArtistPacket(Object packet, PacketType type) {
        Object packet1 = packet;
        PacketType type1 = type;
    }

    public static class PacketLook extends ArtistPacket {
        private final float pitch;
        private final float yaw;

        public PacketLook(Object packet, PacketType type, float yaw, float pitch) {
            super(packet, type);
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float getPitch() {
            return pitch;
        }

        public float getYaw() {
            return yaw;
        }
    }

    public static class PacketArmSwing extends ArtistPacket {

        public PacketArmSwing(Object packet, PacketType type) {
            super(packet, type);
        }
    }

    public static class PacketInteract extends ArtistPacket {
        private final InteractType interaction;

        public PacketInteract(Object packet, PacketType type, InteractType interaction) {
            super(packet, type);
            this.interaction = interaction;
        }

        public InteractType getInteraction() {
            return interaction;
        }

        public enum InteractType {
            INTERACT, ATTACK
        }
    }
}

