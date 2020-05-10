package com.github.ibara1454.secure_shared_preferences.exception

/**
 * The exception indicates that secret key is not in a correct format.
 */
class InvalidKeyException: IllegalArgumentException(
    "Invalid secret key. Please ensure the key is 16-byte length and the algorithm is AES."
)
