package com.github.ibara1454.secure_shared_preferences.cipher

interface Decrypter<T> {
    fun decrypt(text: T): T
}
