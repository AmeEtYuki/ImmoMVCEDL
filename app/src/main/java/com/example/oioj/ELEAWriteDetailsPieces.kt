package com.example.oioj

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ELEAWriteDetailsPieces : AppCompatActivity() {
    val notesEquipements = HashMap<Int, Int>()
    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wele_write_etat_lieux_piece)

        //Bouton Back, redirection page precedente
        val btnBackMesReservations = findViewById<Button>(R.id.btnBackMesLogements)
        btnBackMesReservations.setOnClickListener {
            onBackPressed()
        }

        //Bouton afin de valider l'état des lieux de la pièce.
        val buttonValidate = findViewById<Button>(R.id.buttonValidateWriteEtatLieuxEntree)

        //Affichage Equipement
        val idReservation = intent.getIntExtra("idReservation", -1)
        val idPiece = intent.getIntExtra("piece_id",-1)
        GlobalScope.launch(Dispatchers.IO){
            retrieveEquipement(idPiece)
        }

        // Bouton Photo
        val buttonAddPhoto = findViewById<Button>(R.id.buttonAddPhoto)
        buttonAddPhoto.setOnClickListener {
            selectImage()
        }
        // valider
        buttonValidate.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                insertEDLDetailsEquipement(idReservation, idPiece)
            }
        }

    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    private fun getFileName(uri: Uri): String {
        var result = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = it.getString(displayName)
            }
        }
        cursor?.close()
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                val imageName = getFileName(it)
                println("Nom de l'image sélectionnée : $imageName")
                findViewById<ImageView>(R.id.imageView).setImageURI(uri)

            }
        }
    }


    private suspend fun retrieveEquipement(idPiece: Int) {
        try {
            val token = gestionToken.getToken()
            val url = URL("http://api.immomvc.varin.ovh/?action=recoverEquipementPiece")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val jsonObject = JSONObject().apply {
                put("token", token)
                put("idPiece", idPiece)
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
                println(" la reponse elea write$response")

                val containerEquipements = findViewById<LinearLayout>(R.id.equipementContainer)
                for (i in 0 until jsonArrayLength) {
                    val equipement = jsonArray.getJSONObject(i)
                    val id = equipement.getInt("id")
                    val libelle = equipement.getString("libelle")
                    println("Equipement $i :")
                    println("   ID : $id")
                    println("   Libellé : $libelle")

                    runOnUiThread {

                        val libellePiece = intent.getStringExtra("libelle")
                        val textViewCommentaire = findViewById<TextView>(R.id.txtTitreWriteEtatLieuxEntree);
                        textViewCommentaire.text = "Pièce en cours : $libellePiece"

                        val equipementLayout = LayoutInflater.from(this@ELEAWriteDetailsPieces).inflate(R.layout.wele_card_equipement, containerEquipements, false)
                        val txtTitleEquipement = equipementLayout.findViewById<TextView>(R.id.titleEtatEquipement)
                        txtTitleEquipement.text = libelle

                        val buttonGroup = equipementLayout.findViewById<RadioGroup>(R.id.buttonGroup)
                        val buttonMauvais = equipementLayout.findViewById<RadioButton>(R.id.weleCEbadButton)
                        val buttonMoyen = equipementLayout.findViewById<RadioButton>(R.id.weleCEmoyenButton)
                        val buttonBon = equipementLayout.findViewById<RadioButton>(R.id.weleCEbonButton)

                        buttonGroup.tag = id

                        buttonMauvais.tag = 3
                        buttonMoyen.tag = 2
                        buttonBon.tag = 1

                        buttonGroup.removeView(buttonMauvais)
                        buttonGroup.removeView(buttonMoyen)
                        buttonGroup.removeView(buttonBon)

                        buttonMauvais.id = View.generateViewId()
                        buttonMoyen.id = View.generateViewId()
                        buttonBon.id = View.generateViewId()

                        buttonGroup.addView(buttonMauvais)
                        buttonGroup.addView(buttonMoyen)
                        buttonGroup.addView(buttonBon)

                        buttonGroup.setOnCheckedChangeListener { group, checkedId ->
                            val selectedButton = findViewById<RadioButton>(checkedId)
                            val noteValue = selectedButton.tag as Int
                            val equipmentId = group.tag as Int
                            println("User clicked on button for equipment $equipmentId with note $noteValue")
                            notesEquipements[equipmentId] = noteValue
                        }
                        containerEquipements.addView(equipementLayout)
                    }
                }
            } else {
                val errorStream = httpURLConnection.errorStream
                val errorResponse = errorStream.bufferedReader().use { it.readText() }
                println("Erreur lors de la requête : $errorResponse")
                println("Code de réponse : $responseCode")
            }
        } catch (e: Exception) {
            println("Erreur lors de la récupération (partie pièce equipement):")
            e.printStackTrace()
        }
    }

    private suspend fun insertEDLDetailsEquipement(idReservation: Int, idPiece: Int) {
        for ((idEquipement, note) in notesEquipements) {
            try {
                val token = gestionToken.getToken()
                val url =
                    URL("http://api.immomvc.varin.ovh/?action=writeEDLEquipementPieceEquipement")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")

                val jsonObject = JSONObject().apply {
                    put("token", token)
                    put("idPiece", idPiece)
                    put("idReservation", idReservation)
                    put("idEquipement", idEquipement)
                    put("note", note)
                }
                val outputStream = httpURLConnection.outputStream
                println(jsonObject)
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()

                val responseCode = httpURLConnection.responseCode
                println("Response Code: $responseCode")

            } catch (e: Exception) {
                println("Erreur lors de l'insertion de l'état des lieux (partie pièce equipement):")
                e.printStackTrace()
            }
        }
    }


}




