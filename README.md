# smart-pantry-manager

# Smart Pantry Manager

## Description
Smart Pantry Manager is an Android application written in Java that helps users reduce food waste. The user records the ingredients they currently have at home (their pantry), and the app suggests only the recipes they can cook right now using those ingredients, with no shopping trip required.

A recipe is only suggested if every ingredient it needs is in the pantry in at least the required quantity.

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen
- Pre-loaded collection of recipes
- Suggested Recipes screen using strict ingredient matching
- Recipe detail screen with ingredients and method
- Settings screen

## Database Choice
This app uses **SQLite** (via SQLiteOpenHelper), stored locally on the device.

**Why SQLite:** it works offline, needs no accounts or backend server, and keeps the user's pantry data private on their own phone. It also fits the full CRUD requirements of this app, and the data persists after the app is closed and reopened.

## Setup and Run Instructions
1. Install Android Studio.
2. Clone this repository: `git clone https://github.com/vikshay961203/smart-pantry-manager.git`
3. In Android Studio, choose **File > Open** and select the cloned folder.
4. Wait for the Gradle sync to finish.
5. Create an emulator in **Device Manager**, or connect an Android phone with USB debugging enabled.
6. Click the green **Run** button.

## Technologies
- Java
- Android Studio
- SQLite

## Author
Vikshay Jagesur