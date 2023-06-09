/*
*MIT License
*
*Copyright (c) [2023] [Felix Jeppe Fatum]
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*/

package dk.itu.moapd.scootersharing.fefa.models

import android.content.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

/**
 * Represents a database of rides.
 * This class is a singleton and uses the lazy initialization pattern.
 */
class RidesDB private constructor(context: Context ){
    private val rides = ArrayList<Scooter>()
    companion object : RidesDBHolder<RidesDB, Context>(::RidesDB)
    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference

    /**
     * Returns a reversed list of rides.
     */
    fun getRidesList() : List<Scooter> {
        return rides.reversed()
    }

    /**
     * Adds a new scooter to the list of rides.
     */
    fun addScooter(scooter : Scooter) {
        rides.add(scooter)
    }

    /**
     * Updates the location of the current scooter.
     * The current scooter is assumed to be the last item in the list of rides.
     */
    fun updateScooterInDatabase(id : String) {
        database.child("Scooters").child(id.lowercase()).child("ScooterDetails").setValue(rides.find { it.id == id }!!)
    }

    fun containsScooter(id: String) : Boolean{
        for (scooter in rides) {
            if (scooter.id == id) {
                return true
            }
        }
        return false
    }

    fun getScooter(id : String) : Scooter{
        return rides.find { it.id == id }!!
    }
}

/**
 * A generic singleton holder class.
 * This class is used for lazy initialization of singletons.
 */
open class RidesDBHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    /**
     * Returns an instance of the singleton.
     * If the instance is already created, it is returned.
     * If not, a new instance is created using the creator function.
     */
    fun get(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null)
            return checkInstance
        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null)
                checkInstanceAgain
            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}