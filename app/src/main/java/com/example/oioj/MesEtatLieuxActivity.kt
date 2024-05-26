package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MesEtatLieuxActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mes_mes_etats_lieux)

        val btnBackMesEtatLieux = findViewById<Button>(R.id.btnBackMesEtatLieux)
        btnBackMesEtatLieux.setOnClickListener {
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
        GlobalScope.launch(Dispatchers.Main) {
            retrieveUserEtatDesLieux()
            retrieveUserEtatDesLieuxSortie()
        }
    }
    //sortie
    private suspend fun retrieveUserEtatDesLieuxSortie() {
        return withContext(Dispatchers.IO) {
            try {
                val token = gestionToken.getToken()
                val url =
                    URL("https://api.friedrichalyssa.com/?action=getUserMELSortie") /////////////////////////////////////////////
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                val jsonObject = JSONObject().apply {
                    put("token", token)
                }
                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()
                val responseCode = httpURLConnection.responseCode
                println("Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(response)
                    println(jsonArray)

                    val containerEtatLieuxSortie = findViewById<LinearLayout>(R.id.mesEtatsLieuxContentSortie)
                    val inflater = LayoutInflater.from(this@MesEtatLieuxActivity)
                    for (i in 0 until jsonArray.length()) {
                        val etatlieux = jsonArray.getJSONObject(i)
                        val id = etatlieux.getInt("id")
                        val dateEtat = etatlieux.getString("date_etat")
                        val commentaire = etatlieux.getString("commentaire")
                        val description = etatlieux.getString("description")

                        runOnUiThread {
                            val etatlieuxLayout = LayoutInflater.from(this@MesEtatLieuxActivity)
                                .inflate(R.layout.mes_etatlieuxcard, containerEtatLieuxSortie, false)
                            etatlieuxLayout.id = id
                            val txtNomLogement =
                                etatlieuxLayout.findViewById<TextView>(R.id.txtNomLogementMES)
                            val txtDateEtatLieux =
                                etatlieuxLayout.findViewById<TextView>(R.id.txtDateEtatMES)
                            val txtCommentaire =
                                etatlieuxLayout.findViewById<TextView>(R.id.txtCommentaireMES)

                            txtNomLogement.text = description
                            txtDateEtatLieux.text = dateEtat
                            txtCommentaire.text = commentaire
                            containerEtatLieuxSortie.addView(etatlieuxLayout)
                        }
                    }

                } else {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val contentReponse = JSONObject(response)
                    println(contentReponse)
                }

            } catch (e: Exception) {
                println("Erreur lors de la récupération des etats des lieux: ${e.message}")
            }
        }
    }
    //entree
    private suspend fun retrieveUserEtatDesLieux() {
        return withContext(Dispatchers.IO) {
            try {
                val token = gestionToken.getToken()
                val url = URL("https://api.friedrichalyssa.com/?action=getUserMEL") /////////////////////////////////////////////
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                val jsonObject = JSONObject().apply {
                    put("token", token)
                }
                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()
                val responseCode = httpURLConnection.responseCode
                println("Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(response)
                    println(jsonArray)

                    val containerEtatLieux = findViewById<LinearLayout>(R.id.mesEtatsLieuxContent)
                    val inflater = LayoutInflater.from(this@MesEtatLieuxActivity)
                    for (i in 0 until jsonArray.length()) {
                        val etatlieux = jsonArray.getJSONObject(i)
                        val id = etatlieux.getInt("id")
                        val dateEtat = etatlieux.getString("date_etat")
                        val commentaire = etatlieux.getString("commentaire")
                        val description = etatlieux.getString("description")

                        runOnUiThread {
                            val etatlieuxLayout = LayoutInflater.from(this@MesEtatLieuxActivity).inflate(R.layout.mes_etatlieuxcard, containerEtatLieux, false)
                            etatlieuxLayout.id = id
                            val txtNomLogement = etatlieuxLayout.findViewById<TextView>(R.id.txtNomLogementMES)
                            val txtDateEtatLieux = etatlieuxLayout.findViewById<TextView>(R.id.txtDateEtatMES)
                            val txtCommentaire = etatlieuxLayout.findViewById<TextView>(R.id.txtCommentaireMES)

                            txtNomLogement.text = description
                            txtDateEtatLieux.text = dateEtat
                            txtCommentaire.text = commentaire
                            containerEtatLieux.addView(etatlieuxLayout)
                        }
                    }

                } else {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val contentReponse = JSONObject(response)
                    println(contentReponse)
                }

            } catch (e: Exception) {
                println("Erreur lors de la récupération des etats des lieux: ${e.message}")
            }
        }

    }

}