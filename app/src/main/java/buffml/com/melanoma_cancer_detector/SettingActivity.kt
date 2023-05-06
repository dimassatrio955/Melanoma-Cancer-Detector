package buffml.com.melanoma_cancer_detector

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*
import kotlin.system.exitProcess

class SettingActivity : AppCompatActivity() {
    private val pref by lazy { PrefHelper(this)}

    private lateinit var exit_button: ImageButton

    private lateinit var select_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        if (pref.getBoolean("pref_is_dark_mode")) {
            switch_dark.isChecked = true
        } else {
            switch_dark.isChecked = false
        }
        switch_dark.setOnCheckedChangeListener { compoundButton, isChecked ->
            when (isChecked) {
                true -> {
                    pref.put("pref_is_dark_mode", true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                false -> {
                    pref.put("pref_is_dark_mode", false)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        select_button = findViewById(R.id.select_button)
        select_button.setOnClickListener {
            startActivity(
                Intent(
                    this@SettingActivity,
                    LanguageActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        exit_button = findViewById(R.id.exit_button)
        exit_button.setOnClickListener {
            startActivity(
                Intent(
                    this@SettingActivity,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}
