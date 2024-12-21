package com.enthusiasm.plureutils;

import com.enthusiasm.plurecore.permission.AbstractPermissions;

public class PUPermissions extends AbstractPermissions {
    @Override
    public String getPermissionPrefix() {
        return PlureUtilsEntrypoint.MOD_ID;
    }
}
