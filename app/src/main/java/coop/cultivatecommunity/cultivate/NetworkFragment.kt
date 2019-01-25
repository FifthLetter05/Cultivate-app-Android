package coop.cultivatecommunity.cultivate

import android.support.v4.app.Fragment
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.FragmentManager

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.UnsupportedEncodingException
import java.net.URL

import javax.net.ssl.HttpsURLConnection

class NetworkFragment : Fragment() {

    private var mCallback: DownloadCallback<String>? = null
    private var mDownloadTask: DownloadTask? = null
    private var mUrlString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUrlString = arguments?.getString(URL_KEY)
        // Retain this Fragment across configuration changes in the host Activity.
        retainInstance = true
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Host Activity will handle callbacks from task.
        mCallback = context as DownloadCallback<String>?
    }

    override fun onDetach() {
        super.onDetach()
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null
    }

    override fun onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload()
        super.onDestroy()
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    fun startDownload() {
        cancelDownload()
        mDownloadTask = DownloadTask(mCallback!!)
        mDownloadTask!!.execute(mUrlString)
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    fun cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask!!.cancel(true)
        }
    }

    private class DownloadTask internal constructor(callback: DownloadCallback<String>) : AsyncTask<String, Int, DownloadTask.Result>() {

        private var mCallback: DownloadCallback<String>? = null

        init {
            setCallback(callback)
        }

        internal fun setCallback(callback: DownloadCallback<String>) {
            mCallback = callback
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        internal class Result {
            var mResultValue: String? = null
            var mException: Exception? = null

            constructor(resultValue: String) {
                mResultValue = resultValue
            }

            constructor(exception: Exception) {
                mException = exception
            }
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        override fun onPreExecute() {
            if (mCallback != null) {
                val networkInfo = mCallback!!.activeNetworkInfo
                if (networkInfo == null || !networkInfo.isConnected ||
                        networkInfo.type != ConnectivityManager.TYPE_WIFI && networkInfo.type != ConnectivityManager.TYPE_MOBILE) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback!!.updateFromDownload(null)
                    cancel(true)
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        override fun doInBackground(vararg urls: String): DownloadTask.Result? {
            var result: Result? = null
            if (!isCancelled && urls != null && urls.size > 0) {
                val urlString = urls[0]
                try {
                    val url = URL(urlString)
                    val resultString = downloadUrl(url)
                    if (resultString != null) {
                        result = Result(resultString)
                    } else {
                        throw IOException("No response received.")
                    }
                } catch (e: Exception) {
                    result = Result(e)
                }

            }
            return result
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        override fun onPostExecute(result: Result?) {
            if (result != null && mCallback != null) {
                if (result.mException != null) {
                    mCallback!!.updateFromDownload(result.mException!!.message!!)
                } else if (result.mResultValue != null) {
                    mCallback!!.updateFromDownload(result.mResultValue!!)
                }
                mCallback!!.finishDownloading()
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        override fun onCancelled(result: Result) {}

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        @Throws(IOException::class)
        private fun downloadUrl(url: URL): String? {
            var stream: InputStream? = null
            var connection: HttpsURLConnection? = null
            var result: String? = null
            try {
                connection = url.openConnection() as HttpsURLConnection
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.readTimeout = 3000
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.connectTimeout = 3000
                // For this use case, set HTTP method to GET.
                connection.requestMethod = "GET"
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.doInput = true
                // Open communications link (network traffic occurs here).
                connection.connect()
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS)
                val responseCode = connection.responseCode
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: " + responseCode)
                }
                // Retrieve the response body as an InputStream.
                stream = connection.inputStream
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0)
                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 500)
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close()
                }
                if (connection != null) {
                    connection.disconnect()
                }
            }
            return result
        }

        /**
         * Converts the contents of an InputStream to a String.
         */
        @Throws(IOException::class, UnsupportedEncodingException::class)
        fun readStream(stream: InputStream, maxReadSizePass: Int): String {
            var maxReadSize = maxReadSizePass
            val reader: Reader?
            reader = InputStreamReader(stream, "UTF-8")
            val rawBuffer = CharArray(maxReadSize)
            var readSize: Int
            val buffer = StringBuffer()
            readSize = reader.read(rawBuffer)
            while (readSize != -1 && maxReadSize > 0) {
                if (readSize > maxReadSize) {
                    readSize = maxReadSize
                }
                buffer.append(rawBuffer, 0, readSize)
                maxReadSize -= readSize
            }
            return buffer.toString()
        }
    }

    companion object {
        val TAG = "NetworkFragment"

        private val URL_KEY = "UrlKey"

        /**
         * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
         * from.
         */
        fun getInstance(fragmentManager: FragmentManager, url: String): NetworkFragment {
            // Recover NetworkFragment in case we are re-creating the Activity due to a config change.
            // This is necessary because NetworkFragment might have a task that began running before
            // the config change occurred and has not finished yet.
            // The NetworkFragment is recoverable because it calls setRetainInstance(true).
            var networkFragment: NetworkFragment? = fragmentManager
                    .findFragmentByTag(NetworkFragment.TAG) as NetworkFragment
            if (networkFragment == null) {
                networkFragment = NetworkFragment()
                val args = Bundle()
                args.putString(URL_KEY, url)
                networkFragment.arguments = args
                fragmentManager.beginTransaction().add(networkFragment, TAG).commit()
            }
            return networkFragment
        }
    }
}
