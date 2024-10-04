package com.example.test.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.test.Note
import com.example.test.NoteDao
import com.example.test.R
import kotlinx.coroutines.launch

val MainGreen = Color(0xFF009688)
var SelectedNote: Note? = null

@Composable
fun NoteListScreen(noteDao: NoteDao, navController: NavController) {
    val scope = rememberCoroutineScope()
    var notes by remember { mutableStateOf(listOf<Note>()) }
    LaunchedEffect(Unit) {
        notes = noteDao.getAllNotes()
    }
    Scaffold(
        topBar = {
            HeaderRow(
                content = {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.padding(vertical = 15.dp, horizontal = 15.dp))
                    Text(
                        "Электронный конспект",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp)
                }
            )
        },
        bottomBar = {
            FooterRow(
                content = {
                    ActionButton(
                        onClick = {
                            navController.navigate("add_note") {
                                popUpTo("add_note") {
                                    inclusive = true
                                }
                            }
                        })
                    },
                arrag = Arrangement.Center)
        }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(vertical = 20.dp)) {
            items(notes) { note ->
                NoteCard(note, navController)
            }
        }
    }
}

@Composable
fun AddEditNoteScreen(noteDao: NoteDao, onNoteSaved: () -> Unit = {}, context: Context) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    if (SelectedNote != null) {
        title = SelectedNote!!.title
        content = SelectedNote!!.content
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HeaderRow(content = {
                var text = "Добавить заметку"
                if (SelectedNote != null)
                    text = "Редактирование заметки"
                Text(
                    text,
                    modifier = Modifier.padding(20.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp)
            })
        },
        bottomBar = {
            FooterRow(
                content = {
                    if (SelectedNote != null){
                        ActionButton(
                            onClick = {
                                scope.launch {
                                    noteDao.delete(SelectedNote!!)
                                    onNoteSaved()
                                }
                            },
                            icon = Icons.Default.Clear)
                    }
                    ActionButton(
                        onClick = {
                            if (title.isEmpty()){
                                Toast.makeText(context, "Note title can't be empty!", Toast.LENGTH_LONG).show()
                            }
                            else{
                                if (SelectedNote == null){
                                    scope.launch {
                                        noteDao.insert(Note(title = title, content = content))
                                        onNoteSaved()
                                    }
                                }
                                else{
                                    scope.launch {
                                        noteDao.update(SelectedNote!!.id, title, content)
                                        onNoteSaved()
                                    }
                                }
                            }
                        },
                        icon = Icons.Default.Check)},
                arrag = Arrangement.Absolute.SpaceEvenly)
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Spacer(modifier = Modifier.height(15.dp))
            DefAppTextField(
                name = "Название",
                ico = Icons.Default.Edit,
                value = title,
                onVal = {title = it})
            DefAppTextField(
                name = "Содержание",
                ico = Icons.Default.Menu,
                value = content,
                onVal = {content = it})
        }
    }
}

@Composable
fun NoteCard(note: Note, navController: NavController)
{
    Card(modifier = Modifier.fillMaxWidth().padding(20.dp).size(125.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = {
            SelectedNote = note
            navController.navigate("add_note") {
                popUpTo("add_note") {
                    inclusive = true
                }
            }
        }) {
        Box(modifier = Modifier.background(MainGreen).fillMaxSize()) {
            Column{
                Text(note.title, color = Color.White, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.titleMedium)
                Text(note.content, color = Color.White, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HeaderRow(content: @Composable (RowScope.() -> Unit)) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MainGreen,
            shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)).statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

@Composable
fun FooterRow (
    content: @Composable (RowScope.() -> Unit),
    alig: Alignment.Vertical = Alignment.CenterVertically, arrag: Arrangement.Horizontal = Arrangement.Start,
    @SuppressLint("ModifierParameter") mod: Modifier = Modifier.fillMaxWidth().background(
        MainGreen, shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)).navigationBarsPadding()) {
    Row(content = content, modifier = mod, verticalAlignment = alig, horizontalArrangement = arrag)
}

@Composable
fun ActionButton(onClick: () -> Unit = {}, icon: ImageVector = Icons.Filled.Add) {
    FloatingActionButton(
        modifier = Modifier.padding(10.dp),
        containerColor = Color.White,
        contentColor = MainGreen,
        shape = CircleShape,
        onClick = { onClick() }
    ) {
        Icon(icon, icon.toString())
    }
}

@Composable
fun DefAppTextField(
    name: String, ico: ImageVector,
    @SuppressLint("ModifierParameter")
    mod: Modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 20.dp),
    sl: Boolean = true, value: String, onVal: (String) -> Unit) {
    TextField(
        modifier = mod,
        leadingIcon = { Icon(ico, contentDescription = name) },
        value = value,
        colors =  TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            unfocusedLabelColor = MainGreen,
            focusedContainerColor = Color.Transparent,
            focusedTextColor = Color.Black,
            focusedIndicatorColor = MainGreen,
            cursorColor = MainGreen
        ),
        singleLine = sl,
        onValueChange = onVal,
        placeholder = { Text(name, modifier = Modifier.alpha(0.3f)) }
    )
}

