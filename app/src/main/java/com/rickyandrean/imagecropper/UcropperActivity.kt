package com.rickyandrean.imagecropper

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import java.io.File
import java.lang.StringBuilder
import java.util.*

class UcropperActivity : AppCompatActivity() {
    private lateinit var sourceUri: String
    private lateinit var destinationUri: String
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucropper)

        if (intent.extras != null) {
            sourceUri = intent.getStringExtra("SendImageData") as String
            uri = Uri.parse(sourceUri)
        }

        destinationUri = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()

        UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationUri)))
            .withOptions(UCrop.Options())
            .withAspectRatio(1F, 1F)
            .withMaxResultSize(2000, 2000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri = UCrop.getOutput(data!!)!!

            val intent = Intent()
            intent.putExtra("CROP", "$resultUri")
            setResult(101, intent)
            finish()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError: Throwable = UCrop.getError(data!!)!!
        }
    }
}