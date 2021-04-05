package com.example.testapp.fragments.list

import android.app.AlertDialog
import android.graphics.LinearGradient
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.example.testapp.R
import com.example.testapp.SharedViewModel
import com.example.testapp.data.ViewModels.ToDoViewModel
import com.example.testapp.data.models.ToDoData
import com.example.testapp.databinding.FragmentListBinding
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment(),SearchView.OnQueryTextListener {

    private  val mToDoViewModel:ToDoViewModel by viewModels()
    private  val mSharedViewModel:SharedViewModel by viewModels()


    private val adapter:ListAdapter by lazy {
        ListAdapter()
    }

    private lateinit var binding: FragmentListBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentListBinding.bind(view)

        //setting up the recycler view
        setUpRecyclerView()

       mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer {data->
           //checking whether our data is empty or not,if it is empty,then
           //we will show the empty icon and no data text view
           mSharedViewModel.checkIfDatabaseEmpty(data)
           adapter.setData(data)
       })

        //observing our database
        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer {
            showEmptyDatabaseViews(it)
        })

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setHasOptionsMenu(true)

        }

    private fun setUpRecyclerView() {
        val recyclerView=binding.recyclerView
        recyclerView.adapter=adapter
        //recyclerView.layoutManager=LinearLayoutManager(requireContext())
        //recyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        recyclerView.layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        recyclerView.itemAnimator=LandingAnimator().apply {
            addDuration=350
        }

        swipeToDelete(recyclerView)
    }


    private  fun swipeToDelete(recyclerView: RecyclerView)
    {
        val swipeToDeleteCallBack=object:SwipeToDelete()
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem=adapter.dataList[viewHolder.adapterPosition]

                //delete the item

                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
             //   Toast.makeText(requireContext(),"Successfully Removed ${deletedItem.title}",Toast.LENGTH_SHORT).show()

                //restore the deleted data

                restoreDeletedData(viewHolder.itemView,deletedItem)
            }
        }

        val itemTouchHelper=ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private  fun restoreDeletedData(view:View,deletedItem:ToDoData)
    {
        val snackBar=Snackbar.make(
                view,"Deleted '${deletedItem.title}' ",
                Snackbar.LENGTH_LONG
        )

        snackBar.setAction("Undo")
        {
            mToDoViewModel.insertData(deletedItem)
            //removing this since this is causing error,though animation libraries says to include this
            //but it is working fine then also
            //adapter.notifyItemChanged(position)
        }

        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu,menu)

        val search=menu.findItem(R.id.search_menu)
        val searchView=search.actionView as SearchView
        searchView.isSubmitButtonEnabled=true
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_delete_all ->confirmRemoval()
            R.id.menu_sortBy->mToDoViewModel.sortByHighPriority.observe(this, Observer { adapter.setData(it) })
            R.id.menu_priority_low->mToDoViewModel.sortByLowPriority.observe(this, Observer { adapter.setData(it) })

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!=null)
        {
             searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query!=null)
        {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
          var searchQuery:String=query

        searchQuery="%$searchQuery%"
        //observe the live data

        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer {list->
            list.let {
                adapter.setData(it)
            }
        })
    }

    private fun showEmptyDatabaseViews(emptyDatabase:Boolean) {
        if(emptyDatabase)
        {
            binding.noDataImageView.visibility=View.VISIBLE
            binding.textView.visibility=View.VISIBLE
        }
        else
        {
            binding.noDataImageView.visibility=View.INVISIBLE
            binding.textView.visibility=View.INVISIBLE
        }
    }


    private fun confirmRemoval() {

        val builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton("YES")
        {
            _,_->
            mToDoViewModel.deleteAll()
            Toast.makeText(requireContext(),
                    "Successfully Removed Everything",
                    Toast.LENGTH_SHORT).show()

        }

        builder.setNegativeButton("NO"){_,_->}
        builder.setTitle("Delete All ?")
        builder.setMessage("Are you sure you want to remove All Data ?")
        builder.create().show()

    }


}