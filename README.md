# ToDoApp — Cloud APK Build

This project is preconfigured with a GitHub Actions workflow
(`.github/workflows/build.yml`) that compiles the APK on GitHub's
servers — you don't need Android Studio or the Android SDK installed
locally.

## One-time setup (about 5 minutes)

1. Go to https://github.com/new and create a new **public or private**
   repository (any name, e.g. `todo-app`). Don't initialize it with a
   README — you already have one here.

2. On your Linux machine, open a terminal in this folder and run:

   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
   git push -u origin main
   ```

   Replace `YOUR_USERNAME/YOUR_REPO` with your actual GitHub repo URL
   (shown on the page after you create it).

## Getting the APK (every time after that)

1. Go to your repo on GitHub → the **Actions** tab.
2. You'll see a "Build APK" run in progress (it starts automatically
   on every push). Wait ~3-5 minutes for it to finish.
3. Click the completed run → scroll to **Artifacts** → download
   `app-debug-apk`. Unzip it — that's your `app-debug.apk`.
4. Transfer it to your Android phone (email it to yourself, use a USB
   cable, Google Drive, etc.) and tap it to install. You'll need to
   allow "install from unknown sources" the first time.

## Making changes later

Edit any `.kt` file, then:

```bash
git add .
git commit -m "describe your change"
git push
```

A new APK will build automatically — no local build tools needed.
