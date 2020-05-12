package com.github.ibara1454.secure_shared_preferences

import java.security.MessageDigest

internal inline infix fun <A, B, C> ((B) -> C).compose(crossinline g: (A) -> B): (A) -> C =
    { x -> this(g(x)) }

fun sha256(text: String): ByteArray {
    return MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
}
