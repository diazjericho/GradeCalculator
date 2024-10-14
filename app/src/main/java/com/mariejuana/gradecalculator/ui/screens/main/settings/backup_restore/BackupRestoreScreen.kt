package com.mariejuana.gradecalculator.ui.screens.main.settings.backup_restore

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mariejuana.gradecalculator.data.database.models.ActivityModel
import com.mariejuana.gradecalculator.data.database.models.CategoryModel
import com.mariejuana.gradecalculator.data.database.models.SemesterModel
import com.mariejuana.gradecalculator.data.database.models.SubjectModel
import com.mariejuana.gradecalculator.data.database.models.YearLevelModel
import com.mariejuana.gradecalculator.databinding.ActivityBackupRestoreScreenBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.io.File
import java.util.jar.Manifest

class BackupRestoreScreen : AppCompatActivity() {
    private lateinit var binding: ActivityBackupRestoreScreenBinding
    private val STORAGE_PERMISSION_CODE = 23

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupRestoreScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textInstructions.setText(
            "The app offers two options, Backup and Restore:\n" +
                    "- When performing a backup, the database will be saved to the Documents > StudyTrackBackup folder on your device.\n" +
                    "- When restoring the database, make sure to use the same database that was backed up by the app.\n" +
                    "Any existing data will be overwritten, and the backup file will replace the current database."
        )

        binding.buttonSettingsBackup.setOnClickListener {
            // Check if permissions are granted
            if (checkPermission()) {
                // Proceed with backup if permissions are granted
                backupRealm(this)
            } else {
                // Request the necessary permissions
                requestStoragePermissions()
            }
        }

        binding.buttonSettingsRestore.setOnClickListener {
            // Check if permissions are granted
            if (checkPermission()) {
                // Proceed with backup if permissions are granted
                restoreRealm(this)
            } else {
                // Request the necessary permissions
                requestStoragePermissions()
            }
        }
    }

    object RealmInstance {
        private var realmInstance: Realm? = null
        private val realmConfig: RealmConfiguration = RealmConfiguration
            .Builder(schema = setOf(YearLevelModel::class, SemesterModel::class, SubjectModel::class, CategoryModel::class, ActivityModel::class))
            .name("study_track.realm")
            .schemaVersion(1)  // Ensure the schema version matches across the app
            .build()

        fun getInstance(): Realm {
            if (realmInstance == null) {
                realmInstance = Realm.open(realmConfig)
            }
            return realmInstance!!
        }

        fun closeInstance() {
            realmInstance?.close()
            realmInstance = null
        }
    }

    private fun backupRealm(context: Context): Boolean {
        return try {
            // Open Realm using the singleton instance
            val realm = RealmInstance.getInstance()

            // Get the path of the Realm database file
            val realmFile = File(realm.configuration.path)

            // Define the backup location in the phone's main "Documents" folder
            val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "StudyTrackBackup")
            if (!backupDir.exists()) {
                backupDir.mkdirs()  // Create directory if it doesn't exist
            }

            // Create the backup file
            val backupFile = File(backupDir, "study_track_backup.realm")

            // Copy the Realm database file to the backup location
            realmFile.copyTo(backupFile, overwrite = true)

            // Close Realm instance
            RealmInstance.closeInstance()

            Toast.makeText(context, "Backup success!", Toast.LENGTH_SHORT).show()
            true  // Backup was successful
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Backup failed!", Toast.LENGTH_SHORT).show()
            false  // Backup failed
        }
    }


    private fun restoreRealm(context: Context): Boolean {
        return try {
            // Use the singleton instance to ensure consistent Realm configuration
            RealmInstance.closeInstance() // Close any open instance before restoring

            // Define the backup location (where the backup file is stored)
            val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "StudyTrackBackup")
            val backupFile = File(backupDir, "study_track_backup.realm")

            // Check if the backup file exists
            if (!backupFile.exists()) {
                Toast.makeText(context, "Backup file not found!", Toast.LENGTH_SHORT).show()
                return false  // Backup file doesn't exist
            }

            // Get the path of the original Realm database
            val realmConfig = RealmInstance.getInstance().configuration
            val realmFile = File(realmConfig.path)

            // Close Realm before restoring the database
            RealmInstance.closeInstance()  // Ensure Realm is closed before restoring

            // Replace the current Realm database with the backup file
            backupFile.copyTo(realmFile, overwrite = true)

            // Reopen Realm with the restored database using the singleton instance
            RealmInstance.getInstance()

            Toast.makeText(context, "Restore success!", Toast.LENGTH_SHORT).show()
            true  // Restore was successful
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Restore failed!", Toast.LENGTH_SHORT).show()
            false  // Restore failed
        }
    }

    // Function to check and request permission
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    // Check if WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions are granted
    private fun hasStoragePermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // Request the necessary storage permissions
    private fun requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION

                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)

            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val read = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (write && read) {
                        Toast.makeText(this, "Storage permissions granted!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Storage permissions are required for backup and restore.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}