package com.github.ibara1454.secure_shared_preferences.exception

class DecryptionException: IllegalStateException(
    "An exception occurred when decrypting data. Maybe the data is broken."
)
