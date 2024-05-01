package com.example.oioj

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wele_write_etat_lieux_piece)
        //Recover le piece_id qui nous permettra d'afficher les equipements
        val idPiece = intent.getIntExtra("piece_id",-1)
        println(idPiece);
        //Bouton Back, redirection page precedente
        val btnBackMesReservations = findViewById<Button>(R.id.btnBackMesLogements)
        btnBackMesReservations.setOnClickListener {
            onBackPressed()
        }

        //Bouton afin de valider l'état des lieux de la pièce.
        val buttonValidate = findViewById<Button>(R.id.buttonValidateWriteEtatLieuxEntree)

        GlobalScope.launch(Dispatchers.IO){
            retrieveEquipement(idPiece)
        }
    }

    private suspend fun retrieveEquipement(idPiece: Int){
        try{
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
                for (i in 0 until jsonArray.length()) {
                    val equipement = jsonArray.getJSONObject(i)
                    val id = equipement.getInt("id")
                    val libelle = equipement.getString("libelle")
                    println("Equipement $i :")
                    println("   ID : $id")
                    println("   Libellé : $libelle")

                    runOnUiThread{
                        val equipementLayout = LayoutInflater.from(this@ELEAWriteDetailsPieces).inflate(R.layout.wele_card_equipement, containerEquipements, false)
                        val txtTitleEquipement = equipementLayout.findViewById<TextView>(R.id.titleEtatEquipement)
                        txtTitleEquipement.text = "$libelle"

                        val buttonGroup = equipementLayout.findViewById<RadioGroup>(R.id.buttonGroup)
                        val buttonMauvais = equipementLayout.findViewById<RadioButton>(R.id.weleCEbadButton)
                        val buttonMoyen = equipementLayout.findViewById<RadioButton>(R.id.weleCEmoyenButton)
                        val buttonBon = equipementLayout.findViewById<RadioButton>(R.id.weleCEbonButton)
                        var noteValue = 0

                        buttonGroup.setOnCheckedChangeListener { group, checkedId ->
                            noteValue = when(checkedId){
                                R.id.weleCEbadButton -> 3
                                R.id.weleCEmoyenButton -> 2
                                R.id.weleCEbonButton -> 1
                                else -> 0
                            }
                            println("note saisie : $noteValue")
                        }

                        buttonMauvais.id = View.generateViewId()
                        buttonMoyen.id = View.generateViewId()
                        buttonBon.id = View.generateViewId()


                        containerEquipements.addView(equipementLayout)
                    }
                }
            } else  {
                val errorStream = httpURLConnection.errorStream
                val errorResponse = errorStream.bufferedReader().use { it.readText() }
                println("Erreur lors de la requête : $errorResponse")
                println("Code de réponse : $responseCode")

            }
        }catch (e: Exception) {
            println("Erreur lors de l'insertion de l'état des lieux (partie pièce equipement):")
            e.printStackTrace()
        }
    }




}