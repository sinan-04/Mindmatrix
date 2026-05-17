# Namma-Santhe Ledger

A digital ledger app for managing credit and payments at local markets (Santhe). Track customer dues, record transactions, and send payment reminders — all from your phone.

## Features

- **Customer Management** — Add and manage customers with name and phone number
- **Transaction Tracking** — Record credit and payment transactions per customer
- **Outstanding Balance** — View total outstanding dues and today's sales at a glance
- **Transaction History** — See per-customer balance and full transaction history
- **Payment Reminders** — Send due reminders directly via WhatsApp or SMS
- **Offline-first** — All data stored locally on device using Room database

## Tech Stack

- **Jetpack Compose** — UI
- **Room** — Local database
- **ViewModel + StateFlow** — State management
- **Navigation Compose** — In-app navigation
- **Kotlin + Gradle KTS**

## Prerequisites

- Android Studio Koala or newer
- JDK 11 or newer
- Min SDK 24 / Target SDK 35

## Building

```bash
./gradlew assembleDebug
```
