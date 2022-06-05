package whatsapp.messaging.tool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import whatsapp.messaging.sdk.WhatsappBuilder
import whatsapp.messaging.tool.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnSend.setOnClickListener {

            WhatsappBuilder.sendMessage(
                this,
                "Token", //token
                "version", //v13.0
                "Phone Number ID", //103075932423434
                binding.etNumber.text.toString(),
                binding.etMessage.text.toString(),
                "en_US",
            ) { response ->

                Handler(Looper.getMainLooper()).post {
                    if (response?.getMessage() == null) {
                        Toast.makeText(
                            applicationContext,
                            "Message not sent",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            response.getMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }
}