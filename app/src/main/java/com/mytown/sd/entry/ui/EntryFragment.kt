package com.mytown.sd.entry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mytown.sd.entry.UserViewModel
import com.mytown.sd.HomeActivity
import com.mytown.sd.R
import com.mytown.sd.persistence.User
import kotlinx.android.synthetic.main.fragment_entry.*

/**
 * A placeholder fragment containing a simple view.
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
            user.name  = emptyText
            nameField.text?.toString()?.let {
                if(it.isNotEmpty()){
                    user.name  = it
                }
            }
            user.mobileNumber = emptyText
            mobileField.text?.toString()?.let {
                if(it.isNotEmpty()){
                    user.mobileNumber  = it
                }
            }
            user.temperature = emptyText
            temperatureField.text?.toString()?.let {
                if(it.isNotEmpty()){
                    user.temperature  = it
                }
            }
            user.area = emptyText
            areaField.text?.toString()?.let {
                if(it.isNotEmpty()){
                    user.area  = it
                }
            }
            user.address = emptyText
            addressField.text?.toString()?.let {
                if(it.isNotEmpty()){
                    user.address  = it
                }
            }
            userViewModel.insert(user)
            clearFields()
        }
    }

    private fun clearFields(){
        nameField.setText("")
        mobileField.setText("")
        temperatureField.setText("")
        areaField.setText("")
        addressField.setText("")
        nameField.requestFocus()
    }
    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): EntryFragment {
            return EntryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as HomeActivity)?.onHistoryVisibilityChanged(false)
        }

    }
}