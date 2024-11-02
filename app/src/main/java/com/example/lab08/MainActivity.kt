package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab08.ui.theme.Lab08Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()


                val taskDao = db.taskDao()
                val viewModel = TaskViewModel(taskDao)


                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text(if (isEditing) "Editar tarea" else "Nueva tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    if (isEditing) {
                        taskToEdit?.let {
                            viewModel.updateTask(it.copy(description = newTaskDescription))
                        }
                        isEditing = false
                        taskToEdit = null
                    } else {
                        viewModel.addTask(newTaskDescription)
                    }
                    newTaskDescription = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (isEditing) "Guardar cambios" else "Agregar tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEach { task ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task.description)
                Row {
                    Button(onClick = {
                        isEditing = true
                        taskToEdit = task
                        newTaskDescription = task.description
                    }) {
                        Text("Editar")
                    }
                    Button(onClick = { viewModel.deleteTask(task) }) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}