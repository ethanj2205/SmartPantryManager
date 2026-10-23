# Smart Pantry Manager

A Java Android app for reducing food waste by suggesting recipes that can be made strictly from ingredients already in the user's pantry.

## Features
- SQLite persistence for pantry ingredients.
- Create, read, update and delete pantry items, with quantity, unit and optional expiry date.
- RecyclerView and custom adapter for the pantry list.
- 20 seeded recipes.
- Strict recipe matching: all required ingredients must exist and meet required quantities.
- Recipe detail pages, input validation, Settings screen and empty-state feedback.
- Intent-based navigation between Activities.

## Database choice
SQLite is used because this assignment is a single-device pantry manager. It works offline, needs no server or account, and provides persistent relational storage through Android's SQLiteOpenHelper.

## Run
1. Install Android Studio (a recent version with Android Gradle Plugin 8.7.3 support).
2. Open this project folder in Android Studio.
3. Allow Gradle sync and install any requested SDK platform (compile SDK 35).
4. Start an Android emulator or connect an Android device with USB debugging enabled.
5. Press Run for the `app` configuration.

The first launch creates the SQLite database and seeds example pantry ingredients. You can edit or delete the sample items and add your own.

## Suggested manual test
1. Open Suggested Recipes and note the available recipes.
2. Delete an ingredient required by a recipe (for example, Egg), then return to suggestions.
3. Confirm recipes needing that ingredient disappear.
4. Add it again with a quantity below a recipe's requirement; that recipe must remain excluded.
5. Close and reopen the app and confirm pantry data persists.

## GitHub requirement
Create your own public GitHub repository and commit the project incrementally during development. Do not fabricate commit history: push genuine changes as you build, and add the repository URL to your report.
