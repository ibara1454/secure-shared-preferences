package com.github.ibara1454.secure_shared_preferences.exception

/**
 * The exception indicates that input crypto is not in a correct format.
 */
class DecryptionException: IllegalStateException(
    "An exception occurred when decrypting data. Maybe the data is broken."
)
