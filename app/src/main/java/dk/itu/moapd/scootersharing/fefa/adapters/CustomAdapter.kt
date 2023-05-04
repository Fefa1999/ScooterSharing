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
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.models.RidesDB
import dk.itu.moapd.scootersharing.fefa.models.Scooter
import java.util.*

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
        val img: ImageView = view.findViewById(R.id.image_view)
        val title: TextView = view.findViewById(R.id.text_view_1)
        val location: TextView = view.findViewById(R.id.text_view_2)
        val inUse: TextView = view.findViewById(R.id.text_view_3)
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
        val storage = Firebase.storage("gs://scooter-sharing-a1130.appspot.com")
        val imageRef = storage.getReferenceFromUrl("gs://scooter-sharing-a1130.appspot.com/"+dataSet[position].id+".jpeg")
        imageRef.downloadUrl.addOnSuccessListener {
            Glide.with(viewHolder.itemView.context)
                .load(it)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(viewHolder.img)
        }

        viewHolder.title.text = dataSet[position].id
        if(dataSet[position].inUse){
            viewHolder.inUse.text = "Currently Unavailable"
        }else{
            viewHolder.inUse.text = "Currently available"
        }
        viewHolder.location.text =  dataSet[position].getAddressFromLatLng(fragmentManager.fragments.get(0).requireContext())
    }

    /**
     * Returns the number of items in the dataset.
     */
    override fun getItemCount() = dataSet.size

    companion object {
        lateinit var ridesDB: RidesDB
    }
}
