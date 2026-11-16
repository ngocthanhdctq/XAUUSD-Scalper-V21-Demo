
package com.example.xauusdscalperdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var trades by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("Sẵn sàng mô phỏng") }
    val history = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
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
            InfoCard("BALANCE", "$" + "%.2f".format(balance),
                Modifier.weight(1f))
            InfoCard("EQUITY", "$" + "%.2f".format(balance),
                Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        InfoCard(
            "TÀI KHOẢN",
            "DEMO • MÔ PHỎNG",
            Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Panel),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("AUTO TRADING", color = White,
                    fontWeight = FontWeight.Bold)

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
                            message = if (it)
                                "AUTO Demo đã bật"
                            else "AUTO Demo đã tắt"
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text("MANUAL TRADING", color = White,
            fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
    trades++
    val entry = "BUY XAUUSD • DEMO"
    history.add(entry)
    tradeHistory.add("MỞ LỆNH • $entry")
    message = "Đã mô phỏng lệnh BUY"
                }
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {
                Text("BUY")
            }

            Button(
                onClick = {
    trades++
    val entry = "SELL XAUUSD • DEMO"
    history.add(entry)
    tradeHistory.add("MỞ LỆNH • $entry")
    message = "Đã mô phỏng lệnh SELL"
                }
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red
                )
            ) {
                Text("SELL")
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
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

        Spacer(Modifier.height(12.dp))

        Text("TRẠNG THÁI", color = Green,
            fontWeight = FontWeight.Bold)

        Text(message, color = White, fontSize = 13.sp)

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("TỔNG LỆNH: $trades", color = White)
            TextButton(
    onClick = {
        showHistory = true
    }
) {
    Text(
        "LỊCH SỬ",
        color = Green
    )
            }

        HorizontalDivider(color = Color.DarkGray)

        LazyColumn {
            items(history.reversed()) { item ->
                Text(
                    item,
                    color = White,
                    modifier = Modifier.padding(vertical = 9.dp)
                )
                HorizontalDivider(color = Color.DarkGray)
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            "SIMULATION ONLY • NO REAL ORDERS",
            color = Green,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
if (showHistory) {
    AlertDialog(
        onDismissRequest = {
            showHistory = false
        },
        title = {
            Text("LỊCH SỬ GIAO DỊCH")
        },
        text = {
            Text(
                text = if (tradeHistory.isEmpty()) {
                    "Chưa có giao dịch nào."
                } else {
                    tradeHistory.reversed().joinToString("\n\n")
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showHistory = false
                }
            ) {
                Text("ĐÓNG")
            }
        }
    )
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
