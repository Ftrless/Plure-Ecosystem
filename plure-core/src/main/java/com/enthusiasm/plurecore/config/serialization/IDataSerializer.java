package com.enthusiasm.plurecore.config.serialization;

public interface IDataSerializer<T, U> {
    U serialize(T value);

    T deserialize(U value);
}
