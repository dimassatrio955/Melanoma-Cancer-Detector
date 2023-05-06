package buffml.com.melanoma_cancer_detector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class LanguageActivity : AppCompatActivity() {

    private lateinit var exit_button: ImageButton

    lateinit var spinner: Spinner
    lateinit var locale: Locale
    private var currentLanguage = "in"
    private var currentLang: String? = null.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        if (currentLang == null) {
            currentLanguage = "in"
        } else {
            currentLanguage = "en"
        }
        spinner = findViewById(R.id.spinner)
        val list = ArrayList<String>()
        list.add("")
        list.add("English")
        list.add("Indonesian")

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                    }
                    1 -> setLocale("en")
                    2 -> setLocale("in")

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        exit_button = findViewById(R.id.exit_button)
        exit_button.setOnClickListener {
            startActivity(
                Intent(
                    this@LanguageActivity,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
    private fun setLocale(localeName: String) = if (localeName == currentLanguage) {
        locale = Locale(localeName)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(
            this,
            MainActivity::class.java
        )
        refresh.putExtra(currentLang, localeName)
        startActivity(refresh)
    } else {
        locale = Locale(localeName)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(
            this,
            MainActivity::class.java
        )
        refresh.putExtra(currentLang, localeName)
        startActivity(refresh)
    }
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        exitProcess(0)
    }
}