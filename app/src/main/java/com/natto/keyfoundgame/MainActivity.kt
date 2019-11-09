package com.natto.keyfoundgame

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_CAMERA_PERMISSION: Int = 100
        const val REQUEST_CODE_CAMERA: Int = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        key.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("鍵を発見！")
                .show()
        }

        takePhotoButton.setOnClickListener {
            cameraTask()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
//            photoImageView.setImageURI(pictureUri)

            val bitmap = data?.extras?.get("data") as Bitmap
            photoImageView.setImageBitmap(bitmap)

            val randWidth = Random.nextInt(key_frame.width)
            val randHeight = Random.nextInt(key_frame.width * (bitmap.height / bitmap.width))
            key.apply {
                translationX = randWidth.toFloat()
                translationY = randHeight.toFloat()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            // requestPermissionsで設定した順番で結果が格納されています。
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可されたので処理を続行
                takePicture()
            } else {
                // パーミッションのリクエストに対して「許可しない」
                // または以前のリクエストで「二度と表示しない」にチェックを入れられた状態で
                // 「許可しない」を押されていると、必ずここに呼び出されます。
                Toast.makeText(this, "パーミッションが許可されていません。", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun cameraTask() {
        // カメラの権限の確認
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 許可されていない
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                // すでに１度パーミッションのリクエストが行われていて、
                // ユーザーに「許可しない（二度と表示しないは非チェック）」をされていると
                // この処理が呼ばれます。
                Toast.makeText(this, "パーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else {
                // パーミッションのリクエストを表示
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION
                )
            }
            return
        }
        // 許可されている、またはAndroid 6.0以前
        takePicture()
    }

    fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }
}
