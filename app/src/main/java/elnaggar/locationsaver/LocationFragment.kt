package elnaggar.locationsaver

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.fragment_location_list.*


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [LocationFragment.OnListFragmentInteractionListener] interface.
 */
class LocationFragment : Fragment() {


    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_location_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.layoutManager=LinearLayoutManager(context)
        val mValues = getItems()
        val locationAdapter = MyLocationRecyclerViewAdapter(mValues, listener)

        // Set the adapter

        list.adapter = locationAdapter
        list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Row is swiped from recycler view
                // remove it from adapter
                delete(getItems()[viewHolder.adapterPosition])
                list.adapter = MyLocationRecyclerViewAdapter(getItems(), listener)

            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                // view the background view
            }
        }

// attaching the touch helper to recycler view
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(list)
        list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    private fun getItems(): List<Location> {
        val database: AppDatabase = (activity as MainActivity).getDatabase()
        return database.locationDao().all
    }

    fun delete(item: Location) {
        val database: AppDatabase = (activity as MainActivity).getDatabase()
        database.locationDao().delete(item)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun itemAdded() {
        list.adapter = MyLocationRecyclerViewAdapter(getItems(), listener)

    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Location?)
    }

    companion object {


        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance() = LocationFragment()
    }
}
