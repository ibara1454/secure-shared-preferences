package com.github.ibara1454.secure_shared_preferences.exception

class InvalidKeyException: IllegalArgumentException(
    "Invalid secret key. Please ensure the key is 16-byte length and the algorithm is AES."
)
