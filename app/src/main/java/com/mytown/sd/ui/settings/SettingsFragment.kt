package com.mytown.sd.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mytown.sd.entry.UserViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.mytown.reports.CoroutineHelper
import com.mytown.sd.BuildConfig
import com.mytown.sd.HomeActivity
import com.mytown.sd.R
import es.dmoral.prefs.Prefs
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        version_build_number.text = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
        val isPasswordProtected =
            Prefs.with(requireContext()).readBoolean("isPasswordProtected", false)
        if (isPasswordProtected) {
            changePassword.visibility = View.VISIBLE
            adminLock.isChecked = true
        }
        adminLock.setOnCheckedChangeListener(onCheckChangeListener)
        changePassword.setOnClickListener {
            displayAlert(true)
        }
        deleteHistory.setOnClickListener {
            val isChecked =
                Prefs.with(requireContext()).readBoolean("isPasswordProtected", false)
            if (isChecked) {
                displayRemovePasswordAlert( {
                    deleteAll()
                },true)
            }else{
                deleteAll()
            }
        }
        open_source_license.setOnClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
        }
    }
    private fun deleteAll(){
        CoroutineHelper.doInBackground({
            userViewModel.deleteRecords()
        }, {

            Toast.makeText(requireContext(),getString(R.string.message_record_deleted), Toast.LENGTH_LONG).show()
        })

    }

    private val onCheckChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                displayAlert()
            } else {
                val isChecked =
                    Prefs.with(requireContext()).readBoolean("isPasswordProtected", false)
                if (isChecked) {
                    displayRemovePasswordAlert( {
                        Prefs.with(requireContext()).writeBoolean("isPasswordProtected", false)
                    },false)
                }
            }
        }


    private fun displayRemovePasswordAlert(onSuccuss: ()->Unit, isDelete: Boolean = false) {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_password, null)

        val currentPassword =
            dialogView.findViewById<View>(R.id.currentPassword) as EditText

        val newPassword =
            dialogView.findViewById<View>(R.id.newPassword) as EditText
        val confirmPassword =
            dialogView.findViewById<View>(R.id.confirmPassword) as EditText
        newPassword.visibility = View.GONE
        confirmPassword.visibility = View.GONE
        val button1: Button =
            dialogView.findViewById<View>(R.id.buttonSubmit) as Button
        val button2: Button =
            dialogView.findViewById<View>(R.id.buttonCancel) as Button
        button1.text = getString(R.string.confirm)
        button2.setOnClickListener {
            adminLock.setOnCheckedChangeListener(null)
            val isChecked = Prefs.with(requireContext()).readBoolean("isPasswordProtected", false)
            adminLock.isChecked = isChecked
            adminLock.setOnCheckedChangeListener(onCheckChangeListener)
            dialogBuilder.dismiss()
        }
        button1.setOnClickListener { // DO SOMETHINGS

            val password = Prefs.with(requireContext()).read("currentPassword", "")
            val enteredPassword = currentPassword.text.toString()
            if ((enteredPassword.isNotEmpty() && password == enteredPassword)) {
                onSuccuss.invoke()
            } else {
                revertState()
                Toast.makeText(requireContext(), getString(R.string.error_invalid_password), Toast.LENGTH_LONG).show()
            }
            dialogBuilder.dismiss()
        }
        dialogBuilder.setOnCancelListener{
            revertState()
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun displayAlert(isChange: Boolean = false) {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_password, null)

        val currentPassword =
            dialogView.findViewById<View>(R.id.currentPassword) as EditText
        currentPassword.visibility = View.GONE
        if (isChange) {
            currentPassword.visibility = View.VISIBLE
        }
        val newPassword =
            dialogView.findViewById<View>(R.id.newPassword) as EditText
        val confirmPassword =
            dialogView.findViewById<View>(R.id.confirmPassword) as EditText
        val button1: Button =
            dialogView.findViewById<View>(R.id.buttonSubmit) as Button
        val button2: Button =
            dialogView.findViewById<View>(R.id.buttonCancel) as Button

        button2.setOnClickListener {
            revertState()
            dialogBuilder.dismiss()
        }
        button1.setOnClickListener(View.OnClickListener { // DO SOMETHINGS
            val newPasswordString = newPassword.text.toString()
            val confirmPasswordString = confirmPassword.text.toString()
            if (isChange) {
                val password = Prefs.with(requireContext()).read("currentPassword", "")
                val enteredPassword = currentPassword.text.toString()
                if (!(enteredPassword.isNotEmpty() && password == enteredPassword)) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_not_valid_password),
                        Toast.LENGTH_LONG
                    ).show()
                    return@OnClickListener
                }

            }
            if (newPasswordString.isNotEmpty() && newPasswordString == confirmPasswordString) {
                Prefs.with(requireContext()).write("currentPassword", newPasswordString)
                Prefs.with(requireContext()).writeBoolean("isPasswordProtected", true)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_password_saved),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_password_not_match),
                    Toast.LENGTH_LONG
                ).show()
                revertState()
                Prefs.with(requireContext()).writeBoolean("isPasswordProtected", false)
            }

            dialogBuilder.dismiss()
        })

        dialogBuilder.setOnCancelListener {
            revertState()
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun revertState() {
        adminLock.setOnCheckedChangeListener(null)
        val isChecked =
            Prefs.with(requireContext()).readBoolean("isPasswordProtected", false)
        adminLock.isChecked = isChecked
        adminLock.setOnCheckedChangeListener(onCheckChangeListener)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as HomeActivity).onHistoryVisibilityChanged(false)
        }
    }
}