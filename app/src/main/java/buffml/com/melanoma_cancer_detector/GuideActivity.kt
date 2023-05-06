package buffml.com.melanoma_cancer_detector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class GuideActivity : AppCompatActivity() {
    private lateinit var exit_button: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        exit_button = findViewById(R.id.exit_button)
        exit_button.setOnClickListener {
            startActivity(
                Intent(
                    this@GuideActivity,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}