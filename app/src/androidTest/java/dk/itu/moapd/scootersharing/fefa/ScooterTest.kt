package dk.itu.moapd.scootersharing.fefa

import android.content.Context
import android.location.Geocoder
import android.util.Log

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest

import dk.itu.moapd.scootersharing.fefa.models.Scooter
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScooterTest {
    private lateinit var scooter: Scooter
    private lateinit var appContext: Context
    private lateinit var expectedAddress: String

    @Before
    fun setUp() {
        scooter = Scooter("Scooter 1", 55.698147, 12.582080, false, 100)
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        expectedAddress = "Ringstedgade 6, 2100 København, Danmark"
    }

    @Test
    fun testAddressMethodWorks() {
        assertEquals(expectedAddress, scooter.getAddressFromLatLng(appContext))
    }


    @Test
    fun getAddressFromLatLng_with_invalid_latitude_and_longitude_returns_empty_string() {
        val scooter = Scooter("123", 91.0, -181.0, false, 100)
        assertEquals("", scooter.getAddressFromLatLng(appContext))
    }
}

