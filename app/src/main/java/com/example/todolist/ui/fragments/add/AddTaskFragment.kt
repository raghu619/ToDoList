package com.example.todolist.ui.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todolist.R
import com.example.todolist.databinding.FragmentAddTaskBinding
import com.example.todolist.viewmodels.SharedViewModel
import com.example.todolist.viewmodels.ToDoViewModel


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AddTaskFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: FragmentAddTaskBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_task, container, false)

        setHasOptionsMenu(true)

        return binding.root
    }


}