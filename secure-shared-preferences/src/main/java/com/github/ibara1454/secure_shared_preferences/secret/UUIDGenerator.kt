package com.github.ibara1454.secure_shared_preferences.secret

import java.util.UUID

class UUIDGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
