package com.enthusiasm.plureutils.service.tpa;

import net.minecraft.server.network.ServerPlayerEntity;

public class TpaRequestEntry {
    public final ServerPlayerEntity teleportFrom;
    public final ServerPlayerEntity teleportTo;
    private final ServerPlayerEntity initiator;
    private final ServerPlayerEntity receiver;
    public final boolean toOneSelf;

    public TpaRequestEntry(ServerPlayerEntity teleportFrom, ServerPlayerEntity teleportTo, boolean toOneSelf) {
        this.initiator = teleportFrom;
        this.receiver = teleportTo;
        this.toOneSelf = toOneSelf;

        if (toOneSelf) {
            this.teleportFrom = receiver;
            this.teleportTo = initiator;
        } else {
            this.teleportFrom = initiator;
            this.teleportTo = receiver;
        }
    }

    public boolean similarTo(TpaRequestEntry other) {
        return (this.teleportFrom.equals(other.teleportFrom) && this.teleportFrom.equals(other.teleportTo)) ||
                (this.teleportFrom.equals(other.teleportTo) && this.teleportFrom.equals(other.teleportFrom));
    }
}
