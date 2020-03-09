package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.ProductAdapter
import com.example.myapplication.Models.Data
import com.example.myapplication.Models.Product
import com.example.myapplication.Services.ApiClient
import com.example.myapplication.Services.ProductApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), onProductsItemClickListener {


    private val products : ArrayList<Data> = ArrayList()

    private val TAG = javaClass.simpleName
    private var productAdapter by Delegates.notNull<ProductAdapter>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        page = 1
        totalPage = 0

        fab.setOnClickListener {
            val intent = Intent(this, AddProduct::class.java)
            startActivity(intent)
        }

        rvProducts.layoutManager = GridLayoutManager(this, 2 ) as RecyclerView.LayoutManager?
        setUpRecyleView()
        initListener()

        //** Set the colors of the Pull To Refresh View
        swipeContainer.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        swipeContainer.setColorSchemeColors(Color.WHITE)

        swipeContainer.setOnRefreshListener {
            products.clear()
            setUpRecyleView()
            swipeContainer.isRefreshing = false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)

        if (searchItem!=null) {
            val searchView = searchItem.actionView as SearchView

//            val searchHint = getString(R.string.searchHint)
//            searchView.setQueryHint(searchHint)

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.toString().isNotEmpty()) {

                        startRecyclerView(generateData(newText))
                        companyList.clear()
                    }
                    else {
                        startRecyclerView(generateData(newText))
                        companyList.clear()
                    }
                    return false
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun setUpRecyleView() {
        Log.d(TAG, "page: $page")
        showLoading(true)

        val apiInterface : ProductApi = ApiClient.getClient().create(ProductApi::class.java)

        apiInterface.getProducts(page)
            .enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    response.body()?.data?.forEach {
                        products.add(it)
                    }
                    if (page == 1) {
                        productAdapter = ProductAdapter(products, this@MainActivity)
                        rvProducts.adapter = productAdapter
                    } else {
                        productAdapter.refreshAdapter(products)
                    }

                    totalPage = response.body()!!.meta.pagination.totalPage
                    hideLoading()
                }

                override fun onFailure(call: Call<Product>, t: Throwable){
                    t.printStackTrace()
                }
            })
    }

    override fun onItemClick(product:Data, position:Int){
        // Toast.makeText(this, product.attributes.name, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailProduct::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }


    private fun initListener() {
        rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager?.itemCount
                val lastVisiblePosition = linearLayoutManager?.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                Log.d(TAG, "countItem: $countItem")
                Log.d(TAG, "lastVisiblePosition: $lastVisiblePosition")
                Log.d(TAG, "isLastPosition: $isLastPosition")
                if (!isLoading && isLastPosition && page < totalPage) {
                    showLoading(true)
                    page = page.let { it.plus(1) }
                    setUpRecyleView()
                }
            }
        })
    }

    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        progress_bar_horizontal_activity_main.visibility = View.VISIBLE
        rvProducts.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_horizontal_activity_main.visibility = View.GONE
        rvProducts.visibility = View.VISIBLE
    }



}
