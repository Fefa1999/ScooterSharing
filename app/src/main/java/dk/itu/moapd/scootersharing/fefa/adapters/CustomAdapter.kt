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

package dk.itu.moapd.scootersharing.fefa.adapters
import android.app.AlertDialog
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.RidesDB
import dk.itu.moapd.scootersharing.fefa.fragments.ListRidesFragment
import dk.itu.moapd.scootersharing.fefa.models.Scooter

class CustomAdapter(private val dataSet: List<Scooter>, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * ViewHolder for a Scooter item in the RecyclerView.
     *
     * @property linLay the LinearLayout containing the item's views
     * @property title the TextView displaying the Scooter's name
     * @property location the TextView displaying the Scooter's location
     * @property timestamp the TextView displaying the Scooter's timestamp
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val linLay: LinearLayout = view.findViewById(R.id.LinLay)
        //val title: TextView = view.findViewById(R.id.title)
        val location: TextView = view.findViewById(R.id.location)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }

    /**
     * Called when a ViewHolder is created for a Scooter item in the RecyclerView.
     *
     * @param viewGroup the parent ViewGroup
     * @param viewType the type of the view
     * @return a new ViewHolder instance
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        ridesDB = RidesDB.get(viewGroup.context)
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.list_rides, viewGroup, false)
        return ViewHolder(customView)
    }

    /**
     * Called when a ViewHolder is bound to a Scooter item in the RecyclerView.
     *
     * @param viewHolder the ViewHolder to bind
     * @param position the position of the Scooter item in the dataset
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            val popupMenus = PopupMenu(viewHolder.itemView.context, viewHolder.itemView, Gravity.RIGHT)
            popupMenus.inflate(R.menu.menu_list)
            popupMenus.setOnMenuItemClickListener {
                val builder = AlertDialog.Builder(viewHolder.itemView.context)
                builder.setTitle("Delete Ride")
                builder.setMessage("Are you sure you want to delete this ride? Any data connected to it will be lost")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    ridesDB.deleteScooter(position)
                    fragmentManager.beginTransaction().replace(R.id.fragment_container_main, ListRidesFragment()).commit()
                }
                builder.setNegativeButton(android.R.string.no) { dialog, which -> }
                builder.show()
                true
            }
            popupMenus.setForceShowIcon(true)
            popupMenus.show()
        }
       // if (position == 0) {
            //viewHolder.linLay.background = R.drawable.border.toDrawable()
        //}
        viewHolder.location.text = dataSet[position].location
        //viewHolder.title.text = dataSet[position].name
        viewHolder.timestamp.text = dataSet[position].toString()
    }

    /**
     * Returns the number of items in the dataset.
     */
    override fun getItemCount() = dataSet.size

    companion object {
        lateinit var ridesDB: RidesDB
    }
}
