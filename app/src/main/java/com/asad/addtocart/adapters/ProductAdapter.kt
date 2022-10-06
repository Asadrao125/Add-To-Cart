package com.asad.addtocart.adapters

import android.content.Context
import android.util.Log
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

class ProductAdapter(var context: Context, var list: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    var database: Database? = Database(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product: Product = list.get(position)
        Commons.LoadImage(product.url, holder.imgProduct)
        holder.tvTitle.setText(product.title)
        holder.tvPrice.setText("" + product.price + " Rs")

        if (database?.isAdded(product.id.toInt())!!) {
            holder.tvAddToCart.setText("Added in Cart")
            holder.tvAddToCart.setTextColor(context.resources.getColor(R.color.white))
            holder.tvAddToCart.setBackgroundResource(R.drawable.background_curve_black)
            holder.tvAddToCart.isEnabled = false
        } else {
            holder.tvAddToCart.setText("Add To Cart")
            holder.tvAddToCart.setTextColor(context.resources.getColor(R.color.black))
            holder.tvAddToCart.setBackgroundResource(R.drawable.background_curve_white)
            holder.tvAddToCart.isEnabled = true
        }

        holder.imgPlus.setOnClickListener {
            val text: String = holder.tvCount.text.toString()
            var incCount: Int = text.toInt()
            incCount++
            product.quantity = incCount
            holder.tvCount.setText("" + incCount)
        }

        holder.imgMinus.setOnClickListener {
            val text: String = holder.tvCount.text.toString()
            var decCount: Int = text.toInt()
            if (decCount > 0) {
                decCount--
                product.quantity = decCount
                holder.tvCount.setText("" + decCount)
            }
        }

        holder.tvAddToCart.setOnClickListener {
            val text: String = holder.tvCount.text.toString()
            val count: Int = text.toInt()
            if (count == 0) {
                product.quantity = 1
            } else {
                product.quantity = count
            }
            val isInserted: Long = database?.insertCategory(product)!!
            if (isInserted > 0) {
                Toast(context, "Product Added")
                holder.tvAddToCart.setText("Added in Cart")
                holder.tvAddToCart.setTextColor(context.resources.getColor(R.color.white))
                holder.tvAddToCart.setBackgroundResource(R.drawable.background_curve_black)
                holder.tvAddToCart.isEnabled = false
            } else {
                Toast(context, "Failed to add")
            }
        }
    }

    override fun getItemViewType(position: Int): Int = position

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
        var tvAddToCart: TextView
        var item_cv: CardView

        init {
            imgProduct = itemView.findViewById(R.id.imgProduct)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            imgMinus = itemView.findViewById(R.id.imgMinus)
            imgPlus = itemView.findViewById(R.id.imgPlus)
            tvCount = itemView.findViewById(R.id.tvCount)
            item_cv = itemView.findViewById(R.id.item_cv)
            tvAddToCart = itemView.findViewById(R.id.tvAddToCart)
        }
    }
}