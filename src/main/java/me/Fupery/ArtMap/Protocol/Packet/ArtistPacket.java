package me.Fupery.ArtMap.Protocol.Packet;

public class ArtistPacket {

    public static class PacketLook extends ArtistPacket {
        private final float pitch;
        private final float yaw;

        public PacketLook(float yaw, float pitch) {
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
    }

    public static class PacketInteract extends ArtistPacket {
        private final InteractType interaction;

        public PacketInteract(InteractType interaction) {
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

