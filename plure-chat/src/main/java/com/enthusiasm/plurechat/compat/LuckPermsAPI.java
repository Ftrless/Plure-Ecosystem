package com.enthusiasm.plurechat.compat;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.network.ServerPlayerEntity;

public class LuckPermsAPI {
    public static final boolean LOADED = FabricLoaderImpl.INSTANCE.isModLoaded("luckperms");

    public static String getPrefix(ServerPlayerEntity player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);

        return user.getCachedData().getMetaData().getPrefix();
    }
}
