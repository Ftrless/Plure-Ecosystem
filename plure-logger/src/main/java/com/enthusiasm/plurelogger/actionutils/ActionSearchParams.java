package com.enthusiasm.plurelogger.actionutils;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.Getter;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;

import com.enthusiasm.plurelogger.config.ConfigWrapper;
import com.enthusiasm.plurelogger.config.PLConfig;
import com.enthusiasm.plurelogger.utils.Negatable;

@Getter
public class ActionSearchParams {
    private final BlockBox bounds;
    private final Instant before;
    private final Instant after;
    private final Boolean rolledBack;
    private final Set<Negatable<String>> actions;
    private final Set<Negatable<Identifier>> objects;
    private final Set<Negatable<String>> sourceNames;
    private final Set<Negatable<UUID>> sourcePlayerIds;
    private final Set<Negatable<Identifier>> worlds;

    private ActionSearchParams(Builder builder) {
        this.bounds = builder.bounds;
        this.before = builder.before;
        this.after = builder.after;
        this.rolledBack = builder.rolledBack;
        this.actions = builder.actions;
        this.objects = builder.objects;
        this.sourceNames = builder.sourceNames;
        this.sourcePlayerIds = builder.sourcePlayerIds;
        this.worlds = builder.worlds;
    }

    public void ensureSpecific() throws CommandSyntaxException {
        PLConfig config = ConfigWrapper.getConfig();

        if (bounds == null) {
            throw new SimpleCommandExceptionType(Text.translatable("error.unspecific.range")).create();
        }

        int range = (Math.max(bounds.getBlockCountX(), Math.max(bounds.getBlockCountY(), bounds.getBlockCountZ())) + 1) / 2;
        if (range > config.maxRange) {
            throw new SimpleCommandExceptionType(
                    Text.translatable("error.unspecific.range_to_big", config.maxRange)
            ).create();
        }

        if (sourceNames == null && sourcePlayerIds == null && after == null && before == null) {
            throw new SimpleCommandExceptionType(Text.translatable("error.unspecific.source_or_time")).create();
        }
    }

    public static Builder build() {
        return new Builder();
    }

    @Getter
    public static class Builder {
        private BlockBox bounds = null;
        private Instant before = null;
        private Instant after = null;
        private Boolean rolledBack = null;
        private Set<Negatable<String>> actions = new HashSet<>();
        private Set<Negatable<Identifier>> objects = new HashSet<>();
        private Set<Negatable<String>> sourceNames = new HashSet<>();
        private Set<Negatable<UUID>> sourcePlayerIds = new HashSet<>();
        private Set<Negatable<Identifier>> worlds = new HashSet<>();

        public Builder setBounds(BlockBox bounds) {
            this.bounds = bounds;
            return this;
        }

        public Builder setBefore(Instant before) {
            this.before = before;
            return this;
        }

        public Builder setAfter(Instant after) {
            this.after = after;
            return this;
        }

        public Builder setRolledBack(Boolean rolledBack) {
            this.rolledBack = rolledBack;
            return this;
        }

        public Builder setActions(Set<Negatable<String>> actions) {
            this.actions = actions;
            return this;
        }

        public Builder setObjects(Set<Negatable<Identifier>> objects) {
            this.objects = objects;
            return this;
        }

        public Builder setSourceNames(Set<Negatable<String>> sourceNames) {
            this.sourceNames = sourceNames;
            return this;
        }

        public Builder setSourcePlayerIds(Set<Negatable<UUID>> sourcePlayerIds) {
            this.sourcePlayerIds = sourcePlayerIds;
            return this;
        }

        public Builder setWorlds(Set<Negatable<Identifier>> worlds) {
            this.worlds = worlds;
            return this;
        }

        public ActionSearchParams build() {
            return new ActionSearchParams(this);
        }
    }
}
