package com.example.oioj

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MesEtatLieuxActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mes_mes_etats_lieux)

        val btnBackMesEtatLieux = findViewById<Button>(R.id.btnBackMesEtatLieux)
        btnBackMesEtatLieux.setOnClickListener {
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
    }
}