package coop.cultivatecommunity.cultivate

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
