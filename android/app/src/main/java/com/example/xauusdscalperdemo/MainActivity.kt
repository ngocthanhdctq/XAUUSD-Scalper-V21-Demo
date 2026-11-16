
package com.example.xauusdscalperdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Bg = Color(0xFF101722)
private val Panel = Color(0xFF1B2736)
private val Green = Color(0xFF20C997)
private val Red = Color(0xFFFF647C)
private val White = Color(0xFFF4F7FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DemoDashboard()
            }
        }
    }
}

@Composable
fun DemoDashboard() {
    var auto by remember { mutableStateOf(false) }
    var balance by remember { mutableStateOf(1000.0) }
    var trades by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Sẵn sàng mô phỏng") }

    // Giới hạn rủi ro tổng của EA
    var riskPercent by remember { mutableFloatStateOf(2f) }

    // Chế độ tính Lot
    var lotMode by remember { mutableStateOf("AUTO") }

    // MANUAL nhập phần trăm Balance, không nhập số Lot
    var manualPercentText by remember { mutableStateOf("1") }

    var showHistory by remember { mutableStateOf(false) }

    val history = remember { mutableStateListOf<String>() }
    val tradeHistory = remember { mutableStateListOf<String>() }

    val riskBudget = balance * riskPercent / 100.0
    val manualPercent = manualPercentText
        .replace(",", ".")
        .toDoubleOrNull()

    val manualRiskUsd = if (manualPercent != null && manualPercent > 0) {
        balance * manualPercent / 100.0
    } else {
        0.0
    }

    val manualValid = manualPercent != null &&
        manualPercent in 0.1..10.0

    val manualWithinLimit = manualValid &&
        manualPercent!! <= riskPercent

    val selectedRiskPercent = if (lotMode == "AUTO") {
        1.0
    } else {
        manualPercent ?: 0.0
    }

    val selectedRiskUsd = balance * selectedRiskPercent / 100.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "XAUUSD SCALPER",
            color = Green,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "VERSION 2.1 • DEMO MODE",
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoCard(
                "BALANCE",
                "$" + String.format(Locale.US, "%.2f", balance),
                Modifier.weight(1f)
            )
            InfoCard(
                "EQUITY",
                "$" + String.format(Locale.US, "%.2f", balance),
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        InfoCard(
            "TÀI KHOẢN",
            "DEMO • MÔ PHỎNG",
            Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // QUẢN LÝ VỐN VÀ LOT
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "QUẢN LÝ VỐN & RỦI RO",
                    color = Green,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    "RỦI RO TỐI ĐA TOÀN BỘ LỆNH EA",
                    color = White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tỷ lệ rủi ro tổng", color = Color.LightGray)
                    Text(
                        String.format(Locale.US, "%.1f%%", riskPercent),
                        color = Green,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = riskPercent,
                    onValueChange = { riskPercent = it },
                    valueRange = 2f..10f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = Green,
                        activeTrackColor = Green
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("2%", color = Color.LightGray)
                    Text("10%", color = Color.LightGray)
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "NGÂN SÁCH RỦI RO TỐI ĐA",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Text(
                    "$" + String.format(Locale.US, "%.2f", riskBudget),
                    color = Green,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tính theo Balance: $" +
                        String.format(Locale.US, "%.2f", balance),
                    color = White,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = Color.DarkGray)
                Spacer(Modifier.height(14.dp))

                Text(
                    "CHẾ ĐỘ TÍNH LOT",
                    color = Green,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { lotMode = "AUTO" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lotMode == "AUTO") Green else Color.DarkGray
                        )
                    ) {
                        Text("AUTO LOT", color = Color.Black)
                    }

                    Button(
                        onClick = { lotMode = "MANUAL" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lotMode == "MANUAL") Green else Color.DarkGray
                        )
                    ) {
                        Text("MANUAL LOT", color = Color.Black)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (lotMode == "AUTO") {
                    Text(
                        "AUTO LOT: Mặc định 1% Balance cho lệnh đầu tiên.",
                        color = White
                    )
                    Text(
                        "Ngân sách mục tiêu: $" +
                            String.format(Locale.US, "%.2f", balance * 0.01),
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "NHẬP TỶ LỆ RỦI RO THEO BALANCE (%)",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = manualPercentText,
                        onValueChange = { input ->
                            if (input.length <= 7 &&
                                input.all { it.isDigit() || it == '.' || it == ',' }
                            ) {
                                manualPercentText = input
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Phần trăm Balance (%)") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = Green,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Green,
                            unfocusedLabelColor = Color.LightGray,
                            cursorColor = Green
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Ngân sách rủi ro mục tiêu",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        "$" + String.format(Locale.US, "%.2f", manualRiskUsd),
                        color = Green,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Ví dụ: Balance 1.000 USD, nhập 1 = ngân sách 10 USD.",
                        color = White,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    when {
                        !manualValid -> {
                            Text(
                                "Nhập tỷ lệ từ 0,1% đến 10%.",
                                color = Red,
                                fontSize = 12.sp
                            )
                        }

                        !manualWithinLimit -> {
                            Text(
                                "Vượt giới hạn rủi ro tổng hiện tại (${riskPercent}%). EA phải từ chối hoặc giới hạn lệnh.",
                                color = Red,
                                fontSize = 12.sp
                            )
                        }

                        else -> {
                            Text(
                                "Tỷ lệ MANUAL đang nằm trong giới hạn rủi ro tổng.",
                                color = Green,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "LOT THỰC TẾ",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Text(
                    "EA tính theo Entry – SL thực tế trên MT5",
                    color = Green,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Không quy đổi cố định 10 USD = 0.01 Lot. Lot phụ thuộc khoảng cách SL và thông số hợp đồng của broker.",
                    color = White,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "PHẠM VI TÍNH RỦI RO",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Text(
                    "Chỉ lệnh XAUUSD do EA Scalper V2.1 mở",
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Tối đa 10 lệnh: 1 lệnh đầu + 9 lệnh bổ sung.",
                    color = White,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "EA CONNECTION: CHƯA KẾT NỐI",
                    color = Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Thông số hiện tại chỉ là mô phỏng, chưa gửi sang MT5.",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // AUTO TRADING
        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "AUTO TRADING",
                    color = White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (auto) "AUTO ĐANG BẬT" else "AUTO ĐANG TẮT",
                        color = if (auto) Green else Red
                    )
                    Switch(
                        checked = auto,
                        onCheckedChange = {
                            auto = it
                            message = if (it) {
                                "AUTO Demo đã bật"
                            } else {
                                "AUTO Demo đã tắt"
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "MANUAL TRADING",
            color = White,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Chế độ Lot: $lotMode • Ngân sách mục tiêu: $" +
                String.format(Locale.US, "%.2f", selectedRiskUsd),
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    if (lotMode == "MANUAL" && !manualWithinLimit) {
                        message = "Không thể mô phỏng: tỷ lệ MANUAL không hợp lệ hoặc vượt giới hạn."
                    } else {
                        trades++
                        val entry = "BUY XAUUSD • DEMO • $lotMode"
                        history.add(entry)
                        tradeHistory.add("MỞ LỆNH • $entry")
                        message = "Đã mô phỏng lệnh BUY"
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text("BUY")
            }

            Button(
                onClick = {
                    if (lotMode == "MANUAL" && !manualWithinLimit) {
                        message = "Không thể mô phỏng: tỷ lệ MANUAL không hợp lệ hoặc vượt giới hạn."
                    } else {
                        trades++
                        val entry = "SELL XAUUSD • DEMO • $lotMode"
                        history.add(entry)
                        tradeHistory.add("MỞ LỆNH • $entry")
                        message = "Đã mô phỏng lệnh SELL"
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Red)
            ) {
                Text("SELL")
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                val count = history.size
                history.forEach { entry ->
                    tradeHistory.add("ĐÃ ĐÓNG • $entry")
                }
                history.clear()
                message = "Đã mô phỏng đóng $count lệnh"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ĐÓNG TẤT CẢ LỆNH MÔ PHỎNG")
        }

        Spacer(Modifier.height(14.dp))

        Text(
            "TRẠNG THÁI",
            color = Green,
            fontWeight = FontWeight.Bold
        )
        Text(message, color = White, fontSize = 13.sp)

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TỔNG LỆNH: $trades", color = White)
            TextButton(onClick = { showHistory = true }) {
                Text("LỊCH SỬ", color = Green)
            }
        }

        HorizontalDivider(color = Color.DarkGray)

        history.reversed().forEach { item ->
            Text(
                item,
                color = White,
                modifier = Modifier.padding(vertical = 9.dp)
            )
            HorizontalDivider(color = Color.DarkGray)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "SIMULATION ONLY • NO REAL ORDERS",
            color = Green,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text("LỊCH SỬ GIAO DỊCH") },
            text = {
                if (tradeHistory.isEmpty()) {
                    Text("Chưa có giao dịch nào.")
                } else {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        tradeHistory.reversed().forEach { item ->
                            Text(
                                item,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistory = false }) {
                    Text("ĐÓNG")
                }
            }
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = Color.LightGray, fontSize = 11.sp)
            Spacer(Modifier.height(5.dp))
            Text(
                value,
                color = White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
