package com.example.todolist.ui.fragments.tasks

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todolist.R
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.data.models.ToDoData
import com.example.todolist.databinding.FragmentTasksBinding
import com.example.todolist.utils.SwipeToDelete
import com.example.todolist.utils.hideKeyboard
import com.example.todolist.viewmodels.SharedViewModel
import com.example.todolist.viewmodels.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class TasksFragment : Fragment(), SearchView.OnQueryTextListener {


    private lateinit var binding: FragmentTasksBinding
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: TasksAdapter by lazy {
        TasksAdapter(
            TasksAdapter.OnClickListener {
                findNavController().navigate(
                    TasksFragmentDirections.actionTasksFragmentToUpdateTaskFragment(
                        it
                    )
                )
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
        val recyclerView = setupRecyclerView()
        swipeToDelete(recyclerView)
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { results ->
            mSharedViewModel.checkIfDatabaseEmpty(results)
            adapter.submitList(results)
        })
        hideKeyboard(requireActivity())
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupRecyclerView(): RecyclerView {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }
        return recyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(
                this,
                Observer { adapter.submitList(it) })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(
                this,
                Observer { adapter.submitList(it) })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.getDataItem(viewHolder.adapterPosition)
                deletedItem?.let {
                    mToDoViewModel.deleteItem(it)
                    restoreDeletedData(viewHolder.itemView, deletedItem)
                }

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(
            view, "Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(
                requireContext(),
                "Successfully Removed Everything!",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true

    }

    override fun onQueryTextChange(query: String?): Boolean {

        if (query != null) {
            searchThroughDatabase(query)
        }
        return true

    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                adapter.submitList(it)
            }
        })
    }

}