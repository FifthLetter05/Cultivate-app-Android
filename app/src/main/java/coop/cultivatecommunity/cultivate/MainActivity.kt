package coop.cultivatecommunity.cultivate

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var content: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //content = createMainView(R.layout.content_main)
        //container.addView(content)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //container.removeView(content)
                //content = createMainView(R.layout.content_main)
                //container.addView(content)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //container.removeView(content)
                //content = createMainView(R.layout.content_extra)
                //container.addView(content)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun createMainView(@LayoutRes res: Int): View =
            LayoutInflater.from(this).inflate(res, container, false)
}
