package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraUI()
        }
    }
}

@Composable
fun CalculadoraUI() {
    var input by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var operador by remember { mutableStateOf("") }
    var numeroAnterior by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F1F1F))
            .padding(16.dp)
    ) {
        // Pantalla
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = if (resultado.isNotEmpty()) resultado else input,
                style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val botones = listOf(
            listOf("C", "←", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("+/-", "0", ".", "=",)
        )

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for ((index, fila) in botones.withIndex()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for ((i, texto) in fila.withIndex()) {
                        val span = when {
                            index == 0 && texto == "C" -> 2f
                            else -> 1f
                        }

                        BotonCalculadora(
                            texto = texto,
                            modifier = Modifier
                                .weight(span)
                                .fillMaxHeight()
                        ) {
                            when (texto) {
                                "C" -> {
                                    input = ""
                                    resultado = ""
                                    numeroAnterior = ""
                                    operador = ""
                                }
                                "←" -> {
                                    if (input.isNotEmpty()) {
                                        input = input.dropLast(1)
                                    }
                                }
                                "+/-" -> {
                                    if (input.isNotEmpty() && input.toDoubleOrNull() != null) {
                                        input = (-input.toDouble()).toString().removeSuffix(".0")
                                    }
                                }
                                "+", "−", "×", "÷" -> {
                                    if (input.isNotEmpty()) {
                                        numeroAnterior = input
                                        operador = texto
                                        input = ""
                                    }
                                }
                                "=" -> {
                                    val num1 = numeroAnterior.toDoubleOrNull()
                                    val num2 = input.toDoubleOrNull()
                                    if (num1 != null && num2 != null) {
                                        resultado = when (operador) {
                                            "+" -> (num1 + num2).toString()
                                            "−" -> (num1 - num2).toString()
                                            "×" -> (num1 * num2).toString()
                                            "÷" -> if (num2 != 0.0) (num1 / num2).toString() else "Error"
                                            else -> "Error"
                                        }.removeSuffix(".0")
                                        input = resultado
                                        numeroAnterior = ""
                                        operador = ""
                                    }
                                }
                                else -> {
                                    input += texto
                                    resultado = ""
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BotonCalculadora(texto: String, modifier: Modifier, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isPressed = event.changes.any { it.pressed }
                    }
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF393E46))
    ) {
        Text(text = texto, fontSize = 24.sp, color = Color.White)
    }
}
