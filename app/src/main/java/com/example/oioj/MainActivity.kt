package com.example.oioj
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val mail = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val mdp = findViewById<EditText>(R.id.editTextPassword).text.toString()
            GlobalScope.launch(Dispatchers.Main) {
                val authenticated = authenticate(mail, mdp)
                if (authenticated) {
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Connexion échouée !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun authenticate(mail: String, mdp: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("http://api.immomvc.varin.ovh/?action=login")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                httpURLConnection.doOutput = true
                //envoie data
                val jsonObject = JSONObject().apply {
                    put("login", mail)
                    put("password", mdp)
                }
                println("Data sent: $jsonObject")

                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()

                val responseCode = httpURLConnection.responseCode
                println("Response Code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val reponse = inputStream.bufferedReader().use { it.readText() }
                    println(reponse)
                    val contentReponse = JSONObject(reponse)
                    println(contentReponse)
                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                    intent.putExtra("apiReponse",contentReponse.toString())
                    startActivity(intent)
                    true
                }else{
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
