package com.plenger.kalendermenu.ui.screens.supplier

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmIngredientChip
import com.plenger.kalendermenu.ui.components.KmSecondaryButton
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.components.KmWhatsAppButton
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.NeutralBackground
import com.plenger.kalendermenu.ui.theme.NeutralBlack
import com.plenger.kalendermenu.ui.theme.NeutralLightGray
import com.plenger.kalendermenu.ui.theme.NeutralMidGray
import com.plenger.kalendermenu.ui.theme.NeutralSurface
import com.plenger.kalendermenu.ui.theme.NeutralWhite
import com.plenger.kalendermenu.ui.theme.StatusGreen
import com.plenger.kalendermenu.ui.theme.StatusGreenLight
import com.plenger.kalendermenu.ui.theme.TealPrimary

@Composable
fun SendToSupplierScreen(
    navController: NavController,
    orderId: Long
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    var showCopiedSnack by remember { mutableStateOf(false) }

    val supplierName = "Pak Budi"
    val supplierPhone = "6281234567890"
    val menuName = "Rendang Sapi"
    val portions = 50

    val draftMessage = buildString {
        appendLine("Halo $supplierName 👋")
        appendLine("Saya mau pesan bahan untuk")
        appendLine("*$portions porsi $menuName:*")
        appendLine("• Daging Sapi: 5 kg")
        appendLine("• Santan: 3 liter")
        appendLine("• Cabai Merah: 500 gr")
        appendLine("• Serai: 10 batang")
        appendLine("• Bawang Merah: 300 gr")
        appendLine()
        appendLine("Mohon info harga hari ini.")
        append("Terima kasih 🙏")
    }

    Scaffold(
        containerColor = NeutralBackground,
        topBar = {
            KmTopBar(
                title = "Kirim ke Supplier",
                onBackClick = { navController.popBackStack() },
                actionIcon = Icons.Outlined.ChatBubbleOutline,
                onActionClick = {}
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeutralWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KmWhatsAppButton(
                    text = "Buka WhatsApp & Kirim",
                    onClick = {
                        val encoded = Uri.encode(draftMessage)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$supplierPhone?text=$encoded"))
                        context.startActivity(intent)
                    }
                )
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(draftMessage))
                        showCopiedSnack = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Salin Teks Saja",
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                }
                KmSecondaryButton(
                    text = "Update Harga dari Supplier",
                    onClick = { navController.navigate(Screen.UpdateIngredientPrice.createRoute(1L)) },
                    icon = Icons.Default.Edit
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Receipt, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Draf Pesan Belanja", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NeutralBlack)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KmIngredientChip(name = "$portions Porsi")
                    KmIngredientChip(name = menuName)
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = NeutralBackground
                ) {
                    Text(
                        text = draftMessage,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 15.sp,
                        color = NeutralBlack,
                        lineHeight = 22.sp
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Dibuat otomatis · KalenderMenu",
                    fontSize = 12.sp,
                    color = NeutralLightGray
                )
            }

            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Supplier Terpilih", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NeutralBlack)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeutralSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = NeutralMidGray, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(supplierName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack)
                            Spacer(Modifier.height(2.dp))
                            Text("Pasar Tradisional · +62 812-3456-7890", fontSize = 13.sp, color = NeutralMidGray)
                        }
                    }
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Ganti", color = TealPrimary, fontSize = 14.sp)
                    }
                }
            }

            if (showCopiedSnack) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusGreenLight)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                        Spacer(Modifier.width(8.dp))
                        Text("Teks berhasil disalin!", color = StatusGreen, fontSize = 15.sp)
                    }
                }
                LaunchedEffect(showCopiedSnack) {
                    kotlinx.coroutines.delay(2000)
                    showCopiedSnack = false
                }
            }
        }
    }
}