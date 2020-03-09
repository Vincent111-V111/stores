package com.example.myapplication

import com.example.myapplication.Models.Data
import com.example.myapplication.Models.Product

interface onProductsItemClickListener{
    fun onItemClick(product: Data, position: Int)
}