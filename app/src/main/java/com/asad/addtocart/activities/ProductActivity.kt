package com.asad.addtocart.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.addtocart.R
import com.asad.addtocart.adapters.ProductAdapter
import com.asad.addtocart.database.Database
import com.asad.addtocart.models.Product
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        supportActionBar?.hide()

        setAdapter()

        database = Database(this)
        database?.createDatabase()

        cartLayout?.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
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