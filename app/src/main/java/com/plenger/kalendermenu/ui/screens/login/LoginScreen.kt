package com.plenger.kalendermenu.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralDivider
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.StatusRed
import com.plenger.kalendermenu.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {

    var username    by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var showPass    by remember { mutableStateOf(false) }
    var isLoading   by remember { mutableStateOf(false) }
    var errorMsg    by remember { mutableStateOf<String?>(null) }
    val scope       = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Demo credentials
    val DEMO_USER = "admin"
    val DEMO_PASS = "kalender123"

    fun doLogin() {
        errorMsg = null
        if (username.isBlank() || password.isBlank()) {
            errorMsg = "Username dan password tidak boleh kosong."
            return
        }
        scope.launch {
            isLoading = true
            delay(800L) // simulate network
            if (username.trim() == DEMO_USER && password == DEMO_PASS) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            } else {
                errorMsg = "Username atau password salah.\nDemo: admin / kalender123"
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TealPrimary)
    ) {
        // Top teal header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NeutralWhite.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = NeutralWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "KalenderMenu",
                color = NeutralWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Masuk ke akun katering Anda",
                color = NeutralWhite.copy(alpha = 0.75f),
                fontSize = 14.sp
            )
        }

        // Bottom white card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = NeutralBackground),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Masuk",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralBlack
                )
                Text(
                    "Silakan masukkan username dan password Anda",
                    fontSize = 14.sp,
                    color = NeutralMidGray
                )

                Spacer(Modifier.height(4.dp))

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMsg = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    placeholder = { Text("Contoh: admin", color = NeutralLightGray) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, null, tint = TealPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        focusedLabelColor = TealPrimary,
                        unfocusedBorderColor = NeutralDivider
                    )
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    placeholder = { Text("Masukkan password", color = NeutralLightGray) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null, tint = TealPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                null,
                                tint = NeutralMidGray
                            )
                        }
                    },
                    visualTransformation = if (showPass) VisualTransformation.None
                                           else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        focusedLabelColor = TealPrimary,
                        unfocusedBorderColor = NeutralDivider
                    )
                )

                // Error message
                if (errorMsg != null) {
                    Text(
                        errorMsg!!,
                        color = StatusRed,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }


                Spacer(Modifier.height(4.dp))

                // Login button
                Button(
                    onClick = { doLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = NeutralWhite
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = NeutralWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Masuk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralWhite
                        )
                    }
                }

                // Lupa password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lupa password?", fontSize = 14.sp, color = NeutralMidGray)
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = { /* TODO: reset password */ }) {
                        Text("Hubungi Admin", fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }

                Text(
                    "KalenderMenu v1.0",
                    fontSize = 11.sp,
                    color = NeutralLightGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
