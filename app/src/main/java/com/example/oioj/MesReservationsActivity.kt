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


class MesReservationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mr_mes_reservations)

        val btnBackMesReservations = findViewById<Button>(R.id.btnBackMesReservations);
        btnBackMesReservations.setOnClickListener{
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
        GlobalScope.launch(Dispatchers.Main) {
            retrieveProprioReservation()
        }
    }

    private suspend fun retrieveProprioReservation(){
        return withContext(Dispatchers.IO) {
            try{
                val token = gestionToken.getToken()
                val url = URL("https://api.friedrichalyssa.com/?action=getProprioReserv")
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
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(response)
                    val containerReservations = findViewById<LinearLayout>(R.id.reservationsContainer)
                    val inflater = LayoutInflater.from(this@MesReservationsActivity)
                    for (i in 0 until jsonArray.length()) {
                        val reservationProprio = jsonArray.getJSONObject(i)
                        val reservationId = reservationProprio.getInt("id")
                        val rue = reservationProprio.getString("rue")
                        val cp = reservationProprio.getString("cp")
                        val nomLocataire = reservationProprio.getString("nom_locataire")
                        val prenomLocataire = reservationProprio.getString("prenom_locataire")
                        val dateDebut = reservationProprio.getString("dateDebut")
                        val dateFin = reservationProprio.getString("dateFin")

                        runOnUiThread{

                            val reservationView = inflater.inflate(R.layout.mr_reservations_card, null)
                            reservationView.findViewById<TextView>(R.id.titleCardMR).text = "Réservation #$reservationId - $rue"
                            reservationView.findViewById<TextView>(R.id.dateReservationMR).text = "$dateDebut au $dateFin"
                            reservationView.findViewById<TextView>(R.id.txtCodePostalMR).text = cp
                            reservationView.findViewById<TextView>(R.id.txtRueMR).text = rue
                            reservationView.findViewById<TextView>(R.id.nomClientMR).text = "$nomLocataire $prenomLocataire"

                            containerReservations.addView(reservationView)
                        }
                    }
                } else {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val contentReponse = JSONObject(response)
                    println(contentReponse)
                }
            } catch (e: Exception) {
                println("Erreur lors de la récupération des réservations: ${e.message}")
            }
        }
    }
}