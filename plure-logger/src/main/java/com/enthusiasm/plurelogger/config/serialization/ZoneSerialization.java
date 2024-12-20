package com.enthusiasm.plurelogger.config.serialization;

import java.time.ZoneId;

import com.enthusiasm.plurecore.config.serialization.IDataSerializer;

public class ZoneSerialization implements IDataSerializer<ZoneId, String> {
    @Override
    public String serialize(ZoneId value) {
        return value.getId();
    }

    @Override
    public ZoneId deserialize(String value) {
        return ZoneId.of(value);
    }
}
