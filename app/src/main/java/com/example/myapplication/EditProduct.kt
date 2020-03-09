package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.example.myapplication.Models.Data
import com.example.myapplication.Models.Product
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.ProductApi
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_product.*
import kotlinx.android.synthetic.main.detail_product.*
import kotlinx.android.synthetic.main.edit_product.*
import kotlinx.android.synthetic.main.product_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class EditProduct : AppCompatActivity(){

    lateinit var mImageView : ImageView
    lateinit var pages : Product
//    lateinit var imageUri : Uri
//    var imageData: ByteArray? = null

    companion object{
        val SELECT_IMAGE_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_product)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Edit Product"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)


        mImageView = (imgUpdate)

        val product : Data = intent.getParcelableExtra("product")

        val radioButtonColor = "cb${product.attributes.color}Update"
        var rbColor = findViewById<RadioButton>(resources.getIdentifier(radioButtonColor, "id", packageName))
        rgColorUpdate.check(rbColor.id)

        val radioButtonSize = "cb${product.attributes.size}Update"
        var rbSize = findViewById<RadioButton>(resources.getIdentifier(radioButtonSize, "id", packageName))
        rgSizeUpdate.check(rbSize.id)

        etProductNameUpdate.setText(product.attributes.name)
        etDescProductUpdate.setText(product.attributes.description)
        etPriceUpdate.setText(product.attributes.price.toString())
        etStockProductUpdate.setText(product.attributes.inStock.toString())
        btnPickImagesUpdate.setOnClickListener {

        val intent = Intent()
        intent.type ="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT_IMAGE"),SELECT_IMAGE_CODE)}

        form {
            input(etProductNameUpdate) {
                isNotEmpty()
            }
            input(etPriceUpdate) {
                isNotEmpty()
            }
            submitWith(btnSubmitUpdate) { result->

                val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)

                val productNameUpdate = etProductNameUpdate.text.toString()
                val productPriceUpdate =  etPriceUpdate.text.toString().toInt()
                val productStockUpdate = etStockProductUpdate.text.toString().toInt()
                val productDescriptionUpdate = etDescProductUpdate.text.toString()

                val checkedRbColor: Int = rgColorUpdate.checkedRadioButtonId
                val radioColor: RadioButton = findViewById(checkedRbColor)
                val productColorUpdate: String = radioColor.text.toString()

                val checkedRbSize: Int = rgSizeUpdate.checkedRadioButtonId
                val radioSize: RadioButton = findViewById(checkedRbSize)
                val productSizeUpdate: String = radioSize.text.toString()


                apiInterface.updateProduct(pages.meta.pagination.currentPage, product.attributes.id, productNameUpdate, productPriceUpdate, productStockUpdate, productDescriptionUpdate, productColorUpdate, productSizeUpdate)
                    .enqueue(object : Callback<Data> {
                        override fun onResponse(call: Call<Data>, response: Response<Data>) {
                            println(response.body())
                            if (response.isSuccessful) {
                                Toast.makeText(this@EditProduct,"Product was successfully created",Toast.LENGTH_LONG).show()

                            }
                            else {
                                Toast.makeText(this@EditProduct, response.message(),Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<Data>, t: Throwable) = t.printStackTrace()
                    })
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_CODE && resultCode == Activity.RESULT_OK){
            if (data != null){
                try {
                    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver,data.data)
                    mImageView.setImageBitmap(bitmap)
                }catch (exp: IOException){
                    exp.printStackTrace()
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(applicationContext,"CANCALED", Toast.LENGTH_LONG).show()
        }
    }
}