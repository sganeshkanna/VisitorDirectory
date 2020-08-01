package com.mytown.reports

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException


object ReportManager {

    fun generateCSV(context: Context,queryMethod: (limit: Int, offset: Int) -> List<Report>) : String{

        var isRecordExists = true
        var offset = 0
        val limit = 100
        var headerString = ""
        val recordList = arrayListOf<String>()
        while (isRecordExists) {
            val list = queryMethod.invoke(limit, offset)
            if (list.isNotEmpty()) {
                for (report in list) {
                    if (headerString.isBlank()) {
                        headerString = report.getHeader(context)
                    }
                    val row = report.getRow()
                    recordList.add(row)
                }
                offset += limit
            } else {
                isRecordExists = false
            }
        }
        return exportEmailInCSV(context, "VisitorDirectory", recordList, headerString)
    }

    @Throws(IOException::class)
    fun exportEmailInCSV(
        context: Context,
        directoryName: String,
        list: ArrayList<String>,
        header: String
    ): String {
        val s = context.getExternalFilesDir(null).toString() + "/"
        val recordingDirectory = File(s + directoryName)
        if (!recordingDirectory.exists()) {
            recordingDirectory.mkdirs()
        }
        var file: File? = null
        try {
            file = File.createTempFile("Report_", ".csv", recordingDirectory)
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

        try {
            val fw = FileWriter(file)
            fw.append(header)
            fw.append("\n")
            for (row in list) {
                fw.append(row)
                fw.append("\n")
            }

            // fw.flush();
            fw.close()
        } catch (e: Exception) {
        }
        return file.absolutePath
    }

}
