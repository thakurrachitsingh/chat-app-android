package com.example.bottomnavigationcomposeui.pages.login

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.bottomnavigationcomposeui.datamodel.User
import com.example.bottomnavigationcomposeui.pages.home.util.KtorClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(InternalAPI::class)
class LoginViewModel(): ViewModel() {
    lateinit var mAuth: FirebaseAuth
    lateinit var activityContext: Activity
    lateinit var ktorClient: KtorClient

    fun login(inputEmail: String, inputPassword: String, response: MutableState<Boolean?>, userName: String){
        mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(activityContext) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("login", "signInWithEmail:success")
                    response.value = true
                    val sharedPref = activityContext.getSharedPreferences("userSharedPref", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("userLoggedIn", true).apply()
                    sharedPref.edit().putString("email", inputEmail).apply()
                    sharedPref.edit().putString("password", inputPassword).apply()
                    sharedPref.edit().putString("userName", userName).apply()
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        activityContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.d("login", "Error: ${task.exception}")
//                    updateUI(null)
                }
            }
    }

    fun signUp(
        inputEmail: String,
        inputPassword: String,
        response: MutableState<Boolean?>,
        userName: String
    ){
        mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(activityContext) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    registerUserOnDB(userName, inputEmail, userName, response)
                    Log.d("login", "createUserWithEmail:success")
                    val sharedPref = activityContext.getSharedPreferences("userSharedPref", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("userLoggedIn", true).apply()
                    sharedPref.edit().putString("email", inputEmail).apply()
                    sharedPref.edit().putString("password", inputPassword).apply()
                    sharedPref.edit().putString("userName", userName).apply()
                    val user = mAuth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        activityContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.d("login", "Error: ${task.exception}")
//                    updateUI(null)
                }
            }
    }

    @OptIn(InternalAPI::class)
    private fun registerUserOnDB(
        name: String,
        inputEmail: String,
        userName: String,
        res: MutableState<Boolean?>
    ) {
        val io = CoroutineScope(Dispatchers.IO)
        io.launch {
            ktorClient = KtorClient()
            val dataToSet = User("", name, inputEmail, userName)
            val response = ktorClient.client.post("https://chat-app-backend-db.vercel.app/user/register") {
                contentType(ContentType.Application.Json)
                setBody(dataToSet)
            }
            res.value = true
            Log.d("LoginViewModel", response.toString());
        }
    }
}