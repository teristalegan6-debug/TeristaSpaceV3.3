package com.terista.space.reflection.annotation

/**
 * Marks a method for reflection stub generation.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ReflectionMethod(
    val targetMethod: String = "",
    val parameterTypes: Array<String> = [],
    val returnType: String = "void"
)
