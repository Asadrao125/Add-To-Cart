package com.asad.addtocart.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.addtocart.R
import com.asad.addtocart.adapters.CartAdapter
import com.asad.addtocart.database.Database
import com.asad.addtocart.models.Product

class CartActivity : AppCompatActivity() {
    var database: Database? = null
    var productRecyclerView: RecyclerView? = null
    var tvAmount: TextView? = null
    var bottomlLayout: LinearLayout? = null
    var list: ArrayList<Product> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setTitle("Cart")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        database = Database(this)
        productRecyclerView = findViewById(R.id.cartRecyclerView)
        tvAmount = findViewById(R.id.tvAmount)
        bottomlLayout = findViewById(R.id.bottomlLayout)

        productRecyclerView?.layoutManager = GridLayoutManager(this, 2)
        productRecyclerView?.setHasFixedSize(true)
        list.clear()
        list = database?.getAllProducts()!!
        productRecyclerView?.adapter = CartAdapter(this, list)

        if (list != null) {
            tvAmount?.setText("" + calculateTotalAmount(list) + " Rs")
        } else {
            bottomlLayout?.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun calculateTotalAmount(list: ArrayList<Product>): Double {
        var sum = 0.0
        for (i in list) {
            sum = (sum + (i.price * i.quantity))
            Log.d("sum_expense", "sumExpense: $sum")
        }
        return sum
    }
}