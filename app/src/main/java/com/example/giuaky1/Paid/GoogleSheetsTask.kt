package com.example.giuaky1.Paid

import android.os.AsyncTask
import android.util.Log
import com.example.giuaky1.Interfaces.OnTaskCompleted
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GoogleSheetsTask(private val listener: OnTaskCompleted?) :
    AsyncTask<Void?, Void?, String?>() {

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            try {
                // Using the JSON simple library parse the string into a json object
                val dataObject = JSONObject(result)
                val dataArray = dataObject.getJSONArray("data")
                val lastPaid = dataArray.getJSONObject(dataArray.length() - 1)
                val price = lastPaid.getString("Giá trị")
                val describe = lastPaid.getString("Mô tả")
                Log.d("GGS", price)
                Log.d("GGS", describe)
                listener?.onTaskCompleted(price, describe)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            Log.e("GGS", "Error fetching data")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): String? {
        var connection: HttpURLConnection? = null
        try {
            val url =
                URL("https://script.google.com/macros/s/AKfycbw45jCYcr8I2w8EcmLbPDdWYUHmX4_0xuZVjg7NRpkuhJWUGL6JnMLEHBs2887Thzp75Q/exec")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection!!.connect()
            val responseCode = connection.responseCode
            return if (responseCode != 200) {
                throw RuntimeException("HttpResponseCode: $responseCode")
            } else {
                val inline = StringBuilder()
                val reader = BufferedReader(
                    InputStreamReader(
                        connection.inputStream
                    )
                )
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    inline.append(line)
                }
                reader.close()
                inline.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return null
    }

}
