package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ELESWrite : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //état des lieux - sortie - rédaction EDL logement
        setContentView(R.layout.wele_write_etat_lieux_entree)
        // Ici on récupère le logement ID qu'on a envoyer sur la page ETAT LIEUX ENTREE ACTIVITY
        val idReservation = intent.getIntExtra("reservation_id",-1)
        val idBien = intent.getIntExtra("bien_id", -1)
        GlobalScope.launch(Dispatchers.Main){
            retrievePieces(idBien,idReservation)
        }
        val nomValue = intent.getStringExtra("nom")
        val prenomValue = intent.getStringExtra("prenom")
        val token = gestionToken.getToken()
        val buttonValidate = findViewById<Button>(R.id.buttonValidateWriteEtatLieuxEntree)
        buttonValidate.setOnClickListener {
            val editTextWriteEtatLieuxEntree =
                findViewById<EditText>(R.id.editTextWriteEtatLieuxEntree)
            val userText = editTextWriteEtatLieuxEntree.text.toString()
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            builder.setTitle("Confirmation")
            builder.setMessage("Etat des lieux de la pièces réalisé avec succès ! ")
            builder.setPositiveButton("OK") { dialog, which ->
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        insertEDLGlobalDuLogement(idReservation, userText)
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@ELESWrite, EtatLieuxSortieActivity::class.java)
                            val jsonObject = JSONObject().apply {
                                put("token", token)

                            }
                            val jsonString = jsonObject.toString()
                            intent.putExtra("nom", nomValue)
                            intent.putExtra("prenom", prenomValue)
                            intent.putExtra("apiReponse", jsonString)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        println("Erreur lors de l'insertion de l'état des lieux: ${e.message}")
                    }
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Annuler") { dialog, which ->
                dialog.dismiss()
            }
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //Toast.makeText(this@ELESWriteDetailsPieces, "", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ELESWrite, EtatLieuxSortieActivity::class.java)
                    intent.putExtra("nom", nomValue)
                    intent.putExtra("prenom", prenomValue)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    Toast.makeText(this@ELESWrite, "Bouton retour appuyé, returs vers liste des EDL", Toast.LENGTH_SHORT).show()
                    // Si vous voulez le comportement par défaut du bouton retour, désactivez le callback temporairement et appelez super.onBackPressed()
                    // this.remove()
                    // onBackPressedDispatcher.onBackPressed()
                }
            })
            val dialog = builder.create()
            dialog.show()
        }
    }

    private suspend fun insertEDLGlobalDuLogement(idReservation: Int, userText: String) {
        return withContext(Dispatchers.IO){
            try{
                val token = gestionToken.getToken()
                val url = URL("https://api.immoMVC.varin.ovh/?action=InsertEDLsortie") //////////////////////////// Mettre une action ZEBI sinon sa marchera pas connasse
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
    private suspend fun marqueurPiece(idReservation: Int, piecesID: List<Int>){
        return withContext(Dispatchers.IO){
            println("ici")
            try{
                val token = gestionToken.getToken()
                val url = URL("http://api.immoMVC.varin.ovh/?action=marqueurEDLPieceSortie")
                piecesID.forEach {idPiece ->
                    val jsonObject = JSONObject().apply {
                        put("token", token)
                        put("idReservation", idReservation)
                        put("idPiece", idPiece)
                    }
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "POST"
                    httpURLConnection.setRequestProperty("Content-Type", "application/json")
                    println("la")
                    println(piecesID)
                    println("MARQUEUR : $jsonObject ")
                    val outputStream = httpURLConnection.outputStream
                    outputStream.write(jsonObject.toString().toByteArray())
                    outputStream.close()
                    val responseCode = httpURLConnection.responseCode
                    println("Response Code de MARQUEUR: $responseCode")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = httpURLConnection.inputStream
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val responseInt = response.trim().toInt()
                        runOnUiThread {
                            val containerPieces = findViewById<LinearLayout>(R.id.roomContainer)
                            val pieceLayout = containerPieces.findViewById<View>(idPiece)
                            val textViewSituationPiece = pieceLayout?.findViewWithTag<TextView>("situationPieceWele$idPiece")
                            if (textViewSituationPiece != null) {
                                if (responseInt == 1) {
                                    textViewSituationPiece.text = "État des lieux fait"
                                } else {
                                    textViewSituationPiece.text = "État des lieux non fait"
                                }
                            } else {
                                println("TextView not found for pieceId: $idPiece")
                            }
                        }
                        println("Response de la requête : $response")
                    } else {
                        println("Erreur lors de la récupération des pièces avec EDL déjà fait: ${httpURLConnection.responseMessage}")
                    }
                }
            }catch (e: Exception) {
                println("Erreur lors de la récupération des pièces avec EDL déjà  fait:  ${e.message}")
            }
        }}
    private suspend fun retrievePieces(idBien: Int, idReservation: Int) {
        //Doit charger : la liste des pièces d'un bien choisis
        return withContext(Dispatchers.IO) {
            try {

                val token = gestionToken.getToken();

                val url = URL("https://api.immoMVC.varin.ovh/?action=piece")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")

                val jsonObject = JSONObject().apply {
                    put("token", token)
                    put("idBien", idBien)
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
                    val containerPieces = findViewById<LinearLayout>(R.id.roomContainer)

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

                        runOnUiThread{
                            val pieceLayout = LayoutInflater.from(this@ELESWrite).inflate(R.layout.wele_card_listepiece, containerPieces, false)
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
                                val intent = Intent(this@ELESWrite, ELESWriteDetailsPieces::class.java)
                                intent.putExtra("piece_id", pieceId)
                                intent.putExtra("idReservation", idReservation)
                                intent.putExtra("idBien", idBien)

                                startActivity(intent)
                            }
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