package com.github.ibara1454.secure_shared_preferences.shared_preferences

internal enum class EncryptType {
    NORMAL { override fun downgrade() = NORMAL },
    AES { override fun downgrade() = NORMAL },
    KEYSTORE { override fun downgrade() = AES };

    abstract fun downgrade(): EncryptType
}
