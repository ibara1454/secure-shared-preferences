package com.github.ibara1454.secure_shared_preferences.exception

/**
 * The exception indicates that input base64 data is not in a correct format.
 */
class DecodingException: IllegalArgumentException(
    "Invalid base64 expression."
)
