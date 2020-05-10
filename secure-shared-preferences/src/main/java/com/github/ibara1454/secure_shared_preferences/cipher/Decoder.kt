package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The encoder transforms the given byte array to base64 format.
 */
interface Decoder<T> {
    /**
     * Transform the given data to original data.
     * @param data any transformed data.
     * @returns original data.
     */
    fun decode(data: T): T
}
