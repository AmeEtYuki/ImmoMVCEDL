package com.example.oioj

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MesLogementsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ml_mes_logements)

        val btnBackMesLogements = findViewById<Button>(R.id.btnBackMesLogements);
        btnBackMesLogements.setOnClickListener {
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
        GlobalScope.launch(Dispatchers.Main) {
                retrieveLogement()
        }

    }

    private suspend fun retrieveLogement() {
        return withContext(Dispatchers.IO) {
            try {
                val token = gestionToken.getToken()
                val url = URL("https://api.friedrichalyssa.com/?action=bien")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")

                val jsonObject = JSONObject().apply {
                    put("token", token)
                }
                println("datasent : $jsonObject")

                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()

                val responseCode = httpURLConnection.responseCode
                println("Response Code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                       val jsonArray = JSONArray(response)

                    val logementCard = findViewById<LinearLayout>(R.id.logementCard)
                    val inflater = LayoutInflater.from(this@MesLogementsActivity)

                    for (i in 0 until jsonArray.length()) {
                        val logement = jsonArray.getJSONObject(i)
                        val id = logement.getInt("id")
                        val rue = logement.getString("rue")
                        val cp = logement.getString("cp")
                        val ville = logement.getString("ville")
                        val anneeConstru = logement.getInt("anneeConstru")
                        val description = logement.getString("description")
                        val id_user = logement.getInt("id_user")
                        println("$id , $rue , $cp , $ville, $anneeConstru , $description, $id_user")

                        runOnUiThread {
                            val cardView = inflater.inflate(R.layout.ml_logement_card, null)
                            val txtNomLogement = cardView.findViewById<TextView>(R.id.txtNomLogement)
                            val txtCodePostal = cardView.findViewById<TextView>(R.id.txtCodePostal)
                            val txtRue = cardView.findViewById<TextView>(R.id.txtRue)

                            txtNomLogement.text = "Logement $id"
                            txtCodePostal.text = "Code Postal: $cp"
                            txtRue.text = "Rue: $rue"

                            logementCard.addView(cardView)
                        }

                    }
                } else {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val contentReponse = JSONObject(response)
                    println(contentReponse)
                }
            } catch (e: Exception) {
                println("Erreur lors de la récupération des logements: ${e.message}")
            }
        }
    }


}