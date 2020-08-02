package com.mytown.sd.entry.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mytown.sd.HomeActivity
import com.mytown.sd.R
import com.mytown.sd.entry.UserViewModel
import com.mytown.sd.persistence.PreferenceManager
import com.mytown.sd.persistence.Suggestion
import com.mytown.sd.persistence.User
import kotlinx.android.synthetic.main.fragment_entry.*


/**
 *
 */
class EntryFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var emptyText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        emptyText = getString(R.string.empty)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            val user = User()
            user.name = emptyText
            nameField.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    user.name = it
                }
            }
            user.mobileNumber = emptyText
            mobileField.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    user.mobileNumber = it
                }
            }
            user.temperature = emptyText
            temperatureField.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    user.temperature = it
                }
            }
            user.area = emptyText
            areaField.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    user.area = it
                    PreferenceManager.putArea(requireContext(), it)
                }
            }
            user.address = emptyText
            addressField.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    user.address = it
                }
            }
            userViewModel.insert(user)
            clearFields()
        }
        userViewModel.suggestions.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<Suggestion>>() {
            override fun onChanged(sender: ObservableList<Suggestion>?) {

            }

            override fun onItemRangeRemoved(
                sender: ObservableList<Suggestion>?,
                positionStart: Int,
                itemCount: Int
            ) {
                Log.i("TAG", "Suggestions")
            }

            override fun onItemRangeMoved(
                sender: ObservableList<Suggestion>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {

            }

            override fun onItemRangeInserted(
                sender: ObservableList<Suggestion>?,
                positionStart: Int,
                itemCount: Int
            ) {
                sender?.let {
                    if (it.isNotEmpty()) {
                        if (mobileField.text.toString()
                                .isNotEmpty()
                        ) {
                            val suggestion = it[0]
                            handler.post {
                                suggestion?.let {
                                    areaField.setText(it.area)
                                    addressField.setText(it.address)
                                }
                            }
                        }
                    }
                }
            }

            override fun onItemRangeChanged(
                sender: ObservableList<Suggestion>?,
                positionStart: Int,
                itemCount: Int
            ) {
                Log.i("TAG", "Suggestions")
            }
        })

        mobileField.setOnEditorActionListener { p0, actionId, p2 ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                userViewModel.getSuggestion(mobileField.text.toString())
            }
            false
        }
        areaSuggestionAdapter()
    }

    val handler = Handler()

    private fun areaSuggestionAdapter() {
        val areaNames = PreferenceManager.getArea(requireContext())
        val arrayAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.select_dialog_item, areaNames
        )
        areaField.threshold = 1
        areaField.setAdapter(arrayAdapter)
    }

    private fun clearFields() {
        nameField.setText("")
        mobileField.setText("")
        temperatureField.setText("")
        areaField.setText("")
        addressField.setText("")
        nameField.requestFocus()
        areaSuggestionAdapter()
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as HomeActivity).onHistoryVisibilityChanged(false)
        }
    }
}