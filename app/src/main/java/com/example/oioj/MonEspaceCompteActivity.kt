package com.example.oioj
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MonEspaceCompteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.es_espace_compte)

        val btnBackMonEspaceCompte = findViewById<Button>(R.id.btnBackMonEspaceCompte)
        btnBackMonEspaceCompte.setOnClickListener{
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
        //interface
        val blocChamps = findViewById<ConstraintLayout>(R.id.leMenu)
        val lyo = LayoutInflater.from(this@MonEspaceCompteActivity).inflate(R.layout.es_card_infpers, blocChamps, false)
        blocChamps.addView(lyo)
        GlobalScope.launch {

        }
    }
    private suspend fun chargerInfos() {
        return withContext(Dispatchers.IO) {
            try {
                //récupération des informations 
            } catch (e: Exception) {
                println("Erreur lors de l'insertion de l'état des lieux:  ${e.message}")
            }
        }
    }

}