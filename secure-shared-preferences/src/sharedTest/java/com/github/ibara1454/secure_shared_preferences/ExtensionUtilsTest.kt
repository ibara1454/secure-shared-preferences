package com.github.ibara1454.secure_shared_preferences

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExtensionUtilsTest {
    @Test
    fun test_compose_chained_two_functions() {
        val f = { x: Int -> x + 1 } // f(x) = x + 1
        val g = { x: Int -> x * x } // g(x) = x * x
        val h = { x: Int -> (x + 1) * (x + 1) } // h(x) = (x + 1) * (x + 1)

        val gf = g compose f // (g o f)(x) = (x + 1) * (x + 1)

        assertThat((1..10).map(gf)).isEqualTo((1..10).map(h))
    }
}
