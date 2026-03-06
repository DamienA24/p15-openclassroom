package com.openclassroom.p15.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import android.content.Intent
import com.openclassroom.p15.ui.components.AuthButton

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result, onLoginSuccess) { error ->
            errorMessage = error
        }
    }

    val googleSignInIntent = remember {
        createSignInIntent(AuthUI.IdpConfig.GoogleBuilder().build())
    }

    val emailSignInIntent = remember {
        createSignInIntent(AuthUI.IdpConfig.EmailBuilder().build())
    }

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                onLoginSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = com.openclassroom.p15.R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo Eventorias",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Eventorias",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        AuthButton(
            text = "Sign in with Google",
            logoResId = com.openclassroom.p15.R.drawable.googlelogo,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            onClick = { signInLauncher.launch(googleSignInIntent) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Sign in with email",
            logoResId = com.openclassroom.p15.R.drawable.emaillogo,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = { signInLauncher.launch(emailSignInIntent) }
        )

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

private fun onSignInResult(
    result: FirebaseAuthUIAuthenticationResult,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (result.resultCode == Activity.RESULT_OK) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            onSuccess()
        }
    } else {
        val error = result.idpResponse?.error
        val errorMsg = when {
            error != null -> "Erreur de connexion: ${error.localizedMessage}"
            else -> "Connexion annulée"
        }
        onError(errorMsg)
    }
}

private fun createSignInIntent(provider: AuthUI.IdpConfig): Intent {
    val providers = arrayListOf(provider)
    return AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()
}
