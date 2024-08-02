package com.enthusiasm.plurecore.utils;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CommandUtils {
    public static CommandSyntaxException createException(Message msg) {
        return new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
    }
}