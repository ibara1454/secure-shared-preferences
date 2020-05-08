package com.github.ibara1454.secure_shared_preferences.exception

import java.security.InvalidParameterException

class InvalidIVException: InvalidParameterException(
    "Wrong IV length: must be 16 bytes long."
)
