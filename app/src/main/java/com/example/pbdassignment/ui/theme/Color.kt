package com.example.pbdassignment.ui.theme

import androidx.compose.ui.graphics.Color

// ===================================================================
// Ultra-Clean Modern Fintech Design System
// Inspired by Linear, Apple Wallet, and Revolut
// ===================================================================

// Brand Primary (Clean Deep Emerald)
val BrandPrimary = Color(0xFF0F766E)
val BrandOnPrimary = Color(0xFFFFFFFF)
val BrandPrimaryContainer = Color(0xFFE6F4EA)
val BrandOnPrimaryContainer = Color(0xFF064E3B)

val BrandSecondary = Color(0xFF0D9488)
val BrandSecondaryContainer = Color(0xFFCCFBF1)

// Backgrounds & Card Surfaces
val BackgroundLight = Color(0xFFF8FAFC)      // Crisp modern off-white background
val SurfaceLight = Color(0xFFFFFFFF)         // Pure white card surfaces
val SurfaceVariantLight = Color(0xFFF1F5F9)  // Soft subtle slate tint
val BorderLight = Color(0xFFE2E8F0)          // Subtle 1px hairline card borders

val BackgroundDark = Color(0xFF0B0F17)       // Deep matte obsidian
val SurfaceDark = Color(0xFF161E2E)          // Dark obsidian card surface
val SurfaceVariantDark = Color(0xFF1F293D)
val BorderDark = Color(0xFF26334D)

// Text Hierarchies
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val TextMuted = Color(0xFF94A3B8)

// Financial Semantic Colors (High-Contrast, Punchy & Minimalist)
val IncomeGreen = Color(0xFF10B981)          // Mint emerald for inflows (+LKR)
val IncomeGreenLight = Color(0xFFECFDF5)     // Soft pill background
val ExpenseRed = Color(0xFFEF4444)           // Clean coral red for outflows (-LKR)
val ExpenseRedLight = Color(0xFFFEF2F2)      // Soft pill background

val CommittedBlue = Color(0xFF3B82F6)        // Clean royal blue for fixed obligations
val DiscretionaryOrange = Color(0xFFF59E0B)  // Vibrant amber for lifestyle spend
val GoalGold = Color(0xFFF59E0B)             // Milestone amber-gold
val OverdueRed = Color(0xFFDC2626)           // Warning red
val CryptoPurple = Color(0xFF8B5CF6)         // Crypto purple

// Backwards compatibility aliases
val EmeraldPrimary = BrandPrimary
val EmeraldOnPrimary = BrandOnPrimary
val EmeraldPrimaryContainer = BrandPrimaryContainer
val EmeraldOnPrimaryContainer = BrandOnPrimaryContainer
val SecondaryTeal = BrandSecondary
val SecondaryContainer = BrandSecondaryContainer
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = BrandPrimary
val PurpleGrey40 = BrandSecondary
val Pink40 = ExpenseRed
