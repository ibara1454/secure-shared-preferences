package com.github.ibara1454.secure_shared_preferences.exception

/**
 * The exception indicates that input crypto is not in a correct format.
 */
class IllegalBlockSizeException: IllegalArgumentException(
    "Input length must be multiple of 16 when decrypting with padded cipher"
)
