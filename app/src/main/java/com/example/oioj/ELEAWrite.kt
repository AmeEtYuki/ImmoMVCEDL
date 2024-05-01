package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class ELEAWrite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wele_write_etat_lieux_entree)

        val btnBackMesReservations = findViewById<Button>(R.id.btnBackMesLogementsEntree);
        btnBackMesReservations.setOnClickListener {
            onBackPressed()
        }

        // Ici on récupère le logement ID qu'on a envoyer sur la page ETAT LIEUX ENTREE ACTIVITY
        val idReservation = intent.getIntExtra("reservation_id",-1)
        val idBien = intent.getIntExtra("bien_id", -1)

        GlobalScope.launch(Dispatchers.Main){
            retrievePieces(idBien,idReservation);
        }

        val buttonValidate = findViewById<Button>(R.id.buttonValidateWriteEtatLieuxEntree)
        buttonValidate.setOnClickListener {
            val editTextWriteEtatLieuxEntree =
                findViewById<EditText>(R.id.editTextWriteEtatLieuxEntree)
            val userText = editTextWriteEtatLieuxEntree.text.toString()
            println("Texte saisi par l'utilisateur : $userText")
            GlobalScope.launch(Dispatchers.IO) {
                insertEDLGlobalDuLogement(idReservation, userText)
            }
        }


    }

    private suspend fun insertEDLGlobalDuLogement(idReservation: Int, userText: String){
        return withContext(Dispatchers.IO){
            try{
                val token = gestionToken.getToken()
                val url = URL("http://api.immomvc.varin.ovh/?action=InsertEDLentree") //////////////////////////// Mettre une action ZEBI sinon sa marchera pas connasse
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")

                val jsonObject = JSONObject().apply {
                    put("token", token)
                    put("idReservation", idReservation)
                    put("commentaire", userText)
                }
                println(jsonObject)
                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()
                val responseCode = httpURLConnection.responseCode
                println("Response Code: $responseCode")
            }catch (e: Exception) {
                println("Erreur lors de l'insertion de l'état des lieux:  ${e.message}")
            }
        }
    }

    private suspend fun retrievePieces(idBien: Int, idReservation: Int){
        return withContext(Dispatchers.IO){
            try{

                val token = gestionToken.getToken()
                val url = URL("http://api.immomvc.varin.ovh/?action=piece")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                val jsonObject = JSONObject().apply {
                    put("token", token)
                    put("idBien", idBien)
                }
                println("logement id = $idBien")
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
                    println(" la reponse elea write$response")

                    val containerPieces = findViewById<LinearLayout>(R.id.roomContainer)
                    for (i in 0 until jsonArray.length()) {
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

                        runOnUiThread{
                            val pieceLayout = LayoutInflater.from(this@ELEAWrite).inflate(R.layout.wele_card_listepiece, containerPieces, false)
                            val txtNomPiece = pieceLayout.findViewById<TextView>(R.id.txtNomPieceWele)
                            val surfacePiece = pieceLayout.findViewById<TextView>(R.id.surfacePieceWele)
                            txtNomPiece.text = libelle
                            surfacePiece.text = surface.toString()+"m²"
                            containerPieces.addView(pieceLayout)

                            val buttonPiece = findViewById<Button>(R.id.selectPieceWele)
                            val buttonId = "selectPieceWele_$id"
                            buttonPiece.id = View.generateViewId()
                            buttonPiece.tag = id

                            buttonPiece.setOnClickListener {
                                val pieceId = it.tag as Int
                                val intent = Intent(this@ELEAWrite, ELEAWriteDetailsPieces::class.java)
                                intent.putExtra("piece_id", pieceId)
                                intent.putExtra("idReservation", idReservation)
                                intent.putExtra("idBien", idBien)
                                intent.putExtra("libelle", libelle)
                                startActivity(intent)
                            }
                        }
                    }

                } else {
                    val inputStream = httpURLConnection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val contentReponse = JSONObject(response)
                    println(contentReponse)
                }
            } catch (e: Exception) {
                println("Erreur lors de la récupération des biens: ${e.message}")
            }
        }
    }

}