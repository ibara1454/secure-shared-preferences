package com.github.ibara1454.secure_shared_preferences.exception

/**
 * The exception indicates that the given transformation is not available in this platform.
 */
class InvalidSpecificationException: IllegalArgumentException(
    "Cannot find any provider supporting such specification. " +
        "This is because the given specification is not valid, " +
        "or your platform does not support the specification."
)
