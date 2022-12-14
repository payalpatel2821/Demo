package com.unoapp.demo.utils


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.content.*
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.*
import android.text.*
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.unoapp.demo.App
import com.unoapp.demo.BuildConfig
import com.unoapp.demo.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.lang.reflect.Type
import java.net.URLConnection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * check network
 */
fun Context.isNetworkConnected(): Boolean {
    var result = false
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}

/**
 * Email Validation
 */
fun isValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

/**
 * Add Intent extras
 */
inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)




/**
 * Show Log
 */
@SuppressLint("LogNotTimber")
fun showLog(name: String, value: String) {
    Log.e(name, value)
}

/**
 * show keyboard
 *
 * @param mContext
 * @param view
 */
fun showSoftKeyboard(mContext: Context, view: View?) {
    try {
        val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * hide keyboard if visible
 *
 * @param mActivity
 * @param view
 */
fun hideSoftKeyboard(mActivity: Activity, view: View) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { v, event ->
            hideSoftKeyboard(mActivity)
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            hideSoftKeyboard(mActivity, innerView)
        }
    }
}

fun Context.getDisplayMatrix(): DisplayMetrics {
    return resources.displayMetrics
}

/**
 * hide keyboard if visible
 *
 * @param mActivity
 */
fun hideSoftKeyboard(mActivity: Activity) {
    try {
        val imm = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = mActivity.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(mActivity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * scroll view when soft keyboard is open
 *
 * @param view enter scrollview
 */
fun setupKeyboardListener(view: ScrollView) {
    view.viewTreeObserver.addOnGlobalLayoutListener {
        val r = Rect()
        view.getWindowVisibleDisplayFrame(r)
        if (Math.abs(view.rootView.height - (r.bottom - r.top)) > 100) { // if more than 100 pixels, its probably a keyboard...
            onKeyboardShow(view)
        }
    }
}

fun onKeyboardShow(view: ScrollView) {
    view.scrollToBottomWithoutFocusChange()
}

fun ScrollView.scrollToBottomWithoutFocusChange() { // Kotlin extension to scrollView
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}
//    /**
//    open dialog
//     */
//    var dialog: Dialog? = null
//    fun openProgressDialog(activity: Activity?) {
////    hideProgressDialog()
//
//        try {
//            if (dialog != null) {
//                dialog!!.dismiss()
//                dialog = null
//            }
//
//            dialog = Dialog(activity!!)
//            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog!!.setContentView(R.layout.progress_layout)
//            dialog!!.setCanceledOnTouchOutside(true)
//            Glide.with(activity!!).load(R.drawable.loader).into(dialog!!.progress)
//            if (dialog != null && !dialog!!.isShowing) {
//                dialog!!.show()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Dismiss Dialog
//     */
//    fun hideProgressDialog() {
//        try {
//            if (dialog != null) {
//                dialog!!.hide()
//                dialog!!.dismiss()
//                dialog = null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
/**
open dialog
 */
var dialog: Dialog? = null
fun openProgressDialog(activity: Activity?) {
//    hideProgressDialog()

    try {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }

        dialog = Dialog(activity!!)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog!!.setContentView(R.layout.progress_layout)
        dialog!!.setCanceledOnTouchOutside(true)
        //Glide.with(activity!!).load(R.drawable.loader).into(dialog!!.progress)
        if (dialog != null && !dialog!!.isShowing) {
            dialog!!.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Dismiss Dialog
 */
fun hideProgressDialog() {
    try {
        if (dialog != null) {
            dialog!!.hide()
            dialog!!.dismiss()
            dialog = null
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun requestFocus(context: Context, view: View) {
    if (view.requestFocus()) {
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

/**
 * Check mobile number valid or not
 *
 * @param phone enter mobile number
 * @return
 */
fun isValidPhoneNumber(target: CharSequence): Boolean {
    return if (target.length != 10) {
        false
    } else {
        Patterns.PHONE.matcher(target).matches()
    }
}


fun getFileNameFromURLString(URLString: String): String {
    return URLString.substring(URLString.lastIndexOf('/') + 1);

}

fun convertDurationStringToSeconds(duration: String): String {
    val arrDuration = duration.split(":")
    var totalSec = 0
    return if (arrDuration.isNotEmpty()) {
        val min = arrDuration[0].toInt()
        val sec = arrDuration[1].toInt()
        totalSec = (min * 60) + sec
        totalSec.toString()
    } else {
        duration
    }
}

/**
 * Loads image with Glide into the [ImageView].
 *
 * @param url url to load
 * @param previousUrl url that already loaded in this target. Needed to prevent white flickering.
 * @param round if set, the image will be round.
 * @param cornersRadius the corner radius to set. Only used if [round] is `false`(by default).
 * @param crop if set to `true` then [CenterCrop] will be used. Default is `false` so [FitCenter] is used.
 */
@SuppressLint("CheckResult")
fun ImageView.load(
    url: String?,
    isProfile: Boolean? = false,
    name: String? = "",
    color: String? = "",
    previousUrl: String? = "",
    round: Boolean = false,
    cornersRadius: Int = 0,
    crop: Boolean = false
) {

    val requestOptions = when {
        round -> RequestOptions.circleCropTransform()

        cornersRadius > 0 -> {
            RequestOptions().transforms(
                if (crop) CenterCrop() else FitCenter(),
                RoundedCorners(cornersRadius)
            )
        }
        else -> null
    }
    val glide = Glide.with(context).load(url)
   // if (isProfile == true) {
   //     glide.placeholder(AvatarGenerator.avatarImage(context, 200, AvatarGenerator.RECTANGLE, name ?: "", color ?: ""))
   // } else {
        glide.placeholder(R.drawable.ic_launcher_foreground)
   // }
    //glide.placeholder(R.drawable.logo)
    glide.let {
        // Apply request options
        if (requestOptions != null) {
            it.apply(requestOptions)
//            it.placeholder(R.drawable.logo)
        } else {
            it
        }
    }.let {
        // Workaround for the white flickering.
        // See https://github.com/bumptech/glide/issues/527
        // Thumbnail changes must be the same to catch the memory cache.
        if (previousUrl != null) {
            it.thumbnail(
                Glide.with(context).load(previousUrl)
                    .let {
                        // Apply request options
                        if (requestOptions != null) {
                            it.apply(requestOptions)
                        } else {
                            it
                        }
                    }
            )
        } else {
            it
        }
    }.into(this)
}

fun parseDate(
    inputDateString: String?,
    inputDateFormat: SimpleDateFormat,
    outputDateFormat: SimpleDateFormat
): String? {
    var date: Date? = null
    var outputDateString: String? = null
    try {
        date = inputDateFormat.parse(inputDateString)
        outputDateString = outputDateFormat.format(date)
    } catch (e: ParseException) {
//        FirebaseCrashlytics.getInstance().setCustomKey("ParseDate", e.message)
//        FirebaseCrashlytics.getInstance().recordException(e)
        e.printStackTrace()
    }
    return outputDateString
}

fun AppCompatEditText.setUnderlineColor(color: Int) {
    background.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

}

/**
 * get first character from string
 *
 * @param name
 * @return
 */
fun firstCharacter(name: String): String? {
    val words = name.split(" ").toTypedArray()
    return if (words.size >= 2) {
        var first = if (words[0].toString().isNotEmpty()) {
            words[0].first().toString().toUpperCase(Locale.ROOT)
        } else {
            ""
        }
        var last = if (words[1].toString().isNotEmpty()) {
            words[1].first().toString().toUpperCase(Locale.ROOT)
        } else {
            ""
        }
        first + last
    } else name.first().toString().toUpperCase(Locale.ROOT)
}

/**
 * Username Validation
 */
var blockCharacterSet: String? = "%&\"<>\\'???.\$*()-+=!:;?,{}[]|"

var filter: InputFilter? = InputFilter { source, start, end, dest, dstart, dend ->
    if (source != null && blockCharacterSet!!.contains("" + source)) {
        ""
    } else null
}

/**
 * Helps to set clickable part in text.
 *
 * Don't forget to set android:textColorLink="@color/link" (click selector) and
 * android:textColorHighlight="@color/window_background" (background color while clicks)
 * in the TextView where you will use this.
 */
fun SpannableString.withClickableSpan(
    clickablePart: String,
    onClickListener: () -> Unit
): SpannableString {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = Color.parseColor("#6CAEC4")
            ds.typeface = Typeface.createFromAsset(App.getAppInstance().assets, "lato_regular.ttf")
            ds.isUnderlineText = false
        }
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(
        clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return this
}

fun dpToPx(context: Context, dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun findIndex(arr: List<Int>, item: Int): Int? {
    return (arr.indices)
        .firstOrNull { i: Int -> item == arr[i] };
}


fun String.firstCap() = this.replaceFirstChar { it.uppercase() }


//fun Toolbar.setNavigationIconColor(@ColorInt color: Int) = navigationIcon?.mutate()?.let {
//    it.setTint(color)
//    this.navigationIcon = it
//}

fun isImageFile(path: String?): Boolean {
    Log.e("isImageFile: ", path.toString())

    //Add New
//    val realPathFromUri = SiliCompressor.getRealPathFromUri(App.appInstance!!.applicationContext, Uri.fromFile(File(path)))
//    val realPathFromUri = getPathFromUri(App.appInstance!!.applicationContext,
//        Uri.parse("content://com.android.providers.media.documents/document/image%3A741"))
//        Uri.parse(path))
//    Log.e("isImageFile:realPath", realPathFromUri.toString())

    val mimeType: String = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("image")
}

fun isVideoFile(path: String?): Boolean {
    val mimeType: String = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("video")
}

fun prepareFilePart(partName: String, fileUri: String, type: String): MultipartBody.Part {
    Log.println(Log.ASSERT, "fileUri---", type)

    val file = File(fileUri)

    Log.println(Log.ASSERT, "filefile---", file.absolutePath)
    Log.d("filefile---", file.absolutePath)

    val requestBody: RequestBody = RequestBody.create(type.toMediaTypeOrNull(), file)

    return MultipartBody.Part.createFormData(partName, file.name, requestBody)
}

fun checkCompressfolder(): String? {
    val root = Environment.getExternalStorageDirectory()
    val file = File(root.absolutePath + "/HOW/.compressvideo")
    var isDirectoryCreated = file.exists()
    if (!isDirectoryCreated) {
        isDirectoryCreated = file.mkdir()
    }
    return if (isDirectoryCreated) {
        file.absolutePath
    } else file.absolutePath
}

fun storeImage(image: Bitmap, pictureFile: File?): Boolean {
    if (pictureFile == null) {
        Log.d(
            "storeImage",
            "Error creating media file, check storage permissions: "
        ) // e.getMessage());
        return false
    }
    try {
        val fos = FileOutputStream(pictureFile)
        image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return true
    } catch (e: FileNotFoundException) {
        Log.d("storeImage", "File not found: " + e.message)
    } catch (e: IOException) {
        Log.d("storeImage", "Error accessing file: " + e.message)
    } catch (e: NullPointerException) {
        Log.d("storeImage", "Error accessing file: " + e.message)
    }
    return false
}

fun closeQuietly(closeable: Closeable?) {
    if (closeable == null) return
    try {
        closeable.close()
    } catch (ignored: Throwable) {
    }
}

fun getScaledBitmapForHeight(bitmap: Bitmap, outHeight: Int): Bitmap? {
    val currentWidth = bitmap.width.toFloat()
    val currentHeight = bitmap.height.toFloat()
    val ratio = currentWidth / currentHeight
    val outWidth = Math.round(outHeight * ratio)
    return getScaledBitmap(bitmap, outWidth, outHeight)
}

fun getScaledBitmap(bitmap: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
    val currentWidth = bitmap.width
    val currentHeight = bitmap.height
    val scaleMatrix = Matrix()
    scaleMatrix.postScale(
        outWidth.toFloat() / currentWidth.toFloat(),
        outHeight.toFloat() / currentHeight.toFloat()
    )
    return Bitmap.createBitmap(bitmap, 0, 0, currentWidth, currentHeight, scaleMatrix, true)
}

@TargetApi(Build.VERSION_CODES.KITKAT)
fun getFileFromUri(
    context: Context?,
    uri: Uri
): File? {
    var filePath: String? = null
    val isKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    if (isKitkat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                filePath = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            // String "id" may not represent a valid Long type data, it may equals to
            // something like "raw:/storage/emulated/0/Download/some_file" instead.
            // Doing a check before passing the "id" to Long.valueOf(String) would be much safer.
            filePath = if (RawDocumentsHelper.isRawDocId(id)) {
                RawDocumentsHelper.getAbsoluteFilePath(id)
            } else {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                getDataColumn(context, contentUri, null, null)
            }
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            filePath = getDataColumn(context, contentUri, selection, selectionArgs)
        } else if (isGoogleDriveDocument(uri)) {
            return getGoogleDriveFile(context, uri)!!
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        filePath = if (isGooglePhotosUri(uri)) {
            uri.lastPathSegment
        } else {
            getDataColumn(context, uri, null, null)
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        filePath = uri.path
    }
    return if (filePath != null) {
        File(filePath)
    } else null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context       The context.
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(
    context: Context?, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val projection = arrayOf(
        MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME
    )
    try {
        cursor = context?.contentResolver?.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = if (uri.toString()
                    .startsWith("content://com.google.android.gallery3d")
            ) cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) else cursor.getColumnIndex(
                MediaStore.MediaColumns.DATA
            )
            if (columnIndex != -1) {
                return cursor.getString(columnIndex)
            }
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * @param uri The Uri to check
 * @return Whether the Uri authority is Google Drive.
 */
fun isGoogleDriveDocument(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage" == uri.authority
}

//Add New function for google drive
fun isGoogleDriveDocumentNew(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
}

// A copy of com.android.providers.downloads.RawDocumentsHelper since it is invisibility.
object RawDocumentsHelper {
    const val RAW_PREFIX = "raw:"
    fun isRawDocId(docId: String?): Boolean {
        return docId != null && docId.startsWith(RAW_PREFIX)
    }

    fun getDocIdForFile(file: File): String {
        return RAW_PREFIX + file.absolutePath
    }

    fun getAbsoluteFilePath(rawDocumentId: String): String {
        return rawDocumentId.substring(RAW_PREFIX.length)
    }
}

/**
 * Copy EXIF info to new file
 *
 *
 * =========================================
 *
 *
 * NOTE: PNG cannot not have EXIF info.
 *
 *
 * source: JPEG, save: JPEG
 * copies all EXIF data
 *
 *
 * source: JPEG, save: PNG
 * saves no EXIF data
 *
 *
 * source: PNG, save: JPEG
 * saves only width and height EXIF data
 *
 *
 * source: PNG, save: PNG
 * saves no EXIF data
 *
 *
 * =========================================
 */
fun copyExifInfo(
    context: Context?, sourceUri: Uri?, saveUri: Uri?, outputWidth: Int,
    outputHeight: Int
) {
    if (sourceUri == null || saveUri == null) return
    try {
        val sourceFile: File = getFileFromUri(context, sourceUri)!!
        val saveFile: File = getFileFromUri(context, saveUri)!!
        if (sourceFile == null || saveFile == null) {
            return
        }
        val sourcePath = sourceFile.absolutePath
        val savePath = saveFile.absolutePath
        val sourceExif = ExifInterface(sourcePath)
        val tags: MutableList<String> = ArrayList()
        tags.add(ExifInterface.TAG_DATETIME)
        tags.add(ExifInterface.TAG_FLASH)
        tags.add(ExifInterface.TAG_FOCAL_LENGTH)
        tags.add(ExifInterface.TAG_GPS_ALTITUDE)
        tags.add(ExifInterface.TAG_GPS_ALTITUDE_REF)
        tags.add(ExifInterface.TAG_GPS_DATESTAMP)
        tags.add(ExifInterface.TAG_GPS_LATITUDE)
        tags.add(ExifInterface.TAG_GPS_LATITUDE_REF)
        tags.add(ExifInterface.TAG_GPS_LONGITUDE)
        tags.add(ExifInterface.TAG_GPS_LONGITUDE_REF)
        tags.add(ExifInterface.TAG_GPS_PROCESSING_METHOD)
        tags.add(ExifInterface.TAG_GPS_TIMESTAMP)
        tags.add(ExifInterface.TAG_MAKE)
        tags.add(ExifInterface.TAG_MODEL)
        tags.add(ExifInterface.TAG_WHITE_BALANCE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tags.add(ExifInterface.TAG_EXPOSURE_TIME)
            tags.add(ExifInterface.TAG_APERTURE)
            tags.add(ExifInterface.TAG_ISO)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tags.add(ExifInterface.TAG_DATETIME_DIGITIZED)
            tags.add(ExifInterface.TAG_SUBSEC_TIME)
            tags.add(ExifInterface.TAG_SUBSEC_TIME_DIG)
            tags.add(ExifInterface.TAG_SUBSEC_TIME_ORIG)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tags.add(ExifInterface.TAG_F_NUMBER)
            tags.add(ExifInterface.TAG_ISO_SPEED_RATINGS)
            tags.add(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED)
            tags.add(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL)
        }
        val saveExif = ExifInterface(savePath)
        var value: String?
        for (tag in tags) {
            value = sourceExif.getAttribute(tag)
            if (!TextUtils.isEmpty(value)) {
                saveExif.setAttribute(tag, value)
            }
        }
        saveExif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, outputWidth.toString())
        saveExif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, outputHeight.toString())
        saveExif.setAttribute(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED.toString()
        )
        saveExif.saveAttributes()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/**
 * @param context The context
 * @param uri     The Uri of Google Drive file
 * @return Google Drive file
 */
fun getGoogleDriveFile(context: Context?, uri: Uri?): File? {
    if (uri == null) return null
    var input: FileInputStream? = null
    var output: FileOutputStream? = null
    val filePath = File(context?.cacheDir, "tmp").absolutePath
    try {
        val pfd = context?.contentResolver!!.openFileDescriptor(uri, "r") ?: return null
        val fd = pfd.fileDescriptor
        input = FileInputStream(fd)
        output = FileOutputStream(filePath)
        var read: Int
        val bytes = ByteArray(4096)
        while (input.read(bytes).also { read = it } != -1) {
            output.write(bytes, 0, read)
        }
        return File(filePath)
    } catch (ignored: IOException) {
    } finally {
        closeQuietly(input)
        closeQuietly(output)
    }
    return null
}

fun updateGalleryInfo(context: Context, uri: Uri) {
    if (ContentResolver.SCHEME_CONTENT != uri.scheme) {
        return
    }
    val values = ContentValues()
    val file: File = getFileFromUri(context, uri)!!
    if (file != null && file.exists()) {
        values.put(MediaStore.Images.Media.SIZE, file.length())
    }
    val resolver = context.contentResolver
    resolver.update(uri, values, null, null)
}

fun getExifOrientation(context: Context?, uri: Uri): Int {
    val authority = uri.authority!!.toLowerCase()
    val orientation: Int
    orientation = if (authority.endsWith("media")) {
        getExifRotation(context!!, uri)
    } else {
        getExifRotation(getFileFromUri(context, uri))
    }
    return orientation
}

fun getExifRotation(file: File?): Int {
    if (file == null) return 0
    try {
        val exif = ExifInterface(file.absolutePath)
        return getRotateDegreeFromOrientation(
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        )
    } catch (e: IOException) {
        Log.e("An error occurred : " + e.message, e.toString())
    }
    return 0
}

fun getExifRotation(context: Context, uri: Uri?): Int {
    var cursor: Cursor? = null
    val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
    return try {
        cursor = context.contentResolver.query(uri!!, projection, null, null, null)
        if (cursor == null || !cursor.moveToFirst()) {
            0
        } else cursor.getInt(0)
    } catch (ignored: RuntimeException) {
        0
    } finally {
        cursor?.close()
    }
}

fun getRotateDegreeFromOrientation(orientation: Int): Int {
    var degree = 0
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
        ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
        ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
        else -> {
        }
    }
    return degree
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}



fun Activity.requestFocus(view: View) {
    if (view.requestFocus()) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}






@Throws(IOException::class)
fun getBytes(inputStream: InputStream): ByteArray? {
    val byteBuff = ByteArrayOutputStream()
    val buffSize = 1024
    val buff = ByteArray(buffSize)
    var len = 0
    while (inputStream.read(buff).also { len = it } != -1) {
        byteBuff.write(buff, 0, len)
    }
    return byteBuff.toByteArray()
}


/**
 * json to pojo with type class
 *
 * @param jsonString
 * @param pojoType
 * @return
 */
fun jsonToPojo(jsonString: String, pojoType: Type): Any {
    return Gson().fromJson(jsonString, pojoType)
}

/**
 * json to pojo
 *
 * @param jsonString
 * @param pojoClass
 * @return
 */
fun jsonToPojo(jsonString: String, pojoClass: Class<*>): Any {
    return Gson().fromJson(jsonString, pojoClass)
}

/**
 * checks is device's screen is locked or not
 *
 * @return
 */
fun Context.isScreenLocked(): Boolean {
    val keyguardManager: KeyguardManager? = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
    val powerManager: PowerManager? = getSystemService(Context.POWER_SERVICE) as PowerManager?
    val locked = keyguardManager != null && keyguardManager.isKeyguardLocked
    val interactive = powerManager != null && powerManager.isInteractive
    return locked || !interactive
}

fun getPathFromURI(context: Context, uri: Uri): String? {
    val isKitKat = VERSION.SDK_INT >= VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId: String = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id: String = DocumentsContract.getDocumentId(uri)
            val contentUri: Uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
            return getDataColumns(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId: String = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = Audio.Media.EXTERNAL_CONTENT_URI
            } else {
                contentUri = Files.getContentUri("external")
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumns(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumns(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun getDataColumns(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun getRandomColor(): Int {
    val rnd = Random()
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}

fun checkTrimFolder(): String? {
    val root = Environment.getExternalStorageDirectory()
    val file = File(root.absolutePath + "/HOW/.trimvideo")
    //        File file = new File(context.getCacheDir() + "/HOW/.trimvideo");
    var isDirectoryCreated = file.exists()
    if (!isDirectoryCreated) {
        isDirectoryCreated = file.mkdir()
    }
    return if (isDirectoryCreated) {
        file.absolutePath
    } else file.absolutePath
}

fun onShareClicked(context: Activity) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
        var shareMessage = "\nLet me recommend you this application\n\n"
        shareMessage = """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: java.lang.Exception) {
        //e.toString();
    }
}


fun getEmojiByUnicode(unicode: Int): String? {
    return String(Character.toChars(unicode))
}

fun Context.saveMediaToStorage(bitmap: Bitmap) {
    val filename = "${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }
    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        //showToast("Saved to Photos")
    }
}

fun getContactDataFromString(contact: String, which: Int): String? {
    return contact.split(";").toTypedArray()[which]
}

fun View.animateScaleView() {
    this.animate()
        .scaleX(0.3F)
        .scaleY(0.3F)
        .setDuration(150)
        .withEndAction { this.animate().scaleX(1F).scaleY(1F).duration = 150 }
}


fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = Math.round(dpToPx(this, 50F).toDouble())
    return heightDiff > marginOfError
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}