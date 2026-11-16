# إنشاء ملف APK عبر GitHub Actions

هذه الخطوات تسمح ببناء APK على خادم GitHub دون تثبيت Android Studio على هاتفك.

1. فك ضغط `SmartTVTutor_Android_Project.zip` على الكمبيوتر.
2. أنشئ مستودعاً جديداً على GitHub.
3. ارفع **محتويات** المجلد إلى المستودع (بما فيها مجلد `.github`).
4. افتح تبويب **Actions** في المستودع.
5. اختر **Build Smart TV Tutor APK** ثم **Run workflow**.
6. بعد انتهاء البناء بنجاح، افتح نتيجة التشغيل وابحث عن قسم **Artifacts**.
7. نزّل `SmartTVTutor-debug-apk`، ثم فك ضغطه لتحصل على `app-debug.apk`.

ملاحظة: هذا APK تجريبي غير موقّع للنشر على Google Play. يمكن استخدامه للاختبار والتثبيت اليدوي على جهاز Android/Google TV مع السماح بالتثبيت من المصدر المناسب.
