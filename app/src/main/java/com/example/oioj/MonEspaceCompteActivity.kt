package com.example.oioj
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MonEspaceCompteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.es_espace_compte)
        val btnBackMonEspaceCompte = findViewById<Button>(R.id.btnBackMonEspaceCompte)
        btnBackMonEspaceCompte.setOnClickListener{
            val redirection = Intent(this, DashboardActivity::class.java)
            startActivity(redirection)
        }
        //TU PEUX RECUPERER CA////TU PEUX RECUPERER CA////TU PEUX RECUPERER CA////TU PEUX RECUPERER CA////TU PEUX RECUPERER CA//

        val inputMdp1 = findViewById<EditText>(R.id.editTextPassword1)
        val inputMdp2 = findViewById<EditText>(R.id.editTextPassword2)
        val inputNom = findViewById<EditText>(R.id.editNom)
        val inputMail = findViewById<EditText>(R.id.editTextMail)
        //////////////////////////////////


        //Bouton Onclick function confirmation
        val btnModifierNom = findViewById<Button>(R.id.btnModifierNom)
        btnModifierNom.setOnClickListener {
            confirmationNom();
        }
        val btnModifierMail = findViewById<Button>(R.id.btnModifierMail)
        btnModifierMail.setOnClickListener {
            confirmationMail();
        }
        val btnModifierPassword = findViewById<Button>(R.id.btnModifierPass)
        btnModifierPassword.setOnClickListener {
            confirmationPass();
        }


        //Redirection autres pages
        val btnPolitique = findViewById<Button>(R.id.btnPolitiqueConfidentialite)
        btnPolitique.setOnClickListener {
            val redirection = Intent(this, MECPolitiqueActivity::class.java)
            startActivity(redirection)
        }
        val btnMention = findViewById<Button>(R.id.btnMentionLegale)
        btnMention.setOnClickListener {
            val redirection = Intent(this, MECMentionActivity::class.java)
            startActivity(redirection)
        }
        val btnCondition = findViewById<Button>(R.id.btnConditionsUtilisation)
        btnCondition.setOnClickListener {
            val redirection = Intent(this, MECConditionActivity::class.java)
            startActivity(redirection)
        }

        //Fonction Charger Infos
        GlobalScope.launch (Dispatchers.IO) {
            chargerInfos()
        }
    }


    private suspend fun chargerInfos() {
        return withContext(Dispatchers.IO) {
            try {
                //récupération des informations du compte
                val token = gestionToken.getToken()
                val url = URL("https://api.immomvc.varin.ovh/?action=getAccountInfos") //////////////////////////// Mettre une action ZEBI sinon sa marchera pas connasse
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json")

                // Envoi du token
                val jsonObject = JSONObject().apply {
                    put("token", token)
                }
                val outputStream = httpURLConnection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.close()
                val responseCode = httpURLConnection.responseCode
                println("Récupération des informations du compte : $responseCode")
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val reponse = inputStream.bufferedReader().use { it.readText() }
                    println("Les données récupérées : \n $reponse")
                    val leCompte = JSONObject(reponse)
                    withContext(Dispatchers.Main) {
                        /*
                        val blocChamps = findViewById<ConstraintLayout>(R.id.leMenu)
                        val lyo = LayoutInflater.from(this@MonEspaceCompteActivity).inflate(R.layout.es_card_infpers, blocChamps, false)
                        lyo.findViewById<EditText>(R.id.editEmailUtilisateurEspaceCompte).setText(leCompte.getString("login"))
                        blocChamps.addView(lyo)*/
                    }

                }
            } catch (e: Exception) {
                println("Erreur lors de la récupération des informations :  ${e.message}" +
                        "${e.printStackTrace()}")
            }
        }
    }

    private fun confirmationNom(){
        val popup = AlertDialog.Builder(this)
        popup.setMessage("Êtes-vous sûr de vouloir modifier votre nom de famille?").setPositiveButton("Confirmer", DialogInterface.OnClickListener { dialog, id ->
            // ici ta logique pour modifier le nom de famille
        }).setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, id ->
            dialog.dismiss()
        })
        popup.create().show()
    }
    private fun confirmationMail(){
        val popup = AlertDialog.Builder(this)
        popup.setMessage("Êtes-vous sûr de vouloir modifier votre adresse email?").setPositiveButton("Confirmer", DialogInterface.OnClickListener { dialog, id ->
            // ici ta logique pour modifier le mail
        }).setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, id ->
            dialog.dismiss()
        })
        popup.create().show()
    }
    private fun confirmationPass(){
        val inputMdp1 = findViewById<EditText>(R.id.editTextPassword1)
        val inputMdp2 = findViewById<EditText>(R.id.editTextPassword2)
        val nouveaumdp1 = inputMdp1.text.toString()
        val nouveaumdp2 = inputMdp2.text.toString()

        if (nouveaumdp1.isEmpty() || nouveaumdp2.isEmpty()){
            val popupErreur = AlertDialog.Builder(this)
            popupErreur.setMessage("Vous devez remplir tous les champs de mot de passe ! ").setPositiveButton("Ok"){
                dialog, _ -> dialog.dismiss()
            }
            popupErreur.create().show()
        } else {
            val popup = AlertDialog.Builder(this)
            popup.setMessage("Êtes-vous sûr de vouloir modifier votre mot de passe?")
                .setPositiveButton("Confirmer", DialogInterface.OnClickListener { dialog, id ->
                    // ici ta logique pour modifier le mot de passe
                }).setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
            popup.create().show()
        }
    }

}