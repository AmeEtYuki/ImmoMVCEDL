package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ELESWrite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.wels_write_etat_lieux_sortie)
        val btnBackMesReservations = findViewById<Button>(R.id.btnBackMesLogements)

        btnBackMesReservations.setOnClickListener{
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
    }

    private suspend fun load() {
        //Doit charger : la liste des pièces d'un bien choisis
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
                    println("longueur json : $jsonArrayLength")

                    //scrollViewRooms -> affichage des pièces *
                    val pieces = findViewById<ScrollView>(R.id.scrollViewRooms)

                    for(i in 0 until jsonArrayLength) {
                        val piece = jsonArray.getJSONObject(i)
                        val id = piece.getInt("id")
                        val surface = piece.getDouble("surface")
                        val libelle = piece.getString("libelle")
                        val id_bien = piece.getInt("id_bien")
                        val i = i + 1
                        println("Pièce $i :")
                        println("   ID : $id")
                        println("   Surface : $surface")
                        println("   Libellé : $libelle")
                        println("   ID du bien : $id_bien")

                        //récupération de la carte

                        runOnUiThread {
                            val pieceLy = LayoutInflater.from(this@ELESWrite).inflate(R.layout.wele_card_listepiece, pieces, false)
                            val buttonPiece = findViewById<Button>(R.id.selectPieceWele)
                            val buttonId = "selectPieceWele_$id"
                            buttonPiece.id = View.generateViewId()
                            buttonPiece.tag = id
                            //val btnEtatLieux = pieceLy.findViewById<Button>(R.id.)
                            pieces.addView(pieceLy)

                        }
                    }

                } else {

                }





            } catch (e: Exception) {
                println("${e.message}" +
                        "${e.printStackTrace()}")
            }
        }
    }
}