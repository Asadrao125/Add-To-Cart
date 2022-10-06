package com.asad.addtocart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.asad.addtocart.R
import com.asad.addtocart.database.Database
import com.asad.addtocart.models.Product
import com.asad.addtocart.utils.Commons
import com.asad.addtocart.utils.Commons.Companion.Toast
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class CartAdapter(var context: Context, var list: ArrayList<Product>) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    var database: Database? = Database(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product: Product = list.get(position)
        Commons.LoadImage(product.url, holder.imgProduct)
        holder.tvTitle.setText(product.title)
        holder.tvPrice.setText("" + product.price + " Rs")
        holder.tvCount.setText("" + product.quantity)

        holder.imgPlus.setOnClickListener {
            val text: String = holder.tvCount.text.toString()
            var incCount: Int = text.toInt()
            incCount++
            product.quantity = incCount
            holder.tvCount.setText("" + incCount)
            database?.updateProductQuantity(product.id.toInt(), product.quantity)
        }

        holder.imgMinus.setOnClickListener {
            val text: String = holder.tvCount.text.toString()
            var decCount: Int = text.toInt()
            if (decCount > 1) {
                decCount--
                product.quantity = decCount
                holder.tvCount.setText("" + decCount)
                database?.updateProductQuantity(product.id.toInt(), product.quantity)
            } else {
                database?.deleteCategory(product.id.toInt())
                list.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgProduct: ImageView
        var tvTitle: TextView
        var tvPrice: TextView
        var imgMinus: ImageView
        var imgPlus: ImageView
        var tvCount: TextView

        init {
            imgProduct = itemView.findViewById(R.id.imgProduct)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            imgMinus = itemView.findViewById(R.id.imgMinus)
            imgPlus = itemView.findViewById(R.id.imgPlus)
            tvCount = itemView.findViewById(R.id.tvCount)
        }
    }
}