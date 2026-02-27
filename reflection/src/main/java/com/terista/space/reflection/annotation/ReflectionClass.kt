package com.terista.space.reflection.annotation

/**
 * Marks a class for reflection stub generation.
 * The annotation processor will generate a BR (Black Reflection) class for this target.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ReflectionClass(
    val targetClass: String,
    val stubName: String = "",
    val generateFields: Boolean = true,
    val generateMethods: Boolean = true
)
