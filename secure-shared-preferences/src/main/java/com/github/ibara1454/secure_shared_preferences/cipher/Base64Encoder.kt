package com.github.ibara1454.secure_shared_preferences.cipher

/**
 * The encoder transforms the given byte array into base64 format.
 */
internal class Base64Encoder: Encoder<ByteArray> {
    /**
     * Transform the given data to no-wrap base64 format (the output will be on one long line).
     *
     * See also
     * - https://developer.android.com/reference/android/util/Base64
     *
     * @param data any byte array data.
     * @returns no-wrap base64 data.
     */
    override fun encode(data: ByteArray): ByteArray =
        // This will not throw any exception
        android.util.Base64.encode(data, android.util.Base64.NO_WRAP)
}
