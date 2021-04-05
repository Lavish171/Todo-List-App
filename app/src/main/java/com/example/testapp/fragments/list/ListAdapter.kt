package com.example.testapp.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.models.Priority
import com.example.testapp.data.models.ToDoData
import com.example.testapp.databinding.ActivityMainBinding.bind
import com.example.testapp.databinding.RowLayoutBinding

class ListAdapter:RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList= emptyList<ToDoData>()

    class MyViewHolder(val binding:RowLayoutBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=RowLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.titleTxt.text=dataList[position].title
        holder.binding.descriptionTxt.text=dataList[position].description


        holder.binding.rowBackground.setOnClickListener {
            val action=ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            holder.itemView.findNavController().navigate(action)
        }

        when(dataList[position].priority)
        {
            Priority.HIGH->holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.red
            )
            )

            Priority.MEDIUM->holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.yellow
            )
            )

            Priority.LOW->holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.green
            ))

        }
    }

    fun setData(toDoData:List<ToDoData>)
    {
        val toDoDiffUtil=ToDoDiffUtil(dataList,toDoData)

        val toDoDiffResult=DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList=toDoData
       // notifyDataSetChanged()

        toDoDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
       return dataList.size
    }
}