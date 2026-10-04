package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Hero Arena", appName)
  }

  @Test
  fun `verify ten heroes exist in registry`() {
    val heroes = com.example.model.HeroRegistry.allHeroes
    assertEquals(10, heroes.size)
    assert(heroes.any { it.name == "Okçu Elf" })
    assert(heroes.any { it.name == "Siyah Ork" })
    assert(heroes.any { it.name == "Taş Golemi" })
    assert(heroes.any { it.name == "Robot Tank" })
  }
}
