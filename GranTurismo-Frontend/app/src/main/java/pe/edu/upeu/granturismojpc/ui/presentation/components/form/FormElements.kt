package pe.edu.upeu.granturismojpc.ui.presentation.components.form


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.k0shk0sh.compose.easyforms.EasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsErrorState
import com.github.k0shk0sh.compose.easyforms.EasyFormsValidationType
import com.github.k0shk0sh.compose.easyforms.EmailValidationType
import com.github.k0shk0sh.compose.easyforms.NameValidationType
import com.github.k0shk0sh.compose.easyforms.PasswordValidationType
import com.github.k0shk0sh.compose.easyforms.PhoneNumberValidationType
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

enum class MyFormKeys {
    EMAIL, PASSWORD, SALUTATION, SALUTATION2,SALUTATION3,SALUTATION4,NAME, URL, CUSTOM_FOCUS,
    PHONE, CARD, CHECKBOX, LIST_CHECKBOX, TRI_CHECKBOX, RADIO_BUTTON,
    SWITCH, SLIDER, RANGE_SLIDER,DNI, APE_PAT, APE_MAT, FECHA, TIME, TIME_TOLER, MATERIALES, VALIDINSCRIP, ASISSUBACT, ENTSAL, OFFLINE,
    CUI,TIPOCUI,MATERENTRE,HORAREG,MODFH,ACTIVIDADID, PU,PU_OLD, UTILIDAD, STOCK, STOCK_OLD,
    DESCRIPTION, LOCATION, DATE2, RATING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    easyForm: EasyForms,
    label:String,
    textv: String,
    list:List<ComboModel>
) {
    val state = easyForm.addAndGetCustomState(
        MyFormKeys.SALUTATION, MyEasyFormsCustomStringState(
            validData = list
        )
    )
    val text = state.state
    val isOpen = state.isOpen

    // Solo se ejecuta una vez si textv tiene valor inicial
    LaunchedEffect(textv, list) {
        if (textv.isNotEmpty()) {
            list.find { it.code.trim() == textv.trim() }?.let {
                text.value = it.code+"-"+it.name
            }
        }
    }
    Box{
        Column {
            TextField(
                value = text.value,
                onValueChange = state.onValueChangedCallback,
                label = { Text(text = label) },
                placeholder = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable { state.onClick() }
                    )
                }
            )
            Spacer(size=2)
            DropDownList(
                state = state,
                requestToOpen = isOpen.value,
                list = list,
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = state.onClick)
        )
    }
}

@Composable
private fun DropDownList(
    state: MyEasyFormsCustomStringState,
    requestToOpen: Boolean = false,
    list: List<ComboModel>,
) {
    DropdownMenu(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        expanded = requestToOpen,
        onDismissRequest = state.onDismissed,
    ) {
        list.forEach {
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                text = {Text(
                    text = it.name,
                    modifier = Modifier.wrapContentWidth(),
                )},
                onClick = { state.onValueChangedCallback(if(it.code==it.name) it.code else it.code+"-"+it.name) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxTwo(
    easyForm: EasyForms,
    label:String,
    textv: String,
    list:List<ComboModel>
) {
    val state = easyForm.addAndGetCustomState(
        MyFormKeys.SALUTATION2, MyEasyFormsCustomStringState(
            validData = list
        )
    )
    val text = state.state
    val isOpen = state.isOpen
    LaunchedEffect(textv, list) {
        if (textv.isNotEmpty()) {
            list.find { it.code.trim() == textv.trim() }?.let {
                text.value = it.code+"-"+it.name
            }
        }
    }
    /*if (textv != "") {
        val seleccionado = list.find { it.code == textv }
        text.value = seleccionado!!.name
    }*/
    Box{
        Column {
            TextField(
                value = text.value,
                onValueChange = state.onValueChangedCallback,
                label = { Text(text = label) },
                placeholder = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable { state.onClick() }
                    )
                }
            )
            Spacer(size=2)
            DropDownList(
                state = state,
                requestToOpen = isOpen.value,
                list = list,
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = state.onClick)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxThre(
    easyForm: EasyForms,
    label:String,
    textv: String,
    list:List<ComboModel>
) {
    val state = easyForm.addAndGetCustomState(
        MyFormKeys.SALUTATION3, MyEasyFormsCustomStringState(
            validData = list
        )
    )
    val text = state.state
    val isOpen = state.isOpen
    LaunchedEffect(textv, list) {
        if (textv.isNotEmpty()) {
            list.find { it.code.trim() == textv.trim() }?.let {
                text.value = it.code+"-"+it.name
            }
        }
    }
    /*if (textv != "") {
        val seleccionado = list.find { it.code == textv }
        text.value = seleccionado!!.name
    }*/
    Box{
        Column {
            TextField(
                value = text.value,
                onValueChange = state.onValueChangedCallback,
                label = { Text(text = label) },
                placeholder = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable { state.onClick() }
                    )
                }
            )
            Spacer(size=2)
            DropDownList(
                state = state,
                requestToOpen = isOpen.value,
                list = list,
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = state.onClick)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxFour(
    easyForm: EasyForms,
    label:String,
    textv: String,
    list:List<ComboModel>
) {
    val state = easyForm.addAndGetCustomState(
        MyFormKeys.SALUTATION4, MyEasyFormsCustomStringState(
            validData = list
        )
    )
    val text = state.state
    val isOpen = state.isOpen
    LaunchedEffect(textv, list) {
        if (textv.isNotEmpty()) {
            list.find { it.code.trim() == textv.trim() }?.let {
                text.value = it.code+"-"+it.name
            }
        }
    }
    /*if (textv != "") {
        val seleccionado = list.find { it.code == textv }
        text.value = seleccionado!!.name
    }*/
    Box{
        Column {
            TextField(
                value = text.value,
                onValueChange = state.onValueChangedCallback,
                label = { Text(text = label) },
                placeholder = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.clickable { state.onClick() }
                    )
                }
            )
            Spacer(size=2)
            DropDownList(
                state = state,
                requestToOpen = isOpen.value,
                list = list,
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = state.onClick)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuCustom(
    easyForm: EasyForms,
    label:String,
    textv: String,
    list:List<ComboModel>,
    key:MyFormKeys,
) {
    val state = easyForm.addAndGetCustomState(
        key, MyEasyFormsCustomStringState(
            validData = list
        )
    )
    val text = state.state
    val isOpen = state.isOpen
    LaunchedEffect(textv, list) {
        if (textv.isNotEmpty()) {
            list.find { it.code.trim() == textv.trim() }?.let {
                text.value = it.name
            }
        }
    }
    /*if (textv != "") {
        val seleccionado = list.find { it.code == textv }
        text.value = seleccionado!!.name
    }*/

    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf(list.first()) }

    Log.i("DATAXXX",text.value )
    Box{
        Column {
            TextField(
                value = text.value,
                onValueChange = state.onValueChangedCallback,
                label = { Text(text = label) },
                placeholder = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(size=2)
            DropdownMenu(expanded = state.isOpen.value,
                onDismissRequest = state.onDismissed) {
                list.forEach { category ->
                    DropdownMenuItem(onClick = {
                        selectedCategory.value = category
                        state.onValueChangedCallback(if(category.code==category.name) category.code else category.code+"-"+category.name)
                    }, text = {Text(text = category.name)}
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = state.onClick)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerCustom(
    easyForm: EasyForms,
    label:String,
    texts: String,
    key:MyFormKeys,
    formDP:String
) {
    val state = easyForm.addAndGetCustomState(
        key, MyEasyFormsCustomDateState(
            defaultValue=texts,
            easyFormsValidationType = NameValidationType
        )
    )
    val text = state.state
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val year: Int
    val month: Int
    val day: Int
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    val date = remember { mutableStateOf("") }
    val themeResId: Int = 0
    val datePickerDialog = DatePickerDialog(
        context,
        themeResId,
        { _: DatePicker, yyyy: Int, mm: Int, dd: Int ->
            val c = Calendar.getInstance()
            c.set(yyyy, mm, dd, 0, 0);
            val d = c.time
            date.value = dateShort(formDP,d)
            text.value= date.value
        },
        year,
        month,
        day
    )
    //datePickerDialog.datePicker.locale = Locale("es")
    datePickerDialog.setOnDismissListener {
        focusManager.clearFocus()
        Log.i("VERRRD", date.value.toString())
        state.errorState.value = when (date.value.toString()!="") {
            true -> EasyFormsErrorState.VALID
            false -> EasyFormsErrorState.INVALID
        }
    }

    OutlinedTextField(
        value =  text.value,
        onValueChange = {
            Log.i("VERRR", "quiza: ")
        },
        label = { Text(text = label) },
        placeholder = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    datePickerDialog.show()
                    Log.i("VERRR", "LLEGO AQUI: ")
                }
            },
        isError = (state.errorState.value == EasyFormsErrorState.INVALID),
    )
}





fun dateShort(formF:String,r: Date?): String {
    return if (r != null) SimpleDateFormat(formF).format(r) else ""
}
fun timeShort(formF:String, r: Date?): String { //"HH:mm:ss"
    return if (r != null) SimpleDateFormat(formF).format(r) else ""
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerCustom(
    easyForm: EasyForms,
    label:String,
    texts: String,
    key:MyFormKeys,
    formDP:String
) {
    val state = easyForm.addAndGetCustomState(
        key, MyEasyFormsCustomDateState(
            defaultValue=texts,
            easyFormsValidationType = NameValidationType
        )
    )
    val text = state.state
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val hora: Int
    val minuto: Int
    val segundo: Int
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    hora = calendar.get(Calendar.HOUR)
    minuto = calendar.get(Calendar.MINUTE)
    segundo=calendar.get(Calendar.SECOND)

    val date = remember { mutableStateOf("") }
    val themeResId: Int = 0

    val timePickerDialog = TimePickerDialog(
        context,
        themeResId,
        { _: TimePicker, hh: Int, mm: Int->
            val c = Calendar.getInstance()
            c.set(0,0,0,hh, mm,0);
            val d = c.time
            date.value = timeShort(formDP, d)
            text.value = date.value
        },
        hora,
        minuto,
        true
    )
    //datePickerDialog.datePicker.locale = Locale("es")
    timePickerDialog.setOnDismissListener {
        focusManager.clearFocus()
        Log.i("VERRRD", date.value.toString())
        state.errorState.value = when (date.value.toString()!="") {
            true -> EasyFormsErrorState.VALID
            false -> EasyFormsErrorState.INVALID
        }
    }

    OutlinedTextField(
        value =  text.value,
        onValueChange = {
            Log.i("VERRR", "quiza: ")
        },
        label = { Text(text = label) },
        placeholder = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    timePickerDialog.show()
                    Log.i("VERRR", "LLEGO AQUI: ")
                }
            },
        isError = (state.errorState.value == EasyFormsErrorState.INVALID),
    )
    Spacer(size=2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerCustom(
    easyForm: EasyForms,
    label: String,
    texts: String,
    key: MyFormKeys,
    formDP: String
) {
    val state = easyForm.addAndGetCustomState(
        key, MyEasyFormsCustomDateState(
            defaultValue = texts,
            easyFormsValidationType = NameValidationType
        )
    )
    val text = state.state
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val calendar = Calendar.getInstance()

    val dateTime = remember { mutableStateOf("") }
    val themeResId: Int = 0

    fun updateDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, day, hour, minute)
        val d = c.time
        dateTime.value = dateShort(formDP, d)
        text.value = dateTime.value
    }

    val timePickerDialog = TimePickerDialog(
        context,
        themeResId,
        { _, hourOfDay, minute ->
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            updateDateTime(year, month, day, hourOfDay, minute)

            // Aquí validamos y cerramos el focus
            focusManager.clearFocus()
            state.errorState.value = if (dateTime.value.isNotEmpty()) {
                EasyFormsErrorState.VALID
            } else {
                EasyFormsErrorState.INVALID
            }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        themeResId,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    /*datePickerDialog.setOnDismissListener {
        focusManager.clearFocus()
        state.errorState.value = if (dateTime.value.isNotEmpty()) {
            EasyFormsErrorState.VALID
        } else {
            EasyFormsErrorState.INVALID
        }
    }*/

    OutlinedTextField(
        value = text.value,
        onValueChange = {},
        label = { Text(text = label) },
        placeholder = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    datePickerDialog.show()
                }
            },
        isError = (state.errorState.value == EasyFormsErrorState.INVALID),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameTextField(easyForms: EasyForms, text: String, label:String, key:MyFormKeys) {
    val textFieldState = easyForms.getTextFieldState(
        key = key,
        easyFormsValidationType = NameValidationType,
        defaultValue = text,
    )
    val state = textFieldState.state
    OutlinedTextField(
        value = state.value,
        onValueChange = textFieldState.onValueChangedCallback,
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        isError = textFieldState.errorState.value == EasyFormsErrorState.INVALID,
    )
    Spacer(size=2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(easyForms: EasyForms, text: String, label:String, tipo:String) {
    val textFieldState = easyForms.getTextFieldState(
        key = MyFormKeys.EMAIL,
        easyFormsValidationType = EmailValidationType,
        defaultValue = text,
    )
    val state = textFieldState.state
    OutlinedTextField(
        value = state.value,
        onValueChange = textFieldState.onValueChangedCallback,
        label = { Text(text = label) },
        modifier = if(tipo=="U") Modifier.wrapContentWidth() else Modifier.fillMaxWidth(),
        isError = textFieldState.errorState.value == EasyFormsErrorState.INVALID,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    easyForms: EasyForms,
    text: String,
    label: String,
    key: String,
    passwordVisibility: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val textFieldState = easyForms.getTextFieldState(
        key = key,
        easyFormsValidationType = PasswordValidationType,
        defaultValue = text,
    )
    val state = textFieldState.state

    OutlinedTextField(
        value = state.value,
        label = { Text(text = label) },
        onValueChange = textFieldState.onValueChangedCallback,
        isError = textFieldState.errorState.value == EasyFormsErrorState.INVALID,
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisibility.value)
                Icons.Filled.Face
            else Icons.Filled.Lock

            IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                Icon(imageVector = image, contentDescription = null)
            }
        }
    )
}

object MyCustomLengthValidationType : EasyFormsValidationType(
    minLength = 8,
    maxLength = 8,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DNITextField(easyForms: EasyForms, text: String, label:String) {
    val textFieldState = easyForms.getTextFieldState(MyFormKeys.DNI,
        MyCustomLengthValidationType,
        defaultValue = text,
    )
    val state = textFieldState.state
    TextField(
        value = state.value,
        onValueChange = textFieldState.onValueChangedCallback,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        isError = textFieldState.errorState.value == EasyFormsErrorState.INVALID,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(easyForms: EasyForms, text: String, label:String) {
    val textFieldState = easyForms.getTextFieldState(
        key = MyFormKeys.PHONE,
        easyFormsValidationType = PhoneNumberValidationType,
        defaultValue = text,
    )
    val state = textFieldState.state
    TextField(
        value = state.value,
        onValueChange = textFieldState.onValueChangedCallback,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        isError = textFieldState.errorState.value == EasyFormsErrorState.INVALID,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitedTextField(
    easyForms: EasyForms,
    text: String,
    label: String,
    formKey: MyFormKeys,
    validationType: EasyFormsValidationType,
    maxLength: Int? = null,
    tipo: String = ""
) {
    val textFieldState = easyForms.getTextFieldState(
        key = formKey,
        easyFormsValidationType = validationType,
        defaultValue = text
    )
    val state = textFieldState.state

    OutlinedTextField(
        value = state.value,
        onValueChange = { newValue ->
            if (maxLength == null || newValue.text.length <= maxLength) {
                textFieldState.onValueChangedCallback(newValue)
            }
        },
        label = { Text(text = label) },
        modifier = if (tipo == "U") Modifier.wrapContentWidth() else Modifier.fillMaxWidth(),
        isError = textFieldState.errorState.value == EasyFormsErrorState.INVALID,
        supportingText = {
            if (maxLength != null) {
                Text("${state.value.text.length} / $maxLength")
            }
        }
    )
}

@Composable
fun RatingBarEasyForms(
    easyForms: EasyForms,
    initialValue: Int,
    formKey: MyFormKeys,
    validationType: EasyFormsValidationType,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp,
    spacing: Dp = 4.dp
) {
    val textFieldState = easyForms.getTextFieldState(
        key = formKey,
        easyFormsValidationType = validationType,
        defaultValue = initialValue.toString()
    )

    val state = textFieldState.state
    val rating = state.value.text.toIntOrNull() ?: 0

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Estrella $i",
                    tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier
                        .size(starSize)
                        .clickable {
                            textFieldState.onValueChangedCallback(TextFieldValue(i.toString()))
                        }
                        .padding(end = spacing)
                )
            }
        }

        if (textFieldState.errorState.value == EasyFormsErrorState.INVALID) {
            Text(
                text = "Seleccione una calificación válida (1 a 5)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AccionButtonSuccess(
    easyForms: EasyForms,
    label:String,
    id:Long,
    onClick: () -> Unit,

    ) {
    val errorStates = easyForms.observeFormStates()
    Log.i("DATOCC", id.toString())
    if(id==0.toLong()){
        Button(
            onClick = onClick,
            modifier = Modifier.wrapContentWidth(),
            enabled = errorStates.value.all {it.value == EasyFormsErrorState.VALID }
        ) {Text(label)}
    }else{
        Button(
            onClick = onClick,
            modifier = Modifier.wrapContentWidth(),
            //enabled = errorStates.value.all {it.value == EasyFormsErrorState.VALID }
        ) {Text(label)}
    }
}

@Composable
fun AccionButtonCancel(
    easyForms: EasyForms,
    label:String,
    onClick: () -> Unit,

    ) {
    val errorStates = easyForms.observeFormStates()
    Button(
        onClick = onClick,
        modifier = Modifier.wrapContentWidth(),
    ) {
        Text(label)
    }
}

@Composable
fun LoginButton(
    easyForms: EasyForms,
    onClick: () -> Unit,
    label:String
) {
    val errorStates = easyForms.observeFormStates()
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(5.dp))
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 200.dp, height = 40.dp),
        enabled = errorStates.value.all {it.value == EasyFormsErrorState.VALID}
    ) {
        Text(label, fontSize = 16.sp)
    }
}

object DescriptionValidationType : EasyFormsValidationType() {
    fun validate(text: String): Boolean {
        return text.isNotBlank() // o simplemente `true` si no quieres validación estricta
    }
}
object RatingValidationType : EasyFormsValidationType() {
    fun validate(text: String): Boolean {
        return text.toIntOrNull()?.let { it in 1..5 } == true
    }
}
