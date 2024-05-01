package com.example.oioj
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button

class MonEspaceCompteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.es_espace_compte)

        val btnBackMonEspaceCompte = findViewById<Button>(R.id.btnBackMonEspaceCompte)
        btnBackMonEspaceCompte.setOnClickListener{
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }

    }

}