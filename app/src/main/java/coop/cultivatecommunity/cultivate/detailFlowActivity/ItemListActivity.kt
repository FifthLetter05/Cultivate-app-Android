package coop.cultivatecommunity.cultivate.detailFlowActivity

import android.app.FragmentManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import coop.cultivatecommunity.cultivate.BaseNavActivity
import coop.cultivatecommunity.cultivate.DownloadCallback
import coop.cultivatecommunity.cultivate.NetworkFragment
import coop.cultivatecommunity.cultivate.R
import coop.cultivatecommunity.cultivate.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity() : BaseNavActivity(), DownloadCallback<Any> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false
    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private var mNetworkFragment: NetworkFragment? = null

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private var mDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        //mNetworkFragment = NetworkFragment.getInstance(supportFragmentManager, "https://www.google.com")

        setupRecyclerView(item_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane)
    }

    override fun getNavigationMenuItemId(): Int {
        return R.id.navigation_home
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_item_list
    }

    private fun startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment?.startDownload()
            mDownloading = true
        }
    }

    override fun updateFromDownload(result: Any?) {
        // Update your UI here based on result of download.
    }

    override val activeNetworkInfo: NetworkInfo get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

    override fun onProgressUpdate(progressCode: Int, percentComplete: Int) {
        when(progressCode) {
            // You can add UI behavior for progress updates here.
            DownloadCallback.Progress.ERROR -> {

            }
            DownloadCallback.Progress.CONNECT_SUCCESS -> {

            }
            DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS -> {

            }
            DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS -> {

            }
            DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS -> {

            }
        }
    }

    override fun finishDownloading() {
        mDownloading = false
        mNetworkFragment?.cancelDownload()
    }

}
