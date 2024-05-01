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

class EtatLieuxSortieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wels_etat_lieux_sortie)

        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            onBackPressed()
        }
        GlobalScope.launch {
            retrieveReservations()
        }
    }
    private suspend fun retrieveReservations() {
        return withContext(Dispatchers.IO) {
            try {

                val token = gestionToken.getToken();

                val url = URL("https://api.immomvc.varin.ovh/?action=getELDsortie")
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
                    val jsonArrayLength = jsonArray.length()
                    println("Longueur du tableau JSON : $jsonArrayLength")
                    println(" la reponse elea $response")
                    val containerReservations = findViewById<LinearLayout>(R.id.containerReservations)
                    val inflater = LayoutInflater.from(this@EtatLieuxSortieActivity)

                    for (i in 0 until jsonArray.length()) {
                        val reservation = jsonArray.getJSONObject(i)
                        val id = reservation.getInt("id")
                        val id_periodeDispo = reservation.getInt("id_periodeDispo")
                        val dateDebut = reservation.getString("dateDebut")
                        val dateFin = reservation.getString("dateFin")
                        val valide = reservation.getInt("valide") == 1
                        val id_locataire = reservation.getInt("id_locataire")
                        println(id)
                        //
                        println("Réservation $i :")
                        println("   ID : $id")
                        println("   ID période de disponibilité : $id_periodeDispo")
                        println("   Date de début : $dateDebut")
                        println("   Date de fin : $dateFin")
                        println("   Valide : $valide")
                        println("   ID locataire : $id_locataire")

                        runOnUiThread {
                            println()
                            val reservationLayout = LayoutInflater.from(this@EtatLieuxSortieActivity)
                                .inflate(R.layout.wels_card_pick_card, containerReservations, false)

                            // Récupérer les TextViews et Button de wele_card_pick_card.xml
                            val txtNomLogement = reservationLayout.findViewById<TextView>(R.id.txtNomLogementpc)
                            val dateReservationLogement = reservationLayout.findViewById<TextView>(R.id.dateReservationLogement)

                            val btnEtatLieux = reservationLayout.findViewById<Button>(R.id.supabutton)

                            btnEtatLieux.setOnClickListener{
                                val intent = Intent(this@EtatLieuxSortieActivity, ELESWrite::class.java)
                                intent.putExtra("logement_id", id)
                                startActivity(intent)
                            }

                            txtNomLogement.text = "Logement $id"
                            dateReservationLogement.text = "$dateDebut au $dateFin"
                            containerReservations.addView(reservationLayout)
                        }
                    }

                } else {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val contentReponse = JSONObject(response)
                    println(contentReponse)
                }
            } catch (e: Exception) {
                println("Err retreive reservs : ${e.message}")
            }
        }
    }
}