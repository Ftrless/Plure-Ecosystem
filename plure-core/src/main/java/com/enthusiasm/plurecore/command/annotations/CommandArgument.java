package com.enthusiasm.plurecore.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.enthusiasm.plurecore.command.types.ArgumentTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommandArgument {
    String name();
    ArgumentTypes type();
}
