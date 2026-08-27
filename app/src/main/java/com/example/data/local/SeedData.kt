package com.example.data.local

import com.example.data.model.*

object SeedData {
    val HUBS = listOf(
        HubLocation("hub_1", "IIT Bombay Campus", totalNearbyItems = 124, totalUsers = 480, type = "Campus"),
        HubLocation("hub_2", "Greenwood Residency Society", totalNearbyItems = 86, totalUsers = 210, type = "Society"),
        HubLocation("hub_3", "Indiranagar Sector 4", totalNearbyItems = 152, totalUsers = 650, type = "Sector")
    )

    val CURRENT_USER = UserEntity(
        id = "user_me",
        name = "Saksham Sharma",
        avatarUrl = "",
        rating = 4.9f,
        borrowScore = 91,
        successfulReturns = 31,
        damagedItemsCount = 0,
        lateReturnsCount = 2,
        verificationStatus = "Verified Student & Aadhaar ID",
        memberSinceYear = 2026,
        hubId = "hub_1"
    )

    val SEED_USERS = listOf(
        CURRENT_USER,
        UserEntity("user_2", "Aarav Patel", "", 4.9f, 95, 42, 0, 0, "Verified Resident", 2025, "hub_1"),
        UserEntity("user_3", "Priya Verma", "", 4.8f, 88, 19, 0, 1, "Verified Student", 2026, "hub_1"),
        UserEntity("user_4", "Rohan Mehta", "", 4.7f, 85, 14, 0, 2, "Verified Neighbor", 2025, "hub_1"),
        UserEntity("user_5", "Sneha Rao", "", 5.0f, 99, 58, 0, 0, "Verified Host", 2024, "hub_1")
    )

    val SEED_ITEMS = listOf(
        ItemEntity(
            id = "item_1",
            ownerId = "user_2",
            ownerName = "Aarav Patel",
            ownerScore = 95,
            name = "Epson Full HD Projector",
            category = "Electronics",
            description = "1080p high brightness projector. Perfect for movie nights, matches & party presentations. Comes with HDMI cable and tripod stand.",
            pricePerDay = 250,
            depositAmount = 1000,
            distanceKm = 1.2f,
            rating = 4.9f,
            rentalsCount = 23,
            availabilityStatus = "AVAILABLE",
            imageUrl = "projector",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_2",
            ownerId = "user_3",
            ownerName = "Priya Verma",
            ownerScore = 88,
            name = "Casio FX-991EX Scientific Calculator",
            category = "Study",
            description = "Approved for engineering and competitive exams. 552 functions, solar powered + battery. Clean condition.",
            pricePerDay = 30,
            depositAmount = 200,
            distanceKm = 0.6f,
            rating = 4.7f,
            rentalsCount = 18,
            availabilityStatus = "AVAILABLE",
            imageUrl = "calculator",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_3",
            ownerId = "user_4",
            ownerName = "Rohan Mehta",
            ownerScore = 85,
            name = "Bosch Cordless Power Drill Set",
            category = "Tools",
            description = "18V Lithium-Ion cordless drill with 2 batteries, charger, and 30-piece drill & screwdriver bit kit. Ideal for DIY home fixes.",
            pricePerDay = 100,
            depositAmount = 500,
            distanceKm = 0.8f,
            rating = 4.8f,
            rentalsCount = 12,
            availabilityStatus = "AVAILABLE",
            imageUrl = "drill",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_4",
            ownerId = "user_5",
            ownerName = "Sneha Rao",
            ownerScore = 99,
            name = "Samsonite Hard-Shell Cabin Suitcase (55cm)",
            category = "Travel",
            description = "Lightweight, TSA lock enabled 360-degree spinner wheels. Clean, disinfected interior. Perfect for weekend getaways.",
            pricePerDay = 120,
            depositAmount = 600,
            distanceKm = 1.5f,
            rating = 4.9f,
            rentalsCount = 31,
            availabilityStatus = "AVAILABLE",
            imageUrl = "suitcase",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_5",
            ownerId = "user_2",
            ownerName = "Aarav Patel",
            ownerScore = 95,
            name = "Professional Aluminum Camera Tripod",
            category = "Electronics",
            description = "65-inch height adjustable tripod with 3-way pan-tilt head & phone mount adapter. Sturdy and easy to carry.",
            pricePerDay = 80,
            depositAmount = 300,
            distanceKm = 0.5f,
            rating = 4.8f,
            rentalsCount = 15,
            availabilityStatus = "AVAILABLE",
            imageUrl = "tripod",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_6",
            ownerId = "user_4",
            ownerName = "Rohan Mehta",
            ownerScore = 85,
            name = "Quechua 4-Person Waterproof Camping Tent",
            category = "Travel",
            description = "Fresh & Black technology keeps interior dark & cool. Includes stakes, carrying pouch, and 2 camping sleeping mats.",
            pricePerDay = 350,
            depositAmount = 1200,
            distanceKm = 2.1f,
            rating = 4.9f,
            rentalsCount = 9,
            availabilityStatus = "AVAILABLE",
            imageUrl = "tent",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_7",
            ownerId = "user_3",
            ownerName = "Priya Verma",
            ownerScore = 88,
            name = "Karakal Professional Badminton Racket Pair",
            category = "Sports",
            description = "Set of 2 high-tension carbon fiber badminton rackets with 3 Mavis 350 Yonex shuttlecocks and carrying case.",
            pricePerDay = 60,
            depositAmount = 250,
            distanceKm = 0.4f,
            rating = 4.8f,
            rentalsCount = 27,
            availabilityStatus = "AVAILABLE",
            imageUrl = "racket",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_8",
            ownerId = "user_5",
            ownerName = "Sneha Rao",
            ownerScore = 99,
            name = "Karcher High Pressure Washer 120 Bar",
            category = "Tools",
            description = "Powerful pressure cleaner for balcony tiles, cars, and patio furniture. Includes foam jet attachment and 5m hose.",
            pricePerDay = 200,
            depositAmount = 800,
            distanceKm = 1.8f,
            rating = 4.9f,
            rentalsCount = 11,
            availabilityStatus = "AVAILABLE",
            imageUrl = "washer",
            hubId = "hub_1"
        ),
        ItemEntity(
            id = "item_9",
            ownerId = "user_2",
            ownerName = "Aarav Patel",
            ownerScore = 95,
            name = "JBL Charge 5 Portable Waterproof Speaker",
            category = "Events",
            description = "Massive 40W pro sound, built-in power bank, 20-hour battery life. Perfect for small outdoor gatherings.",
            pricePerDay = 150,
            depositAmount = 700,
            distanceKm = 0.7f,
            rating = 5.0f,
            rentalsCount = 35,
            availabilityStatus = "AVAILABLE",
            imageUrl = "speaker",
            hubId = "hub_1"
        )
    )

    val SEED_REQUESTS = listOf(
        ItemRequestEntity(
            id = "req_1",
            requesterId = "user_3",
            requesterName = "Priya Verma",
            requesterScore = 88,
            title = "🏏 Need a English Willow Cricket Bat",
            timeframe = "Tomorrow, 4 PM – 8 PM",
            maxPrice = 150,
            distanceKm = 1.4f,
            category = "Sports",
            status = "OPEN",
            hubId = "hub_1",
            createdAt = System.currentTimeMillis() - 3600000
        ),
        ItemRequestEntity(
            id = "req_2",
            requesterId = "user_4",
            requesterName = "Rohan Mehta",
            requesterScore = 85,
            title = "📹 Need a Foldable Projector Screen (100 inch)",
            timeframe = "This Friday Night (6 PM - 11 PM)",
            maxPrice = 200,
            distanceKm = 0.9f,
            category = "Events",
            status = "OPEN",
            hubId = "hub_1",
            createdAt = System.currentTimeMillis() - 7200000
        ),
        ItemRequestEntity(
            id = "req_3",
            requesterId = "user_5",
            requesterName = "Sneha Rao",
            requesterScore = 99,
            title = "🪜 Need an Extension Ladder (12ft)",
            timeframe = "Saturday Morning (2 Hours)",
            maxPrice = 100,
            distanceKm = 0.5f,
            category = "Tools",
            status = "OPEN",
            hubId = "hub_1",
            createdAt = System.currentTimeMillis() - 14400000
        )
    )

    val INITIAL_RENTAL = RentalEntity(
        id = "rent_sample_1",
        itemId = "item_1",
        itemTitle = "Epson Full HD Projector",
        itemPricePerDay = 250,
        itemDeposit = 1000,
        ownerId = "user_2",
        ownerName = "Aarav Patel",
        borrowerId = "user_me",
        borrowerName = "Saksham Sharma",
        startDate = "Today, 5:00 PM",
        endDate = "Tomorrow, 5:00 PM",
        totalDays = 1,
        rentalFee = 250,
        serviceFee = 25,
        totalPrice = 275,
        status = "INSPECTION_PENDING",
        createdAt = System.currentTimeMillis() - 1800000
    )

    val INITIAL_MESSAGES = listOf(
        ChatMessageEntity("m1", "rent_sample_1", "user_2", "Aarav Patel", "Hey Saksham! Your request for the Epson Projector is accepted. Let's do the 4-photo condition check before pickup.", System.currentTimeMillis() - 1500000),
        ChatMessageEntity("m2", "rent_sample_1", "user_me", "Saksham Sharma", "Awesome Aarav! I'm coming to Hostel Block B in 15 mins. Will snap the front, back, left & right condition photos.", System.currentTimeMillis() - 1200000),
        ChatMessageEntity("m3", "rent_sample_1", "system", "BorrowHub Safety", "🔐 Security Deposit of ₹1,000 is safely held in Escrow. Complete the 4-Photo Handover Inspection below.", System.currentTimeMillis() - 900000, isSystem = true)
    )
}
