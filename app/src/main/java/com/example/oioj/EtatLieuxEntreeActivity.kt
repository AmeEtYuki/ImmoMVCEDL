package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class EtatLieuxEntreeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val nomValue = intent.getStringExtra("nom")
        val prenomValue = intent.getStringExtra("prenom")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wele_etat_lieux_entree)
        val token = intent.getStringExtra("apiReponse")
        // Récupération du bouton de retour et déclaration du listener

        // Appel de la fonction pour récupérer cles réservations
        GlobalScope.launch(Dispatchers.Main) {
            retrieveReservations()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Toast.makeText(this@ELESWriteDetailsPieces, "", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EtatLieuxEntreeActivity, DashboardActivity::class.java)
                intent.putExtra("nom", nomValue)
                intent.putExtra("prenom", prenomValue)
                intent.putExtra("token", token)
                startActivity(intent)
                Toast.makeText(this@EtatLieuxEntreeActivity, "Bouton retour appuyé, returs vers MainActivity", Toast.LENGTH_SHORT).show()
                // Si vous voulez le comportement par défaut du bouton retour, désactivez le callback temporairement et appelez super.onBackPressed()
                // this.remove()
                // onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    private suspend fun retrieveReservations() {
        return withContext(Dispatchers.IO) {
            try {
                // Récupération du token
                val token = gestionToken.getToken()
                println("Le token retrouvé page Rédigé état des lieux est : $token")

                // Connexion à l'API pour récupérer les réservations
                val url = URL("https://api.friedrichalyssa.com/?action=writeEDLentree")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")

                // Envoi du token
                val jsonObject = JSONObject().apply {
                    put("token", token)
                }
                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()

                // Récupération de la réponse de l'API
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
                    val inflater = LayoutInflater.from(this@EtatLieuxEntreeActivity)
                    for (i in 0 until jsonArray.length()) {
                        val reservation = jsonArray.getJSONObject(i)
                        val id = reservation.getInt("id")
                        val dateDebut = reservation.getString("dateDebut")
                        val dateFin = reservation.getString("dateFin")
                        val rue = reservation.getString("rue")
                        val idBien = reservation.getInt("idBien")

                        runOnUiThread {

                            val reservationLayout = LayoutInflater.from(this@EtatLieuxEntreeActivity)
                                .inflate(R.layout.wele_card_pick_card, containerReservations, false)

                            // Récupérer les TextViews et Button de wele_card_pick_card.xml
                            val txtNomLogement = reservationLayout.findViewById<TextView>(R.id.txtNomLogementpc)
                            val dateReservationLogement = reservationLayout.findViewById<TextView>(R.id.dateReservationLogement)
                            val txtRue = reservationLayout.findViewById<TextView>(R.id.txtRue)

                            val nomValue = intent.getStringExtra("nom")
                            val prenomValue = intent.getStringExtra("prenom")
                            val btnEtatLieux = reservationLayout.findViewById<Button>(R.id.supabutton)
                            btnEtatLieux.setOnClickListener{
                                val intent = Intent(this@EtatLieuxEntreeActivity, ELEAWrite::class.java)
                                intent.putExtra("reservation_id", id)
                                intent.putExtra("bien_id", idBien)
                                intent.putExtra("nom", nomValue)
                                intent.putExtra("prenom", prenomValue)
                                startActivity(intent)
                            }

                            txtNomLogement.text = "Logement $idBien"
                            dateReservationLogement.text = "$dateDebut au $dateFin"
                            txtRue.text = "$rue"
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
                println("Erreur lors de la récupération des réservations: ${e.message}")
            }
        }
    }
}