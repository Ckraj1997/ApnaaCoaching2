package com.chandan.apnaacoaching.ui.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

suspend fun triggerGoogleSignIn(context: Context): String? {
    val credentialManager = CredentialManager.create(context)

    val webClientId = "251313184195-83lfljhj8br8tqcljc1fgndlopdc9gmh.apps.googleusercontent.com"
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(true)
        .build()
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    return try {
        val result: GetCredentialResponse = credentialManager.getCredential(
            request = request,
            context = context
        )
        handleSignInResult(result)
    } catch (e: Exception) {
        Log.e("GoogleAuth", "Sign-in failed: ${e.message}")
        null
    }
}

private fun handleSignInResult(result: GetCredentialResponse): String? {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            return googleIdTokenCredential.idToken
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Received an invalid google id token response", e)
        }
    }
    return null
}