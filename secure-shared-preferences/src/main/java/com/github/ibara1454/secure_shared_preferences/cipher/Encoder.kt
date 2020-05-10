package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The encoder provides the method [encode] to transform the original data into another format.
 */
interface Encoder<T> {
    /**
     * Transform the given data to another format.
     * @param data any data.
     * @returns transformed data.
     */
    fun encode(data: T): T
}
