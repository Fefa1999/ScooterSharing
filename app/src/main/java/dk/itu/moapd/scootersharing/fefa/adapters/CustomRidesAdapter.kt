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

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.scooterName.text = "Scooter: "+dataSet[position].get("ScooterId").toString()
        holder.length.text = "Duration: "+dataSet[position].get("length").toString()
        holder.price.text = "Price: "+dataSet[position].get("price").toString()
    }

    companion object {
        lateinit var ridesDB: RidesDB
    }
}