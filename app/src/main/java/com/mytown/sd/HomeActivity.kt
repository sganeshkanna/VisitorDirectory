package com.mytown.sd

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mytown.reports.CoroutineHelper
import com.mytown.reports.Report
import com.mytown.reports.ReportManager
import com.mytown.sd.entry.ui.HistoryFragment
import com.mytown.sd.persistence.VisitorDatabase
import com.mytown.sd.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File


class HomeActivity : VisitorActivity() {

    private var shareMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
//        when (resources.getInteger(R.integer.source)) {
//            1 -> {
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            }
//            2 -> {
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            }
//        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_entry, R.id.navigation_history, R.id.navigation_more
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        navView.setOnNavigationItemSelectedListener {
//            shareMenuItem?.isVisible = false
//            if(it.itemId == R.id.navigation_history){
//                shareMenuItem?.isVisible = true
//            }
//            navView.selectedItemId = it.itemId
//            return@setOnNavigationItemSelectedListener false
//        }
        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        shareMenuItem = menu!!.findItem(R.id.action_share)
        shareMenuItem?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                prepareReport()
            }
        }
        return true
    }

    private var job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private fun prepareReport() {
        val userDao = VisitorDatabase.getDatabase(application, uiScope).userDao()
        CoroutineHelper.doInBackground({
            ReportManager.generateCSV(this@HomeActivity) { limit, offset ->
                return@generateCSV userDao!!.loadUser(limit, offset) as List<Report>

            }
        }, { path ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                historyFragment?.showOption { option ->
                    when (option) {
                        1 -> {
                            saveToDownloads(path as String)
                        }
                        2 -> {
                            shareFile(path as String)
                        }
                    }
                }
            } else {
                shareFile(path as String)
            }

            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "Path $path", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveToDownloads(path: String) {
        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this)
            return
        }
        val fileWithinMyDir = File(path)
        val uri = FileProvider.getUriForFile(
            this, applicationContext
                .packageName.toString() + ".provider", fileWithinMyDir
        )

        // You can add more columns.. Complete list of columns can be found at
        // https://developer.android.com/reference/android/provider/MediaStore.Downloads
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Downloads.TITLE, fileWithinMyDir.name)
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileWithinMyDir.name)
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv")
        contentValues.put(MediaStore.Downloads.SIZE, fileWithinMyDir.length())

        // If you downloaded to a specific folder inside "Downloads" folder
        contentValues.put(
            MediaStore.Downloads.RELATIVE_PATH,
            Environment.DIRECTORY_DOWNLOADS + File.separator + "Visitor"
        )

        // Insert into the database
        val database = contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            database.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        }
    }

    private fun shareFile(path: String) {
        val intentShareFil = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir = File(path)
        val uri = FileProvider.getUriForFile(
            this, applicationContext
                .packageName.toString() + ".provider", fileWithinMyDir
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/octet-stream"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        intentShareFil.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivityForResult(Intent.createChooser(shareIntent, "Backup"), 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var historyFragment: HistoryFragment? = null
    fun onHistoryVisibilityChanged(isVisible: Boolean, fragment: HistoryFragment? = null) {
        shareMenuItem?.isVisible = isVisible
        fragment?.let {
            historyFragment = it
        }
    }

}