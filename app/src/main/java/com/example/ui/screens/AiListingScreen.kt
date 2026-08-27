package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.AiScannerState
import com.example.ui.BorrowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiListingScreen(
    viewModel: BorrowViewModel,
    onItemListed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiState.collectAsState()
    val currentHub by viewModel.selectedHub.collectAsState()

    var photoInputPrompt by remember { mutableStateOf("Bosch cordless drill") }

    // Form fields for checking before tapping "LIST"
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tools") }
    var pricePerDay by remember { mutableStateOf("100") }
    var depositAmount by remember { mutableStateOf("500") }
    var description by remember { mutableStateOf("") }

    // Synchronize form when AI succeeds
    LaunchedEffect(aiState) {
        if (aiState is AiScannerState.Success) {
            val res = (aiState as AiScannerState.Success).result
            title = res.title
            category = res.category
            pricePerDay = res.suggestedPricePerDay.toString()
            depositAmount = res.suggestedDeposit.toString()
            description = res.description
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Header
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Listing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1-Photo AI Item Listing 📸",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "No long forms! Snap a photo -> AI auto-fills title, category, daily rental rate & description in 10 seconds.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Sample Photo Selectors
        Text(
            text = "Select Sample Item Photo or Enter Keyword:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SamplePhotoButton("Drill 🛠️", "Bosch cordless drill") {
                photoInputPrompt = it
                viewModel.analyzePhotoForItemListing(it)
            }
            SamplePhotoButton("Calculator 🧮", "Casio scientific calculator") {
                photoInputPrompt = it
                viewModel.analyzePhotoForItemListing(it)
            }
            SamplePhotoButton("Projector 📽️", "Epson HD projector") {
                photoInputPrompt = it
                viewModel.analyzePhotoForItemListing(it)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SamplePhotoButton("Suitcase 🧳", "Samsonite cabin suitcase") {
                photoInputPrompt = it
                viewModel.analyzePhotoForItemListing(it)
            }
            SamplePhotoButton("Tripod 📷", "Camera tripod stand") {
                photoInputPrompt = it
                viewModel.analyzePhotoForItemListing(it)
            }
            SamplePhotoButton("Tent 🎪", "Camping tent 4 person") {
                photoInputPrompt = it
                viewModel.analyzePhotoForItemListing(it)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Snap/Scan Input
        OutlinedTextField(
            value = photoInputPrompt,
            onValueChange = { photoInputPrompt = it },
            label = { Text("Photo Tag / Item Description") },
            placeholder = { Text("e.g. Pressure washer 120 bar") },
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.analyzePhotoForItemListing(photoInputPrompt) },
                    modifier = Modifier.testTag("ai_scan_photo_btn")
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Scan Photo", tint = MaterialTheme.colorScheme.primary)
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("photo_prompt_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.analyzePhotoForItemListing(photoInputPrompt) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analyze_photo_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Analyze")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Run AI Item Recognition", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Scanner Status / Results
        when (aiState) {
            is AiScannerState.Analyzing -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Gemini AI is analyzing item features & pricing...", style = MaterialTheme.typography.bodyMedium)
            }
            is AiScannerState.Error -> {
                Text(
                    text = (aiState as AiScannerState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is AiScannerState.Success -> {
                val result = (aiState as AiScannerState.Success).result
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
                                text = "AI Generated Listing Details",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = result.confidence,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth().testTag("ai_title_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Category") },
                                modifier = Modifier.weight(1f).testTag("ai_category_field")
                            )

                            OutlinedTextField(
                                value = pricePerDay,
                                onValueChange = { pricePerDay = it },
                                label = { Text("Rental (₹/day)") },
                                modifier = Modifier.weight(1f).testTag("ai_price_field")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = depositAmount,
                            onValueChange = { depositAmount = it },
                            label = { Text("Suggested Deposit (₹)") },
                            modifier = Modifier.fillMaxWidth().testTag("ai_deposit_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth().testTag("ai_description_field")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.confirmCreateItemFromAi(
                                    title = title,
                                    category = category,
                                    description = description,
                                    pricePerDay = pricePerDay.toIntOrNull() ?: 100,
                                    deposit = depositAmount.toIntOrNull() ?: 500
                                )
                                onItemListed()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_list_item_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("LIST ITEM (10 SECONDS)", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
            else -> {
                Text(
                    text = "Tap any sample photo or scan your own item to trigger AI Auto-Fill!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RowScope.SamplePhotoButton(
    label: String,
    prompt: String,
    onClick: (String) -> Unit
) {
    OutlinedButton(
        onClick = { onClick(prompt) },
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = Modifier.weight(1f)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
