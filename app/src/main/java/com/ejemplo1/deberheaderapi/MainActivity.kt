package com.ejemplo1.deberheaderapi

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), Response.Listener<JSONObject>, Response.ErrorListener {
    var editNombre: TextView? = null
    var editGenero: TextView? = null
    var editCorreo: TextView? = null

    var textResponse: TextView? = null

    var progreso: ProgressDialog? = null

    var request: RequestQueue? = null
    var jsonObjectRequest: JsonObjectRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editNombre = findViewById<View>(R.id.TxtNombre) as EditText
        editGenero = findViewById<View>(R.id.TxtGenero) as EditText
        editCorreo = findViewById<View>(R.id.TxtCorreo) as EditText

        textResponse = findViewById<View>(R.id.textResponse) as TextView
        val btnSend: Button = findViewById<View>(R.id.btnSend) as Button
        request = Volley.newRequestQueue(this)

        btnSend.setOnClickListener{
            cargarAPI()
        }
    }
    private fun cargarAPI() {
        val url = "https://gorest.co.in/public/v1/users"
        val usuario = JSONObject()
        val Nombre = editNombre!!.text.toString()
        val genero = editGenero!!.text.toString()
        val correo = editCorreo!!.text.toString()
        if (Nombre.length < 1) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_LONG).show()
            return
        } else if (genero.length < 1) {
            Toast.makeText(this, "Debes ingresar un genero (male/female)", Toast.LENGTH_LONG).show()
            return
        } else if (correo.length < 1) {
            Toast.makeText(this, "Debes ingresar un correo", Toast.LENGTH_LONG).show()
            return
        }
        progreso = ProgressDialog(this)
        progreso!!.setTitle("Comunicando con la API...")
        progreso!!.show()
        try {
            usuario.put("name", Nombre)
            usuario.put("gender", genero)
            usuario.put("email", correo)
            usuario.put("status", "active")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonObjectRequest =
            object : JsonObjectRequest(Request.Method.POST, url, usuario, this, this) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] =
                        "Bearer " + "3a5a4b6da0ecaa3e51d52cefe1e424aa67b819260613cc21010af305fab71cfa"
                    //params.put("Accept-Language", "fr");
                    return params
                }
            }
        request!!.add(jsonObjectRequest)
    }
    override fun onErrorResponse(error: VolleyError) {
        progreso!!.hide()
        Toast.makeText(this, "Ha existido un error", Toast.LENGTH_LONG).show()
        textResponse!!.text = error.toString()
    }
    override fun onResponse(response: JSONObject) {
        progreso!!.hide()
        Toast.makeText(this, "Se a registrado correctamente", Toast.LENGTH_LONG).show()
        var data: JSONObject? = null
        try {
            data = response.getJSONObject("data")
            textResponse!!.text = """
            El usuario se a creado correctamente: 
            id: ${data.getString("id")}
            Nombre: ${data.getString("name")}
            Correo: ${data.getString("email")}
            """.trimIndent()
            println(data.toString())
        } catch (e: JSONException) {
            Toast.makeText(this, "Error al presentar los datos", Toast.LENGTH_LONG).show()
        }
        editNombre!!.text = ""
        editGenero!!.text = ""
        editCorreo!!.text = ""
    }

}