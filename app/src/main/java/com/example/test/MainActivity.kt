package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.ui.theme.AddEditNoteScreen
import com.example.test.ui.theme.NoteListScreen
import com.example.test.ui.theme.SelectedNote

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteDao = NoteDB.getDatabase(applicationContext).noteDao()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "note_list") {
                composable("note_list") {
                    NoteListScreen(noteDao = noteDao, navController = navController)
                }
                composable("add_note") {
                    AddEditNoteScreen(
                        noteDao = noteDao,
                        onNoteSaved = {
                            navController.popBackStack()
                            SelectedNote = null
                        },
                        context = applicationContext)
                }
            }
        }
    }
}
