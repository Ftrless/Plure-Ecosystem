package com.enthusiasm.plurecore.data;

public record DataHolderEntry(String userUUID, DataHolder<?> dataHolder, boolean markedForUpdate) {}
