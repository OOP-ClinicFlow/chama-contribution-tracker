package com.zut.chama_contribution_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zut.chama_contribution_tracker.data.AppDatabase
import com.zut.chama_contribution_tracker.data.ChamaRepository
import com.zut.chama_contribution_tracker.ui.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val repository = ChamaRepository(db.memberDao(), db.contributionDao())

        setContent {
            AppNavigation(repository)
        }
    }
}
