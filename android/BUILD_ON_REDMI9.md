# PROTOCOL ZERO — Build APK Directly on Your Redmi 9 (No PC Needed)

This guide is specifically designed for building an installable APK on your **Redmi 9** (MediaTek Helio G80 / 3GB/4GB RAM / Android 10/11/12 MIUI).

You have **two 100% phone-friendly methods**:

---

## Method 1: Instant Cloud Build via GitHub Actions (Recommended for Redmi 9)
Because the Redmi 9 has 3GB or 4GB of RAM, running a heavy Android Gradle build locally inside Termux can occasionally hit memory limits (OOM). GitHub Actions builds your APK on a 16GB RAM cloud server in under 2 minutes and hands you the download link directly on your phone:

1. **Upload / Push this project to a GitHub repository** (can be private or public).
2. Go to your repository on your phone's browser (Chrome/Brave).
3. Tap **Actions** tab -> **Build PROTOCOL ZERO APK** -> **Run workflow**.
4. When it turns green (approx. 90-120 seconds), tap the completed run.
5. Under **Artifacts**, tap `PROTOCOL_ZERO_debug_apk`.
6. Download the zip, open it, tap `app-debug.apk`, and tap **Install** on your Redmi 9!

---

## Method 2: On-Device Build using Termux (Pure Phone-Only)
If you want to compile directly on your Redmi 9 terminal:

### 1. Install Termux & Dependencies
Download Termux from [F-Droid](https://f-droid.org/packages/com.termux/) (do not use Google Play version as it is deprecated).
Open Termux and run:
```bash
pkg update && pkg upgrade -y
pkg install openjdk-17 git gradle android-tools -y
```

### 2. Verify Java 17
```bash
java -version
```

### 3. Clone or Unzip the Project into Termux
If you downloaded the project zip using the PROTOCOL ZERO web app button:
```bash
termux-setup-storage
cp /sdcard/Download/protocol-zero-android.zip ~/
unzip protocol-zero-android.zip -d protocol-zero
cd protocol-zero/android
```

### 4. Build the APK
```bash
gradle assembleDebug
```
The APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 5. Install the APK
```bash
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/
```
Now open your Redmi 9 **File Manager** -> **Downloads** -> tap `app-debug.apk` to install!

---

## ESP32-CAM Local Wi-Fi Configuration
- Ensure your Redmi 9 is connected to the same Wi-Fi network as the ESP32-CAM (or connected to the ESP32's Access Point e.g., `ESP32-CAM-ACCESS-POINT`).
- In the PROTOCOL ZERO app, enter your ESP32's local IP (default is usually `192.168.4.1` in AP mode, or `192.168.1.xxx` in Station mode).
- The app uses `network_security_config.xml` to allow cleartext HTTP local communication without HTTPS certificates.
- Once you upload your Arduino firmware tomorrow, we will bind the exact endpoints and JSON schema!
