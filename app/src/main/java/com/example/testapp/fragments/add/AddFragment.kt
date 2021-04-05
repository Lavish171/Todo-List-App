package com.example.testapp.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testapp.R
import com.example.testapp.SharedViewModel
import com.example.testapp.data.ViewModels.ToDoViewModel
import com.example.testapp.data.models.Priority
import com.example.testapp.data.models.ToDoData
import com.example.testapp.databinding.FragmentAddBinding


class AddFragment : Fragment(){

    private  val mToDoViewModel:ToDoViewModel by viewModels()
    private  val mSharedViewModel:SharedViewModel by viewModels()

    private lateinit var addBinding: FragmentAddBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_add, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBinding= FragmentAddBinding.bind(view)
        addBinding.prioritiesSpinner.onItemSelectedListener=mSharedViewModel.listener
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_add)
        {
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        val mTitle=addBinding.titleEt.text.toString()
        val mPriority=addBinding.prioritiesSpinner.selectedItem.toString()
        val mDescription=addBinding.descriptionEt.text.toString()

        val validation=mSharedViewModel.verifyDataFromUser(mTitle,mDescription)

        if(validation)
        {
            //if validation becomes true then only insert the data into our database
            val newData=ToDoData(
                0,
                mTitle,
                    mSharedViewModel.parsePriority(mPriority),
                mDescription
            )

            mToDoViewModel.insertData(newData)
            //Toast Message
            Toast.makeText(requireContext(),"Successfully Added",Toast.LENGTH_SHORT).show()
            //Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
        else
        {
            Toast.makeText(requireContext(),"Please Fill All the Fields",Toast.LENGTH_SHORT).show()
        }
    }




}