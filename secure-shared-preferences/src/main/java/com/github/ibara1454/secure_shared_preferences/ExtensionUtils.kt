package com.github.ibara1454.secure_shared_preferences

internal inline infix fun <A, B, C> ((B) -> C).compose(crossinline g: (A) -> B): (A) -> C =
    { x -> this(g(x)) }
