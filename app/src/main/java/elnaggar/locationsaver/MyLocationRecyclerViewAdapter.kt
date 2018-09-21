package elnaggar.locationsaver

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import elnaggar.locationsaver.LocationFragment.OnListFragmentInteractionListener
import elnaggar.locationsaver.dummy.DummyContent.Location

import kotlinx.android.synthetic.main.location_list_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [Location] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyLocationRecyclerViewAdapter(
        private val mValues: List<Location>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyLocationRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Location
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.location_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mTitleView.text = item.title
        holder.mLatitudeView.text = item.latitude
        holder.mLongitudeView.text = item.longitude

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size
    fun delete(adapterPosition: Int) {
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.tv_name
        val mLongitudeView: TextView = mView.tv_longitude
        val mLatitudeView: TextView = mView.tv_latitude

    }
}
