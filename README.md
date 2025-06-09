# MovieApp Android

## Table of Contents
- [Description](#description)
- [Technologies Used](#technologies-used)
- [Key Features](#key-features)
- [Installation Guide](#installation-guide)
- [Demo Images](#demo-images)
- [Download Link](#download-link)
- [Support](#support)

## Description
MovieApp is an Android application that allows users to search for, view information about, and watch movies. The app uses the kkPhim API to fetch movie data, including information and images.

## Technologies Used
Programming language: Java  
- **Android SDK:** Minimum 21 (Android 5.0 Lollipop)  
Main libraries:
- **Firebase:** Data storage, user authentication, and analytics.
- **Facebook SDK:** Facebook login integration and features.
- **Google Services:** User authentication via Google.
- **ExoPlayer:** Video playback.
- **Retrofit:** HTTP requests and JSON handling.
- **Glide:** Image loading and display.
- **Material Design:** Modern user interface design.

## Key Features
- **Movie Search:** Users can search movies by name.
- **View Detailed Information:** Display detailed information about movies, including descriptions and images.
- **Watch Movies:** Users can play and watch movies directly within the app.
- **Add to Favorites:** Allows users to add movies to their favorites list from the detail page.
- **Add to Watch Later:** Allows users to add movies to their watch later list from the detail page.
- **User-Friendly Interface:** Interface designed following Material Design standards.
- **Search History:** Stores user's search history for easy access later.
- **Login with Google:** Users can log in using their Google account.
- **Login with Facebook:** Users can log in using their Facebook account.

## Installation Guide
Step 1: Clone the repository
```bash
git clone https://github.com/DucAnh4r/MovieApp_Android.git
```
Step 2: Open the project in Android Studio
- Open Android Studio and select "Open an existing Android Studio project".
- Choose the folder containing the cloned project.
Step 3: Configure API Keys
- Register an account at Firebase to create a project and get configuration information.
- Download the google-services.json file from Firebase Console and add it to the app/ folder in the project.
- Register an account at Facebook for Developers to create an app and get Facebook App ID and App Secret.
- Add the Facebook App ID to the strings.xml file in the res/values/ folder as follows:
```bash
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token">YOUR_FACEBOOK_CLIENT_TOKEN</string>
```
Step 4: Run the application
- Connect an Android device or use an emulator.
- Click the "Run" button in Android Studio to build and run the app.

## Demo Images
- **Home Screen**  
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218487/3bf61041-de21-459b-82f5-7b5cb727b0f5_jblkdb.jpg)

- **Profile Screen**  
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218485/219345aa-25ed-464c-900e-c3bc0a2772f5_hvdzud.jpg)

- **Detail Screen**  
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218487/dcbfe27a-3cfb-46c3-a342-788ef323b156_xctqrl.jpg)

- **Watch Movie Screen**  
![App Screenshot](https://res.cloudinary.com/dkjwrhxm6/image/upload/v1749218485/a68fb034-e2f1-4e64-9c75-d4a44710556f_i2fvog.jpg)

## Download Link
https://drive.google.com/drive/folders/1KGqk0wwrvtfWGBAPDBcADd0brchOPztt

## Support
For support, please send an email to manhduc889@gmail.com.
