package com.mytown.reports

import kotlinx.coroutines.*

object CoroutineHelper {

    private var job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val bgDispatcher: CoroutineDispatcher = Dispatchers.IO
    fun doInBackground(backgroundTask: () -> Any, onCompletion: (Any) -> Unit) = uiScope.launch {
        val result = withContext(bgDispatcher) {
            // background thread
            backgroundTask.invoke()
        }
        onCompletion.invoke(result)
    }
}