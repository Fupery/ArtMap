package me.Fupery.ArtMap.Protocol.In.Packet;

public abstract class ArtistPacket {

    private final PacketType type;

    protected ArtistPacket(PacketType type) {
        this.type = type;
    }

    public PacketType getType() {
        return type;
    }

    public static class PacketLook extends ArtistPacket {
        private final float pitch;
        private final float yaw;

        public PacketLook(float yaw, float pitch) {
            super(PacketType.LOOK);
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
        public PacketArmSwing() {
            super(PacketType.ARM_ANIMATION);
        }
    }

    public static class PacketInteract extends ArtistPacket {
        private final InteractType interaction;

        public PacketInteract(InteractType interaction) {
            super(PacketType.INTERACT);
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

