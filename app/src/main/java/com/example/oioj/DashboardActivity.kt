package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        val token = intent.getStringExtra("apiReponse")
        if (token != null){
            println("Contenu du token: $token")
            val jsonObject = JSONObject(token)
            val nomValue = jsonObject.getString("nom")
            val prenomValue = jsonObject.getString("prenom")
            if (nomValue == null && prenomValue == null){
                val nomValue = intent.getStringExtra("nom")
                val prenomValue = intent.getStringExtra("prenom")
            }
            println("$nomValue $prenomValue")
            val inputUsername = findViewById<TextView>(R.id.txtNomUtilisateur)
            inputUsername.text = "$nomValue $prenomValue"

            val tokenValue = jsonObject.getString("token")
            gestionToken.setToken(tokenValue)

        } else {
            println("token nul")
        }
        val jsonObject = JSONObject(token)
        val nomValue = jsonObject.getString("nom")
        val prenomValue = jsonObject.getString("prenom")

        val btnBackDash = findViewById<Button>(R.id.btnBackDash)
        btnBackDash.setOnClickListener {
            val redirection = Intent(this, MainActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
        val btnEntree = findViewById<Button>(R.id.btnEntree)
        btnEntree.setOnClickListener {
            val redirection = Intent(this, EtatLieuxEntreeActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
        val btnSortie = findViewById<Button>(R.id.btnSortie)
        btnSortie.setOnClickListener {
            val redirection = Intent(this, EtatLieuxSortieActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
        val btnVoirLogements = findViewById<Button>(R.id.btnVoirLogements)
        btnVoirLogements.setOnClickListener {
            val redirection = Intent(this,MesLogementsActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
        val btnVoirReservations = findViewById<Button>(R.id.btnVoirReservations)
        btnVoirReservations.setOnClickListener {
            val redirection = Intent(this, MesReservationsActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
        val btnVoirCompte = findViewById<Button>(R.id.btnMonCompte)
        btnVoirCompte.setOnClickListener {
            val redirection = Intent(this, MonEspaceCompteActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
        val btnVoirEtatLieux = findViewById<Button>(R.id.btnVoirEtatsLieux)
        btnVoirEtatLieux.setOnClickListener {
            val redirection = Intent(this, MesEtatLieuxActivity::class.java)
            redirection.putExtra("nom", nomValue)
            redirection.putExtra("prenom", prenomValue)
            startActivity(redirection)
        }
    }
}

