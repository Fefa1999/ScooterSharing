package dk.itu.moapd.scootersharing.fefa

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dk.itu.moapd.scootersharing.fefa.fragments.ListRidesFragment
import dk.itu.moapd.scootersharing.fefa.models.Scooter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CustomAdapterTest {

    private lateinit var scenario: FragmentScenario<ListRidesFragment>
    private lateinit var adapter: CustomDatabaseAdapter
    private lateinit var recyclerView: RecyclerView

    private val dataSet = listOf(
        Scooter("Scooter 1", "Location 1", 1.toLong()),
        Scooter("Scooter 2", "Location 2", 2.toLong()),
        Scooter("Scooter 3", "Location 3", 3.toLong())
    )

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<ListRidesFragment>(themeResId = R.style.Theme_ScooterSharing)
        scenario.onFragment { fragment ->
            recyclerView = fragment.view?.findViewById(R.id.scooterList)!!
            adapter = CustomDatabaseAdapter(dataSet, fragment.childFragmentManager)
            recyclerView.adapter = adapter
        }
    }

    @Test
    fun testListItemsDisplayedCorrectly() {
        onView(withId(R.id.scooterList))
            .check(matches(hasDescendant(withText("Scooter 1"))))

        onView(withId(R.id.scooterList))
            .check(matches(hasDescendant(withText("Location 1"))))

        onView(withId(R.id.scooterList))
            .check(matches(hasDescendant(withText("Scooter 2"))))

        onView(withId(R.id.scooterList))
            .check(matches(hasDescendant(withText("Location 2"))))

        onView(withId(R.id.scooterList))
            .check(matches(hasDescendant(withText("Scooter 3"))))

        onView(withId(R.id.scooterList))
            .check(matches(hasDescendant(withText("Location 3"))))
    }
}

