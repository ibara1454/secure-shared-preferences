package com.github.ibara1454.secure_shared_preferences.cipher

interface Encrypter<T> {
    fun encrypt(text: T): T
}
