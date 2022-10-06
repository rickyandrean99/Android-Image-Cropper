package com.rickyandrean.imagecropper

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.rickyandrean.imagecropper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        //if (it.resultCode == RESULT_OK) { }

        val result = it.data?.getStringExtra("picture")
        var selectedImg = it.data?.data

        if (result != null) {
            selectedImg = Uri.parse(result)
        }

        val intent = Intent(this@MainActivity, UcropperActivity::class.java)
        intent.putExtra("SendImageData", selectedImg.toString())
        launcherCropImage.launch(intent)
    }

    private val launcherCropImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val result = it.data?.getStringExtra("CROP")
        var uri = it.data?.data

        if (result != null) {
            uri = Uri.parse(result)
        }

        binding.imageview.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageBtn.setOnClickListener {
//            val intent = Intent().also {
//                it.action = Intent.ACTION_GET_CONTENT
//                it.type = "image/*"
//            }
//
//            val chooser = Intent.createChooser(intent, "Choose Plant Picture")
//            launcherGallery.launch(chooser)

            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10
    }
}