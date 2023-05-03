package dk.itu.moapd.scootersharing.fefa.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.models.RidesDB

class CustomRidesAdapter(private val dataSet: MutableList<HashMap<String, Any?>>, private val fragmentManager: FragmentManager) :
RecyclerView.Adapter<CustomRidesAdapter.ViewHolder>()  {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scooterName: TextView = view.findViewById(R.id.scooter_name)
        val length: TextView = view.findViewById(R.id.length)
        val price: TextView = view.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.list_old_rides, parent, false)
        return ViewHolder(customView)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.scooterName.text = "Scooter: "+dataSet[position].get("ScooterId").toString()
        holder.length.text = "Duration: "+dataSet[position].get("length").toString()
        holder.price.text = "Price: "+dataSet[position].get("price").toString()
    }

    companion object {
        lateinit var ridesDB: RidesDB
    }
}