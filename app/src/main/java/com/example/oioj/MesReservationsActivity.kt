package com.example.oioj

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button


class MesReservationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mr_mes_reservations)

        val btnBackMesReservations = findViewById<Button>(R.id.btnBackMesReservations);
        btnBackMesReservations.setOnClickListener{
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
    }
}