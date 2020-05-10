package com.github.ibara1454.secure_shared_preferences.cipher

import com.github.ibara1454.secure_shared_preferences.exception.DecodingException

/**
 * The encoder transforms the given base64 input into original data.
 */
internal class Base64Decoder: Decoder<ByteArray> {
    /**
     * Transform the given no-wrap base64 (one long line) to original data.
     *
     * See also
     * - https://developer.android.com/reference/android/util/Base64
     *
     * @param data any no-wrap base64 byte array.
     * @returns the original data.
     * @throws DecodingException if the input data is not in a correct no-wrap base64 format.
     */
    @Throws(DecodingException::class)
    override fun decode(data: ByteArray): ByteArray =
        try {
            // Decode the given no-wrap base 64 input.
            android.util.Base64.decode(data, android.util.Base64.NO_WRAP)
        } catch (e: IllegalArgumentException) {
            // If any exception thrown.
            // This will occurred if the input data is not in a correct no-wrap base64 format.
            throw DecodingException()
        }
}
