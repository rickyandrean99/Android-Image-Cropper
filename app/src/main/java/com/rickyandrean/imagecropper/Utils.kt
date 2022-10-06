package com.rickyandrean.imagecropper

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

val timeStamp: String =
    SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(System.currentTimeMillis())

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun cropImage(bitmap: Bitmap, context: Context): File {
    // Cropping Image
    val cropBitmap = Bitmap.createBitmap(
        bitmap,
        bitmap.width * 2 / 8,
        (bitmap.height - bitmap.width * 4 / 8) / 2,
        bitmap.width * 4 / 8,
        bitmap.width * 4 / 8,
        Matrix(),
        true
    )

    // Create File
    val file = File(context.cacheDir, "image.png")
    file.createNewFile()

    val bos = ByteArrayOutputStream()
    cropBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
    val bitmapData = bos.toByteArray()

    FileOutputStream(file).apply {
        write(bitmapData)
        flush()
        close()
    }

    return file
}

fun processImage(bm: Bitmap, context: Context): File {
    val file = cropImage(bm, context)

    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)

        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int

    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)

    outputStream.close()
    inputStream.close()

    return myFile
}

fun createCustomTempFile(context: Context): File = File.createTempFile(
    timeStamp,
    ".jpg",
    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
)