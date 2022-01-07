package com.example.safetravels.data

import android.widget.Toast
import com.example.safetravels.data.model.LoggedInUser
import java.io.IOException



/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val ourUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            return Result.Success(ourUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}