package stuff.mykolamiroshnychenko.accessibilityapp.ui

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import kotlin.mykolamiroshnychenko.accessibilityapp.R
import stuff.mykolamiroshnychenko.accessibilityapp.services.ChatHeadService

/**
 * Created by mykolamiroshnychenko on 2/19/18.
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (shouldExplicitlyRequestPermission()) {
            startSystemDialogWithOverlayPermission()
        } else {
            initializeView()
        }
    }

    private fun initializeView() {
        findViewById<View>(R.id.start_btn).setOnClickListener {
            startService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            handleCoreDrawPermission(resultCode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleCoreDrawPermission(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            initializeView()
        } else {
            Toast.makeText(this,
                    R.string.draw_permission_not_available,
                    Toast.LENGTH_SHORT).show()

            finish()
        }
    }

    private fun shouldExplicitlyRequestPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)
    }

    private fun startSystemDialogWithOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName))
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
    }

    private fun startService() {
        val intent = Intent(this@MainActivity, ChatHeadService::class.java)
        startService(intent)
        finish()
    }

    companion object {
        private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    }
}
