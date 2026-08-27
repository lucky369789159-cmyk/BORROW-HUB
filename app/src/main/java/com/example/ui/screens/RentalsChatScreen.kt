package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.RentalEntity
import com.example.ui.BorrowViewModel
import com.example.ui.components.InspectionPhotoSelector
import com.example.ui.theme.EmeraldSuccess

@Composable
fun RentalsChatScreen(
    viewModel: BorrowViewModel,
    modifier: Modifier = Modifier
) {
    val rentals by viewModel.rentals.collectAsState()
    val activeRentalId by viewModel.activeRentalId.collectAsState()
    val messages by viewModel.activeRentalMessages.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var chatInput by remember { mutableStateOf("") }

    // Photos state for pickup/handover
    var fPhoto by remember { mutableStateOf("front_ok.jpg") }
    var bPhoto by remember { mutableStateOf("back_ok.jpg") }
    var lPhoto by remember { mutableStateOf("left_ok.jpg") }
    var rPhoto by remember { mutableStateOf("right_ok.jpg") }

    // Photos state for return
    var rfPhoto by remember { mutableStateOf("r_front_ok.jpg") }
    var rbPhoto by remember { mutableStateOf("r_back_ok.jpg") }
    var rlPhoto by remember { mutableStateOf("r_left_ok.jpg") }
    var rrPhoto by remember { mutableStateOf("r_right_ok.jpg") }

    val activeRental = rentals.find { it.id == activeRentalId } ?: rentals.firstOrNull()

    Column(modifier = modifier.fillMaxSize()) {
        // Active Rental Selector Tabs
        if (rentals.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = rentals.indexOf(activeRental).coerceAtLeast(0),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                rentals.forEach { rental ->
                    Tab(
                        selected = rental.id == activeRental?.id,
                        onClick = { viewModel.setActiveRentalId(rental.id) },
                        text = {
                            Text(
                                text = rental.itemTitle,
                                fontWeight = if (rental.id == activeRental?.id) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (activeRental == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active rentals yet. Request an item from the Explore screen!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            val listState = rememberLazyListState()

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rental Header Info Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeRental.itemTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = activeRental.status,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Fee Breakdown
                            val ownerEarns = activeRental.rentalFee
                            val serviceFee = activeRental.serviceFee
                            val totalPay = activeRental.totalPrice

                            Text(
                                text = "💰 Earnings & Fees: Borrower pays ₹$totalPay (Rental ₹$ownerEarns + Service Fee ₹$serviceFee). Owner receives ₹$ownerEarns.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "🔐 Escrow Security Deposit: ₹${activeRental.itemDeposit} (Refundable upon inspection)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Pickup Handover 4-Photo Inspection
                if (activeRental.status == "INSPECTION_PENDING") {
                    item {
                        InspectionPhotoSelector(
                            title = "📸 Step 1: Pickup 4-Photo Handover Inspection",
                            frontPhoto = fPhoto,
                            backPhoto = bPhoto,
                            leftPhoto = lPhoto,
                            rightPhoto = rPhoto,
                            onPhotoSelected = { angle, tag ->
                                when (angle) {
                                    "front" -> fPhoto = tag
                                    "back" -> bPhoto = tag
                                    "left" -> lPhoto = tag
                                    "right" -> rPhoto = tag
                                }
                            },
                            onConfirmAll = {
                                viewModel.submitPickupPhotos(activeRental.id, fPhoto, bPhoto, lPhoto, rPhoto)
                            }
                        )
                    }
                }

                // Return Handover 4-Photo Inspection
                if (activeRental.status == "ACTIVE") {
                    item {
                        InspectionPhotoSelector(
                            title = "📸 Step 2: Return 4-Photo Condition Verification",
                            frontPhoto = rfPhoto,
                            backPhoto = rbPhoto,
                            leftPhoto = rlPhoto,
                            rightPhoto = rrPhoto,
                            onPhotoSelected = { angle, tag ->
                                when (angle) {
                                    "front" -> rfPhoto = tag
                                    "back" -> rbPhoto = tag
                                    "left" -> rlPhoto = tag
                                    "right" -> rrPhoto = tag
                                }
                            },
                            onConfirmAll = {
                                viewModel.submitReturnPhotos(activeRental.id, rfPhoto, rbPhoto, rlPhoto, rrPhoto)
                            }
                        )
                    }
                }

                // Chat Messages
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg, isMe = msg.senderId == currentUser?.id)
                }
            }

            // Bottom Chat Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Message owner/borrower...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                viewModel.sendChatMessage(activeRental.id, chatInput)
                                chatInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("send_chat_msg_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageEntity, isMe: Boolean) {
    if (message.isSystem) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Safety Log",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Surface(
                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (!isMe) {
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
