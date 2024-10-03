# SMS Lock App

## Description

SMS Lock App is an Android application developed in **Kotlin** that automatically locks the device when it receives an SMS from a specific number (eSIM). The app uses Android’s device administration features to lock the screen and prevent unauthorized access.

Inspired by https://x.com/moo9000/status/1841764146716532920 


## Features

- **Automatic Phone Locking**: The app locks the phone automatically when a specific number sends an SMS.
- **Device Administration**: The app requests device administrator permissions to lock the phone.
- **SMS Reception**: Uses a `BroadcastReceiver` to intercept incoming SMS messages.

## Prerequisites

Before installing and using the application, ensure that you have:

- An Android device running Android 7.0 (API 24) or higher.
- Device administrator access to enable the app to lock the device.

## How It Works

1. Upon installation, the app asks for SMS and device administrator permissions.
2. The user must activate the device administrator feature to allow the app to lock the phone.
3. The app listens for incoming SMS messages.
4. If a message is received from the specified number, the device is "immediately" locked.

## Installation

1. **Enable Developer Options** on your Android device by going to **Settings** > **About phone**, and tap **Build number** seven times.
2. **Enable USB Debugging** in **Settings** > **Developer options**.
3. Connect your phone to your computer.
4. Open Android Studio and run the application on your device by pressing **Run** or using the APK file to install it manually.

## Permissions

The application requires the following permissions:

- **Receive SMS**: To intercept SMS messages.
- **Read SMS**: To check the message content.
- **Device Admin Permission**: To lock the device upon receiving a specific SMS.

## Usage

1. Launch the app and click the **Activate Admin** button to grant device administrator permissions.
2. Once permissions are granted, the app will listen for incoming SMS messages.
3. When an SMS is received with a specific keyword, the phone will lock "immediately".


> [!WARNING]  
> Workers are designed for deferrable, guaranteed background tasks that don't need to run immediately. Ideal for tasks that can be executed even if the app is closed or the device restarts. But execution is not guaranteed immediately.
> Hopefully if you need this app your device was stolen while unlocked. So the background call should work.


**RCS CHATS**

Apps can't intercept RCS chats (https://support.google.com/messages/answer/9487020?hl=en)
As these are neither SMS nor MMS. 
You need as the sender to disable it in the communication setting to the stolen phone running this app.
Messages App > The Contact >  Details > Use SMS & MMS.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
