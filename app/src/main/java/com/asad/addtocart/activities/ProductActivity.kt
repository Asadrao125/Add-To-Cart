package com.asad.addtocart.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.addtocart.R
import com.asad.addtocart.adapters.ProductAdapter
import com.asad.addtocart.database.Database
import com.asad.addtocart.models.Product
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ProductActivity : AppCompatActivity() {
    var database: Database? = null
    var productRecyclerView: RecyclerView? = null
    var productsList: ArrayList<Product> = ArrayList()
    var cartList: ArrayList<Product> = ArrayList()
    var cartLayout: FrameLayout? = null
    var tvCount: TextView? = null
    var imgLogout: ImageView? = null
    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        supportActionBar?.hide()

        setAdapter()

        database = Database(this)
        database?.createDatabase()

        imgLogout = findViewById(R.id.imgLogout)
        mAuth = FirebaseAuth.getInstance()

        cartLayout?.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        imgLogout?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Empty cart and logout")
                .setMessage("Are you sure you want perform this action?")
                .setPositiveButton("Logout Anyway",
                    DialogInterface.OnClickListener { dialog, which ->
                        database?.clearTableData()
                        mAuth?.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                .setNegativeButton("Close", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        setCartCount()
    }

    private fun setAdapter() {
        productsList.clear()
        productsList = loadJSONFromAssets("products.json")
        productRecyclerView = findViewById(R.id.productRecyclerView)
        tvCount = findViewById(R.id.tvCount)
        cartLayout = findViewById(R.id.cartLayout)
        productRecyclerView?.layoutManager = GridLayoutManager(this, 2)
        productRecyclerView?.setHasFixedSize(true)
        productRecyclerView?.adapter = ProductAdapter(this, productsList)
    }

    private fun setCartCount() {
        cartList = database?.getAllProducts()!!
        if (cartList != null) {
            tvCount?.setText("" + cartList.size)
        } else {
            tvCount?.setText("0")
        }
    }

    fun Context.loadJSONFromAssets(fileName: String): ArrayList<Product> {
        applicationContext.assets.open(fileName).bufferedReader().use { reader ->
            try {
                val jsonObj = JSONObject(reader.readText())
                val jsonArray: JSONArray = jsonObj.getJSONArray("products")
                for (i in 0..jsonArray.length()) {
                    val jObject = jsonArray.getJSONObject(i)
                    val id = jObject.getString("id")
                    val title = jObject.getString("title")
                    val price = jObject.getDouble("price")
                    val url = jObject.getString("url")
                    productsList.add(Product(id, 0, price, title, url))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return productsList
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
        setCartCount()
    }
}