package eu.bindworks.scandocumentpoc

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.labters.documentscanner.ImageCropActivity
import com.labters.documentscanner.helpers.ScannerConstants
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
	val REQUEST_IMAGE_CAPTURE = 1
	val REQUEST_CROP = 2
	lateinit var currentPhotoPath: String

	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

		findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
			dispatchTakePictureIntent()
		}
    }
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			ScannerConstants.backText = "Zpět"
			ScannerConstants.backColor = "#999999"
			ScannerConstants.cropText = "Pokračovat"
			ScannerConstants.cropColor = "#E2001A"
			ScannerConstants.progressColor = "#E2001A"
			BitmapFactory.decodeFile(currentPhotoPath)?.also { bitmap ->
				ScannerConstants.selectedImageBitmap=bitmap
				startActivityForResult(Intent(MainActivity@this, ImageCropActivity::class.java), REQUEST_CROP)
			}
		}
		if (requestCode==REQUEST_CROP && resultCode== RESULT_OK )
		{
			if (ScannerConstants.selectedImageBitmap!=null){
				findViewById<ImageView>(R.id.picture).setImageBitmap(ScannerConstants.selectedImageBitmap)
			}
		}
		super.onActivityResult(requestCode,resultCode,data);
	}

	private fun dispatchTakePictureIntent() {
		Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
			// Ensure that there's a camera activity to handle the intent
			takePictureIntent.resolveActivity(packageManager)?.also {
				// Create the File where the photo should go
				val photoFile: File? = try {
					createImageFile()
				} catch (ex: IOException) {
					// Error occurred while creating the File
					print("failed to save file")
					null
				}
				// Continue only if the File was successfully created
				photoFile?.also {
					val photoURI: Uri = FileProvider.getUriForFile(
						this,
						"eu.bindworks.scandocumentpoc.android.fileprovider",
						it
					)
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
					startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
				}
			}
		}
	}

	@Throws(IOException::class)
	private fun createImageFile(): File {
		// Create an image file name
		val name = UUID.randomUUID().toString()
		val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
		return File.createTempFile(
			name, /* prefix */
			".jpg", /* suffix */
			storageDir /* directory */
		).apply {
			// Save a file: path for use with ACTION_VIEW intents
			currentPhotoPath = absolutePath
		}
	}
}
