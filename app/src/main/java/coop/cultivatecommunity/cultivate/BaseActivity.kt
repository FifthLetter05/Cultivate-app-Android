package coop.cultivatecommunity.cultivate

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import coop.cultivatecommunity.cultivate.detailFlowActivity.ItemDetailActivity
import coop.cultivatecommunity.cultivate.detailFlowActivity.ItemListActivity

abstract class BaseActivity : AppCompatActivity() {

    lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            //window.enterTransition = Fade()
            //window.exitTransition = Fade()
        }
        setContentView(getContentViewId())

        navigationView = findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)
    }

    override fun onStart() {
        super.onStart()
        updateNavigationBarState()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                startActivity(Intent(this, ItemListActivity::class.java)/*, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ActivityOptions.makeSceneTransitionAnimation(this).toBundle() else null*/)
                Log.i(Reference.TAG, "Hello")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                startActivity(Intent(this, ItemDetailActivity::class.java)/*, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ActivityOptions.makeSceneTransitionAnimation(this).toBundle() else null*/)
                Log.i(Reference.TAG, "Hello")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    //overrides reselected listener so no fading happens when selecting selected item
    private val mOnNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemReselectedListener { _ -> }

    private fun updateNavigationBarState() {
        val actionId = getNavigationMenuItemId()
        selectBottomNavigationBarItem(actionId)
    }

    fun selectBottomNavigationBarItem(itemId: Int) {
        val menu = navigationView.menu
        val size = menu.size()
        for (i in 0..(size-1)) {
            val item = menu.getItem(i)
            val shouldBeChecked = item.itemId == itemId
            if (shouldBeChecked) {
                item.isChecked = true
                break
            }
        }
    }

    abstract fun getContentViewId(): Int

    abstract fun getNavigationMenuItemId(): Int
}
