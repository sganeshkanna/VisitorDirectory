package com.mytown.sd.entry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mytown.sd.HomeActivity
import com.mytown.sd.R
import com.mytown.sd.entry.UserListAdapter
import com.mytown.sd.entry.UserViewModel
import kotlinx.android.synthetic.main.fragment_list.*

/**
 *
 */
class HistoryFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = UserListAdapter(requireContext())
        userListView.layoutManager = LinearLayoutManager(requireContext())
        userListView.adapter = adapter
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        userViewModel.allUsers.observe(viewLifecycleOwner, Observer { list ->
            // Update the cached copy of the list in the adapter.
            list?.let {
                adapter.setUsers(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as HomeActivity).onHistoryVisibilityChanged(true, this)
        }
    }


    fun showOption(onSelection: (option: Int) -> Unit) {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.bottom_sheet, null)
        (dialogView.findViewById(R.id.downloadOption) as View).setOnClickListener {
            onSelection.invoke(1)
            dialogBuilder.dismiss()
        }
        (dialogView.findViewById(R.id.sendOption) as View).setOnClickListener {
            onSelection.invoke(2)
            dialogBuilder.dismiss()
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }
}