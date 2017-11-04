package coop.cultivatecommunity.cultivate

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var logo = ImageView(this)

        logo.setImageURI()

        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
