package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ItemCategory
import com.example.ui.BorrowViewModel
import com.example.ui.components.RequestCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedRequestsScreen(
    viewModel: BorrowViewModel,
    onNavigateToRentals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.requests.collectAsState()
    val currentHub by viewModel.selectedHub.collectAsState()

    var showPostDialog by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newTimeframe by remember { mutableStateOf("") }
    var newMaxPrice by remember { mutableStateOf("150") }
    var newCategory by remember { mutableStateOf(ItemCategory.SPORTS) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPostDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Post Request") },
                text = { Text("Post a Need", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("post_need_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Explanation Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "I Need This",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "🗣️ 'I Need This' Uber-Style Network",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Don't browse 100 listings. Post what you need and nearby owners will offer theirs directly!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(requests, key = { it.id }) { req ->
                    RequestCard(
                        request = req,
                        onOfferMineClick = {
                            viewModel.offerMineToRequest(req) {
                                onNavigateToRentals()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showPostDialog) {
        AlertDialog(
            onDismissRequest = { showPostDialog = false },
            title = { Text("Post 'I Need This' Request 🏏") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Broadcast your need to neighbors within 3 km of ${currentHub.name}:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("What do you need?") },
                        placeholder = { Text("e.g., Need a cricket bat") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("need_title_input")
                    )

                    OutlinedTextField(
                        value = newTimeframe,
                        onValueChange = { newTimeframe = it },
                        label = { Text("When do you need it?") },
                        placeholder = { Text("e.g., Tomorrow, 4–8 PM") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("need_timeframe_input")
                    )

                    OutlinedTextField(
                        value = newMaxPrice,
                        onValueChange = { newMaxPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Max Budget (₹)") },
                        placeholder = { Text("150") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("need_price_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.postNeedRequest(
                                title = if (newTitle.startsWith("Need")) newTitle else "Need a $newTitle",
                                timeframe = newTimeframe.ifBlank { "Tomorrow" },
                                maxPrice = newMaxPrice.toIntOrNull() ?: 150,
                                category = newCategory.displayName
                            )
                            showPostDialog = false
                            newTitle = ""
                        }
                    },
                    modifier = Modifier.testTag("submit_need_request_btn")
                ) {
                    Text("Broadcast Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
