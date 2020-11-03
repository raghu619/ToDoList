package com.example.todolist.ui.fragments.tasks

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.databinding.FragmentTasksBinding
import com.example.todolist.utils.SwipeToDelete
import com.example.todolist.utils.hideKeyboard
import com.example.todolist.viewmodels.SharedViewModel
import com.example.todolist.viewmodels.ToDoViewModel
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class TasksFragment : Fragment() {


    private lateinit var binding: FragmentTasksBinding
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: TasksAdapter by lazy {
        TasksAdapter(
            TasksAdapter.OnClickListener {
                findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToUpdateTaskFragment(it))
            }
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }
        swipeToDelete(recyclerView)
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { results ->
            mSharedViewModel.checkIfDatabaseEmpty(results)
            adapter.submitList(results)
        })
        hideKeyboard(requireActivity())
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.getDataItem(viewHolder.adapterPosition)
                deletedItem?.let { mToDoViewModel.deleteItem(it) }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


}