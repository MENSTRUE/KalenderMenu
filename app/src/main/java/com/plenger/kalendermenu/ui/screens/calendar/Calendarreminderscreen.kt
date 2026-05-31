package com.plenger.kalendermenu.ui.screens.calendar

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.TealContainer
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun CalendarReminderScreen(
    navController: NavController,
    orderId: Long,
    menuName: String
) {
    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(title = "Pengingat Kalender", onBackClick = { navController.popBackStack() })
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeutralWhite)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // PERBAIKAN 1: Tombol Kembali ke Beranda diganti agar teks dan ikon pasti putih
                Button(
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = NeutralWhite, // Ikon dipaksa putih
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kembali ke Beranda",
                        color = NeutralWhite, // Teks dipaksa putih
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // PERBAIKAN 2: Tombol Kirim ke Supplier sekalian dirapikan posisi ikonnya
                OutlinedButton(
                    onClick = { navController.navigate(Screen.SendToSupplier.createRoute(orderId)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, TealPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TealPrimary // Warna teks dan ikon jadi Teal
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kirim ke Supplier",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = NeutralWhite,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(TealPrimary, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Pengingat Berhasil!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NeutralBlack,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Jadwal memasak sudah tersimpan di\nGoogle Calendar HP Anda.",
                fontSize = 16.sp,
                color = NeutralMidGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.DateRange, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Selasa, 24 Juni 2026",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = NeutralBlack
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "07:00 WIB · Siapkan Masakan $menuName",
                            fontSize = 14.sp,
                            color = NeutralMidGray,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.DateRange, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "Tersimpan di Google Calendar",
                                color = TealPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}