package com.example.bottomnavigationcomposeui.pages.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OTPScreen(modifier: Modifier, phoneNumber: String){
    val focusManager = LocalFocusManager.current
    val otp =
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray)){
        Column(modifier = modifier.fillMaxSize()) {
            Text(text = "Enter OTP sent to $phoneNumber")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for(i in 0..5){
                    val otp = rememberSaveable{ mutableStateOf("") }
                    TextField(value = otp.value.toString(), onValueChange = {
                        if((it.isEmpty() || it.matches(Regex("^\\d+\$")) && otp.value.isEmpty())){
                            otp.value = it
                        }
                        if(it.length==1 && i<5){
                            focusManager.moveFocus(FocusDirection.Right)
                        }
                    },
                        Modifier
                            .clip(shape = RectangleShape)
                            .width(45.dp),
                        keyboardActions = KeyboardActions.Default,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            Button(onClick = {  }) {
                Text(text = "Continue")
            }
        }
    }
}

fun signup(){

}