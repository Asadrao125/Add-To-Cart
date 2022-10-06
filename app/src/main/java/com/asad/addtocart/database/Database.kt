package com.asad.addtocart.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.widget.Toast
import com.asad.addtocart.models.Product
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class Database(activity: Context) {
    var DB_PATH = "data/data/com.asad.addtocart/databases/"
    var DB_NAME = "add_to_cart_app_db.sqlite"
    var activity: Context
    var sqLiteDatabase: SQLiteDatabase? = null

    fun createDatabase() {
        var dBExist = false
        try {
            dBExist = checkDatabase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (dBExist) {
        } else {
            try {
                sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
                sqLiteDatabase!!.close()
                copyDatabaseTable()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun copyDatabaseTable() {
        val myInput: InputStream = activity.getAssets().open(DB_NAME)
        val outFileName = DB_PATH + DB_NAME
        val myOutput: OutputStream = FileOutputStream(outFileName)
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    private fun checkDatabase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        val myPath = DB_PATH + DB_NAME
        try {
            try {
                checkDB = SQLiteDatabase.openDatabase(
                    myPath,
                    null,
                    SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            //no database exists...
        }
        checkDB?.close()
        return if (checkDB != null) true else false
    }

    fun open() {
        sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
    }

    fun close() {
        sqLiteDatabase!!.close()
    }

    /*
    CREATE TABLE "product" (
	"id"	INTEGER,
	"productId"	INTEGER,
	"productQuantity"	INTEGER,
	"productPrice"	REAL,
	"productTitle"	TEXT,
	"productUrl"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT));
    */

    fun insertCategory(product: Product): Long {
        var rowId: Long = -1
        try {
            open()
            val cv = ContentValues()
            cv.put("productId", product.id)
            cv.put("productQuantity", product.quantity)
            cv.put("productPrice", product.price)
            cv.put("productTitle", product.title)
            cv.put("productUrl", product.url)
            rowId = sqLiteDatabase!!.insert("product", null, cv)
            close()
        } catch (e: SQLiteException) {
            Toast.makeText(activity, "Database exception", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        println("-- Record inserted rowId : $rowId")
        return rowId
    }

    fun getAllProducts(): ArrayList<Product> {
        open()
        val productBeans: ArrayList<Product> = ArrayList()
        var temp: Product?
        val query1 = "select * from product"
        println("--query in getAllProducts : $query1")
        val cursor: Cursor = sqLiteDatabase!!.rawQuery(query1, null)
        if (cursor.moveToFirst()) {
            do {
                val id: Int = cursor.getInt(cursor.getColumnIndex("id"))
                val productId: String = cursor.getString(cursor.getColumnIndex("productId"))
                val quantity: String = cursor.getString(cursor.getColumnIndex("productQuantity"))
                val price: Double = cursor.getDouble(cursor.getColumnIndex("productPrice"))
                val title: String = cursor.getString(cursor.getColumnIndex("productTitle"))
                val url: String = cursor.getString(cursor.getColumnIndex("productUrl"))
                temp = Product(id.toString(), quantity.toInt(), price, title, url)
                productBeans.add(temp)
                temp = null
            } while (cursor.moveToNext())
            close()
            return productBeans
        }
        close()
        return productBeans
    }

    fun deleteCategory(id: Int) {
        open()
        val query = "delete from product WHERE id = '$id'"
        sqLiteDatabase!!.execSQL(query)
        close()
    }

    fun clearTableData() {
        open()
        val query = "delete from product"
        sqLiteDatabase!!.execSQL(query)
        close()
    }

    fun isAdded(id: Int): Boolean {
        open()
        val query = "SELECT * FROM product WHERE productId = '$id'"
        val cursor = sqLiteDatabase!!.rawQuery(query, null)
        return if (cursor?.count!! > 0) true else false
        close()
    }

    fun updateProductQuantity(id: Int, quantity: Int) {
        open()
        val dataToUpdate = ContentValues()
        dataToUpdate.put("productQuantity", quantity)
        val where = "id='$id'"
        try {
            val rows = sqLiteDatabase!!.update("product", dataToUpdate, where, null)
            println("-- rows updated: $rows")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        close()
    }

    init {
        this.activity = activity
    }
}