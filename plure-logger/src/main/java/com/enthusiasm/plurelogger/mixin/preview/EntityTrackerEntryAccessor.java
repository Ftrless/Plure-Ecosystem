package com.enthusiasm.plurelogger.mixin.preview;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;

@Mixin(EntityTrackerEntry.class)
public interface EntityTrackerEntryAccessor {
    @Accessor
    Entity getEntity();
}
