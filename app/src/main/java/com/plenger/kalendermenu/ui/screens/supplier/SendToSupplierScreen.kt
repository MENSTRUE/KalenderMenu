package com.plenger.kalendermenu.ui.screens.supplier

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.plenger.kalendermenu.data.OrderRepository
import com.plenger.kalendermenu.ui.components.KmCard
import com.plenger.kalendermenu.ui.components.KmIngredientChip
import com.plenger.kalendermenu.ui.components.KmSecondaryButton
import com.plenger.kalendermenu.ui.components.KmTopBar
import com.plenger.kalendermenu.ui.components.KmWhatsAppButton
import com.plenger.kalendermenu.ui.navigation.Screen
import com.plenger.kalendermenu.ui.theme.*

data class SupplierData(
    val nama: String,
    val lokasi: String,
    val nomorWa: String
)

@Composable
fun SendToSupplierScreen(
    navController: NavController,
    orderId: Long
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val repository = remember { OrderRepository.getInstance(context) }
    val order = remember(orderId) { repository.getOrderById(orderId) }

    val menuName = order?.menuName ?: "Menu Belum Dipilih"
    val ingredients = order?.ingredients ?: emptyList()

    val supplierList = remember {
        listOf(
            SupplierData("Pak Muflihin", "Pasar Tradisional", "6285648804502"),
            SupplierData("Pak Wowo", "Pasar Modern", "62881036850480"),
            SupplierData("Pak Cesar", "Distributor Bahan", "6285156461831")
        )
    }

    var selectedSupplier by remember { mutableStateOf(supplierList[0]) }
    var showSupplierPicker by remember { mutableStateOf(false) }
    var showCopiedSnack by remember { mutableStateOf(false) }

    val draftMessage = buildString {
        appendLine("Halo ${selectedSupplier.nama} 👋")
        appendLine("Saya mau pesan bahan untuk *Menu $menuName*:")
        if (ingredients.isNotEmpty()) {
            ingredients.forEach { item ->
                appendLine("• $item")
            }
        } else {
            appendLine("• Bahan Baku Utama")
            appendLine("• Bumbu Dasar & Rempah")
        }
        appendLine()
        appendLine("Mohon info rincian harganya untuk hari ini.")
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
                        val phone = selectedSupplier.nomorWa
                        val encoded = Uri.encode(draftMessage)
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encoded")
                        )
                        context.startActivity(intent)
                    }
                )

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Kembali", color = NeutralWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                KmSecondaryButton(
                    text = "Update Harga dari Supplier",
                    onClick = { navController.navigate(Screen.UpdateIngredientPrice.createRoute(orderId)) },
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
                    Icon(Icons.Outlined.Receipt, null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Draf Pesan Belanja", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NeutralBlack)
                }
                Spacer(Modifier.height(10.dp))
                KmIngredientChip(name = menuName)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dibuat otomatis · KalenderMenu", fontSize = 12.sp, color = NeutralLightGray)
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(draftMessage))
                            showCopiedSnack = true
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Salin Teks Saja",
                            color = TealPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        )
                    }
                }
            }

            KmCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Person, null, tint = TealPrimary, modifier = Modifier.size(20.dp))
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
                                .background(TealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                selectedSupplier.nama.first().toString(),
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(selectedSupplier.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeutralBlack)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${selectedSupplier.lokasi} · +${selectedSupplier.nomorWa}",
                                fontSize = 13.sp,
                                color = NeutralMidGray
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showSupplierPicker = true },
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
                        Icon(Icons.Default.CheckCircle, null, tint = StatusGreen)
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

    if (showSupplierPicker) {
        SupplierPickerDialog(
            suppliers = supplierList,
            selectedSupplier = selectedSupplier,
            onSelect = { supplier ->
                selectedSupplier = supplier
                showSupplierPicker = false
            },
            onDismiss = { showSupplierPicker = false }
        )
    }
}

@Composable
private fun SupplierPickerDialog(
    suppliers: List<SupplierData>,
    selectedSupplier: SupplierData,
    onSelect: (SupplierData) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var newNama by remember { mutableStateOf("") }
    var newLokasi by remember { mutableStateOf("") }
    var newNomor by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NeutralWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Pilih Supplier", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeutralBlack)
                Spacer(Modifier.height(14.dp))

                suppliers.forEach { supplier ->
                    val isSelected = supplier.nama == selectedSupplier.nama
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TealContainer else NeutralWhite)
                            .clickable { onSelect(supplier) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                supplier.nama.first().toString(),
                                color = NeutralWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(supplier.nama, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = NeutralBlack)
                            Text(
                                "${supplier.lokasi} · +${supplier.nomorWa}",
                                fontSize = 13.sp,
                                color = NeutralMidGray
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                    HorizontalDivider(color = NeutralDivider, modifier = Modifier.padding(vertical = 4.dp))
                }

                if (showAddForm) {
                    Spacer(Modifier.height(8.dp))
                    Text("Tambah Supplier Baru", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TealPrimary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNama, onValueChange = { newNama = it },
                        label = { Text("Nama Supplier") }, modifier = Modifier.fillMaxWidth(),
                        singleLine = true, shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = NeutralDivider)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLokasi, onValueChange = { newLokasi = it },
                        label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth(),
                        singleLine = true, shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = NeutralDivider)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNomor,
                        onValueChange = { v -> newNomor = v.filter { it.isDigit() } },
                        label = { Text("Nomor WhatsApp (628xxx)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = NeutralDivider)
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (newNama.isNotBlank() && newNomor.isNotBlank()) {
                                val newS = SupplierData(newNama.trim(), newLokasi.trim(), newNomor.trim())
                                onSelect(newS)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Simpan & Pilih", color = NeutralWhite) }
                }

                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { showAddForm = !showAddForm }) {
                        Text(
                            if (showAddForm) "Batal Tambah" else "+ Tambah Supplier",
                            color = TealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Tutup", color = NeutralMidGray)
                    }
                }
            }
        }
    }
}