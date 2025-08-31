# ProductsAdder
![WhatsApp Image 2025-09-01 at 01 52 55_42155470](https://github.com/user-attachments/assets/5f1de078-0314-43b8-914c-b3686d746709)
![WhatsApp Image 2025-09-01 at 01 52 55_9bbca18f](https://github.com/user-attachments/assets/6589284d-0965-4ae0-9bb7-999acfa9a25b)
![WhatsApp Image 2025-09-01 at 01 52 55_11560cb8](https://github.com/user-attachments/assets/6cd18654-b793-439e-a81d-91ec970b292a)

An Android application for adding and managing products with Firebase integration. This app allows users to add products with images, descriptions, prices, and other details, storing them in Firebase Firestore and Firebase Storage.

## Features

- **Product Management**: Add products with comprehensive details
- **Image Upload**: Upload multiple images for each product
- **Color Selection**: Pick colors for products using a color picker
- **Firebase Integration**: 
  - Firebase Firestore for product data storage
  - Firebase Storage for image storage
- **Modern UI**: Clean and intuitive user interface
- **Form Validation**: Ensures all required fields are filled

## Tech Stack

- **Language**: Kotlin
- **Platform**: Android
- **Architecture**: MVVM with ViewBinding
- **Backend**: Firebase
  - Firestore (NoSQL database)
  - Firebase Storage (File storage)
- **UI Components**: 
  - Material Design
  - Color Picker View
  - Image picker with multiple selection
- **Coroutines**: For asynchronous operations
- **Gradle**: Build system

## Prerequisites

- Android Studio Arctic Fox or later
- Android SDK (API level 24+)
- Google Firebase account
- Kotlin knowledge

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/LloydMatei254/ProductsAdder.git
cd ProductsAdder
```

### 2. Firebase Setup
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Place the file in the `app/` directory of your project

### 3. Firebase Configuration
1. Enable Firestore Database in your Firebase project
2. Enable Firebase Storage in your Firebase project
3. Update Firestore security rules to allow read/write access
4. Update Storage security rules to allow read/write access

### 4. Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project
4. Run on an emulator or physical device

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/productsadder/
│   │   ├── MainActivity.kt          # Main activity with product management
│   │   └── Product.kt               # Data class for product model
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # Main activity layout
│   │   ├── menu/
│   │   │   └── toolbar_menu.xml     # Toolbar menu with save button
│   │   └── values/
│   │       ├── colors.xml
│   │       ├── strings.xml
│   │       └── themes.xml
│   └── AndroidManifest.xml
├── build.gradle                     # App-level build configuration
└── google-services.json             # Firebase configuration
```

## Usage

1. **Adding a Product**:
   - Fill in the product name, category, and price (required fields)
   - Optionally add description and offer percentage
   - Add sizes (comma-separated)
   - Select colors using the color picker
   - Upload images using the image picker
   - Click the save button in the toolbar

2. **Validation**:
   - All required fields must be filled
   - At least one image must be selected
   - Price must be a valid number

## Firebase Security Rules

### Firestore Rules
```javascript
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 7, 25);
    }
  }
}
```

### Storage Rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;
    }
  }
}
```

**Note**: These are development rules. For production, implement proper authentication and authorization.

## Dependencies

- `androidx.core:core-ktx:1.16.0`
- `androidx.appcompat:appcompat:1.7.1`
- `com.google.android.material:material:1.12.0`
- `androidx.constraintlayout:constraintlayout:2.2.1`
- `com.google.firebase:firebase-firestore:26.0.0`
- `com.google.firebase:firebase-storage:22.0.0`
- `com.github.skydoves:colorpickerview:2.3.0`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.9.2`
- `org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Lloyd Matei**
- GitHub: [@LloydMatei254](https://github.com/LloydMatei254)

## Acknowledgments

- Firebase team for the excellent backend services
- Android community for the open-source libraries
- Material Design team for the design guidelines
