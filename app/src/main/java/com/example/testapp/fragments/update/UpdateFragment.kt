package com.example.testapp.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testapp.R
import com.example.testapp.SharedViewModel
import com.example.testapp.data.ViewModels.ToDoViewModel
import com.example.testapp.data.models.Priority
import com.example.testapp.data.models.ToDoData
import com.example.testapp.databinding.FragmentUpdateBinding


class UpdateFragment : Fragment() {

    private  val args by navArgs<UpdateFragmentArgs>()

    private val mSharedViewModel:SharedViewModel by viewModels()

    private  val mToDoViewModel:ToDoViewModel by viewModels()

    private lateinit var updateBinding: FragmentUpdateBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateBinding= FragmentUpdateBinding.bind(view)

        //updateBinding.currentTitleEt.setText(args.currentItem.title)
        updateBinding.currentTitleEt.setText(args.currentItem.title)
        updateBinding.currentDescriptionEt.setText(args.currentItem.description)
        updateBinding.currentPrioritiesSpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))

        updateBinding.currentPrioritiesSpinner.onItemSelectedListener=mSharedViewModel.listener
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_save-> updateItem()
            R.id.menu_delete-> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }



    private fun updateItem() {
        val title=updateBinding.currentTitleEt.text.toString()
        val description=updateBinding.currentDescriptionEt.text.toString()
        val getPriority=updateBinding.currentPrioritiesSpinner.selectedItem.toString()

        val validation=mSharedViewModel.verifyDataFromUser(title,description)
        if(validation)
        {
            val updatedItem=ToDoData(
                    args.currentItem.id,
                    title,
                    mSharedViewModel.parsePriority(getPriority),
                    description,
            )
                mToDoViewModel.updateData(updatedItem)
                Toast.makeText(requireContext(),"Succesfully Updated",Toast.LENGTH_SHORT).show()
            //when we have updated our item we have to navigate back to our activity
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        else
        {
            Toast.makeText(requireContext(),"Pls Fill Out All The Field",Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmItemRemoval() {
        val builder=AlertDialog.Builder(requireContext())
        builder.setPositiveButton("YES")
        {
            _,_->
            mToDoViewModel.deleteItem(args.currentItem)
            Toast.makeText(requireContext(),
                    "Successfully Removed : ${args.currentItem.title}",
                    Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton("NO"){_,_->}
        builder.setTitle("Delete '${args.currentItem.title}' ?")
        builder.setMessage("Are you sure you want to remove '${args.currentItem.title}' ?")
        builder.create().show()
    }

}