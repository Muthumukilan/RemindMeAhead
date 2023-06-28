package com.example.remindmeahead

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remindmeahead.database.Event
import com.example.remindmeahead.database.MainViewModel
import com.example.remindmeahead.database.Note
import com.example.remindmeahead.ui.theme.AppTheme
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel:MainViewModel by viewModels()

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScaffold(mainViewModel)
                }
            }
        }
    }
}

enum class Screens {
    Home,
    Add,
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScaffold(mainViewModel:MainViewModel) {
    val navController = rememberNavController()
    var state by remember {
        mutableStateOf(true)
    }
    Scaffold(floatingActionButton = {
        if (state) {
            FloatingActionButton(onClick = {
                state = !state
                navController.navigate(route = Screens.Add.name)
            }) {
                Icon(Icons.Filled.Add, "")
            }
        }
    }) { padding ->
        NavHost(navController = navController, startDestination = Screens.Home.name) {
            composable(route = Screens.Home.name) {
                if (navController.currentBackStackEntry?.destination?.route.toString() == Screens.Home.name) {
                    state = true
                }
                LandingScreen(padding,mainViewModel)
            }
            composable(route = Screens.Add.name) {
                AddScreen(padding, LocalContext.current,mainViewModel)
            }
        }
    }
}

@Composable
fun LandingScreen(
    padding: PaddingValues,mainViewModel:MainViewModel
) {
    var category by remember {
        mutableStateOf("")
    }
    val eventList=mainViewModel.allData.collectAsState(listOf())

    ConstraintLayout(Modifier.padding(padding)) {
        val (radio, list) = createRefs()
        val options = listOf("Birthday", "Wedding", "Memorial", "Other")
        LazyRow(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .constrainAs(radio) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            items(count = 4) {
                if (category == options[it]) {
                    Button(
                        onClick = {
                            if (category == options[it]) {
                                category = ""
                            } else {
                                category = options[it]
                            }
                        }, modifier = Modifier
                            .height(50.dp)
                            .width(150.dp)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = options[it])
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            category = options[it]
                        }, modifier = Modifier
                            .height(50.dp)
                            .width(150.dp)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = options[it])
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.constrainAs(list) {
            top.linkTo(radio.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {

            items(eventList.value.size){index ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ){
                    Column() {
                        val event=eventList.value[index]
                        Text(text = event.category)
                        Text(text = event.fname)
                        Text(text = event.lname)
                        Text(text = event.date)
                        IconButton(onClick = { mainViewModel.deleteEvent(event) }) {
                            Icon(Icons.Default.Delete,"delete")
                        }
                        IconButton(onClick = { //TODO edit page
                             }) {
                            Icon(Icons.Default.Edit,"edit")
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(padding: PaddingValues, context: Context,mainViewModel:MainViewModel) {
    ConstraintLayout(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        val (type, fnm, lnm, cal, note, buttons) = createRefs()
        val options = listOf("Birthday", "Wedding", "Memorial", "Other")
        val times = listOf("1 day", "2 days", "3 days", "1 week", "2 weeks")
        val placeHolders = listOf("Happy Birthday", "Happy wedding day", "My Deepest Condolences", "Remind the event")
        val vChain =
            createVerticalChain(type, fnm, lnm, cal, note, buttons, chainStyle = ChainStyle.Spread)

        var ddm by remember {
            mutableStateOf(false)
        }
        var dddm by remember {
            mutableStateOf(false)
        }
        var eventCategory by remember {
            mutableStateOf("")
        }
        var remindMeAt by remember {
            mutableStateOf(0)
        }
        var firstName by remember {
            mutableStateOf("")
        }
        var lastName by remember {
            mutableStateOf("")
        }
        var eventNote by remember {
            mutableStateOf("")
        }
        var phNumber by remember {
            mutableStateOf("")
        }
        var tillDate by remember {
            mutableStateOf("")
        }
        var notesToSend by remember {
            mutableStateOf("")
        }
        var moreOptions by remember {
            mutableStateOf(false)
        }
        var isDialogShown: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var remindDate: LocalDate? by remember {
            mutableStateOf(null)
        }
        var date: LocalDate? by remember {
            mutableStateOf(null)
        }

        if (isDialogShown) {
            DatePickerDialog(
                onDismissRequest = { isDialogShown = false },
                onDateChange = {
                    date = it
                    isDialogShown = false
                },

                title = { Text(text = "Select date") }
            )
        }

        ExposedDropdownMenuBox(expanded = ddm,
            onExpandedChange = { ddm = !ddm },
            modifier = Modifier
                .constrainAs(type) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = eventCategory,
                onValueChange = { eventCategory = it },
                label = { Text(text = "Event Type") },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            if (ddm == true) {
                                Icons.Filled.KeyboardArrowUp
                            } else {
                                Icons.Filled.KeyboardArrowDown
                            }, contentDescription = ""
                        )
                    }
                })
            ExposedDropdownMenu(expanded = ddm, onDismissRequest = { ddm = false }) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(text = selectionOption) }, onClick = {
                        eventCategory = selectionOption
                        ddm = false
                    })
                }
            }
        }

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(text = "First name") },
            modifier = Modifier.constrainAs(fnm) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(text = "Last name") },
            modifier = Modifier.constrainAs(lnm) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

        Button(onClick = {
            isDialogShown = true
        }, shape = RoundedCornerShape(10.dp), modifier = Modifier.constrainAs(cal) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Text(text = "Set Date")
        }

        OutlinedTextField(value = eventNote,
            onValueChange = { eventNote = it },
            label = { Text(text = "Additional Note") },
            modifier = Modifier
                .height(225.dp)
                .constrainAs(note) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(buttons) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            val (more, confirm) = createRefs()
            val hChain = createHorizontalChain(more, confirm, chainStyle = ChainStyle.SpreadInside)
            OutlinedButton(onClick = { moreOptions = true }, modifier = Modifier
                .padding(start = 35.dp)
                .constrainAs(more) {}, shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "More Options")
            }
            Button(onClick = {
                mainViewModel.addEvent(Event(
                    fname = firstName,
                    lname = lastName,
                    note = eventNote,
                    category = eventCategory,
                    date =date.toString(), toRemind = date.toString(),
                ))
            }, modifier = Modifier
                .padding(end = 35.dp)
                .constrainAs(confirm) {}, shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Confirm")
            }
        }

        if (moreOptions) {
            AlertDialog(onDismissRequest = {
                moreOptions = false
                phNumber = ""
                remindDate = null
            }, confirmButton = {
                Button(onClick = {
                    moreOptions = false
                    if (phNumber != "" && phNumber.length == 10) {
                        mainViewModel.addNote(Note(notesToSend = notesToSend, number = phNumber, sent = true))
                    } else {
                        Toast.makeText(context, "Invalid Mobile Number", Toast.LENGTH_LONG).show()
                    }
                }, shape = RoundedCornerShape(10.dp)) {
                    Text(text = "Confirm")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    moreOptions = false
                    phNumber = ""
                    //TODO ENTER DETAILS AFTER CONVERTING INTO LOCALDATE FORMAT
                }) {
                    Text(text = "Remind Only")
                }
            }, title = {
                Text(text = "Add details")
            }, text = {
                ConstraintLayout() {
                    val (num, tim,tim2) = createRefs()
                    val mvChain = createVerticalChain(num, tim,tim2, chainStyle = ChainStyle.SpreadInside)
                    ExposedDropdownMenuBox(expanded = dddm,
                        onExpandedChange = { dddm = !dddm }, modifier = Modifier
                            .padding(10.dp)
                            .constrainAs(num) {}) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            value = tillDate,
                            onValueChange = { tillDate = it },
                            label = { Text(text = "Remind Me Ahead") },
                            trailingIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        if (dddm == true) {
                                            Icons.Filled.KeyboardArrowUp
                                        } else {
                                            Icons.Filled.KeyboardArrowDown
                                        }, contentDescription = ""
                                    )
                                }
                            })
                        ExposedDropdownMenu(expanded = dddm, onDismissRequest = { dddm = false }) {
                            times.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(text = selectionOption) },
                                    onClick = {
                                        if(selectionOption==times[0]){remindMeAt=1}
                                        else if(selectionOption==times[1]){remindMeAt=2}
                                        else if(selectionOption==times[2]){remindMeAt=3}
                                        else if(selectionOption==times[3]){remindMeAt=4}
                                        else if(selectionOption==times[4]){remindMeAt=5}
                                        dddm = false
                                    })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = phNumber,
                        onValueChange = { phNumber = it },
                        label = { Text("Mobile Number") },
                        placeholder = {
                            Text(
                                text = "Optional"
                            )
                        }, modifier = Modifier
                            .padding(10.dp)
                            .constrainAs(tim) {})
                    OutlinedTextField(
                        value = notesToSend,
                        onValueChange = { notesToSend = it },
                        label = { Text("Notes To Send") },
                        placeholder = {
                            if(eventCategory=="Birthday"){notesToSend=placeHolders[0]}
                            else if(eventCategory=="Wedding"){notesToSend=placeHolders[1]}
                            else if(eventCategory=="Memorial"){notesToSend=placeHolders[2]}
                            else if(eventCategory=="Other"){notesToSend=placeHolders[3]}
                            Text(
                                text = notesToSend
                            )
                        }, modifier = Modifier
                            .padding(10.dp)
                            .constrainAs(tim2) {})
                }
            })
        }
    }
}
