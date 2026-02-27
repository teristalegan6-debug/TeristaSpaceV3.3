package com.terista.space.reflection.annotation

/**
 * Marks a field for reflection stub generation.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class ReflectionField(
    val targetField: String = "",
    val fieldType: String = "Object"
)
