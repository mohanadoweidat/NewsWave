package com.example.newswave

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newswave.Models.NewsApiResponse
import com.example.newswave.Models.NewsHeadlines
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth



class MainActivity : AppCompatActivity(), SelectListener, View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var customAdapter: CustomAdapter
    private lateinit var dialog: ProgressDialog
    private lateinit var b1: Button
    private lateinit var b2: Button
    private lateinit var b3: Button
    private lateinit var b4: Button
    private lateinit var b5: Button
    private lateinit var b6: Button
    private lateinit var b7: Button
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private  var lastSelectedCountry: String = "SE"
    private lateinit var topAppBar: MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user == null){
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        else{

            val manager =  RequestManager(this);
            manager.getNewsHeadlines(listener, "general", null,lastSelectedCountry);

            fillCountryDropdown()
            initSearchView()
            initAndHandleLogout()
        }
    }


    private fun initAndHandleLogout(){
        topAppBar = findViewById(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.logOut -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }
    private fun initSearchView(){
        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission
                dialog.setTitle("Fetching news articles of $query")
                dialog.show()
                val manager = RequestManager(this@MainActivity);
                manager.getNewsHeadlines(listener, "general", query, lastSelectedCountry);
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle query text changes
                return false
            }
        })


        dialog = ProgressDialog(this)
        dialog.setTitle("Fetching news articles...")
        dialog.show()

        b1 = findViewById(R.id.btn_1)
        b1.setOnClickListener(this)
        b2 = findViewById(R.id.btn_2)
        b2.setOnClickListener(this)
        b3 = findViewById(R.id.btn_3)
        b3.setOnClickListener(this)
        b4 = findViewById(R.id.btn_4)
        b4.setOnClickListener(this)
        b5 = findViewById(R.id.btn_5)
        b5.setOnClickListener(this)
        b6 = findViewById(R.id.btn_6)
        b6.setOnClickListener(this)
        b7 = findViewById(R.id.btn_7)
        b7.setOnClickListener(this)
    }
    private fun fillCountryDropdown(){
        val countriesMap = mapOf(
            "United Arab Emirates" to "AE",
            "Argentina" to "AR",
            "Austria" to "AT",
            "Australia" to "AU",
            "Belgium" to "BE",
            "Bulgaria" to "BG",
            "Brazil" to "BR",
            "Canada" to "CA",
            "Switzerland" to "CH",
            "China" to "CN",
            "Colombia" to "CO",
            "Cuba" to "CU",
            "Czech Republic" to "CZ",
            "Germany" to "DE",
            "Egypt" to "EG",
            "France" to "FR",
            "United Kingdom" to "GB",
            "Greece" to "GR",
            "Hong Kong" to "HK",
            "Hungary" to "HU",
            "Indonesia" to "ID",
            "Ireland" to "IE",
            "Israel" to "IL",
            "India" to "IN",
            "Italy" to "IT",
            "Japan" to "JP",
            "South Korea" to "KR",
            "Lithuania" to "LT",
            "Latvia" to "LV",
            "Morocco" to "MA",
            "Mexico" to "MX",
            "Malaysia" to "MY",
            "Nigeria" to "NG",
            "Netherlands" to "NL",
            "Norway" to "NO",
            "New Zealand" to "NZ",
            "Philippines" to "PH",
            "Poland" to "PL",
            "Portugal" to "PT",
            "Romania" to "RO",
            "Serbia" to "RS",
            "Russia" to "RU",
            "Saudi Arabia" to "SA",
            "Sweden" to "SE",
            "Singapore" to "SG",
            "Slovenia" to "SI",
            "Slovakia" to "SK",
            "Thailand" to "TH",
            "Turkey" to "TR",
            "Taiwan" to "TW",
            "Ukraine" to "UA",
            "United States" to "US",
            "Venezuela" to "VE",
            "South Africa" to "ZA"
        )
        val countryNames = countriesMap.keys.toTypedArray()
        val autoComplete = findViewById<AutoCompleteTextView>(R.id.auto_complete)

        // Populate the dropdown with country names
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countryNames)
        autoComplete.setAdapter(adapter)


        autoComplete.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            // Handle the selected item
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Get the selected country code from the dropdown
            val selectedCountryCode = countriesMap[countryNames[position]]

            dialog.setTitle("Fetching news articles of country $selectedItem")
            dialog.show()
            val manager =  RequestManager(this);
            manager.getNewsHeadlines(listener, "general", null, selectedCountryCode);
            lastSelectedCountry = selectedCountryCode ?: "SE"
            //Toast.makeText(this@MainActivity, "Selected country:" + selectedItem + ", it's code: " + selectedCountryCode , Toast.LENGTH_SHORT).show()
        }
    }



    private val listener: OnFetchDataListener<NewsApiResponse> = object : OnFetchDataListener<NewsApiResponse> {
        fun onSuccess(response: NewsApiResponse) {
            // Implementation of onSuccess

        }

        fun onFailure(error: Throwable) {
            // Implementation of onFailure
        }

        override fun onFetchData(list: MutableList<NewsHeadlines>?, message: String?) {
            if (list != null) {
                if (list.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No data found!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else{
                    showNews(list)
                    dialog.dismiss()
                }
            };
        }

        override fun onError(message: String?) {
            Toast.makeText(this@MainActivity, "An error occurred!!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showNews(list: List<NewsHeadlines>) {
        // Implementation of showNews
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        customAdapter = CustomAdapter(this@MainActivity, list,this)
        recyclerView.adapter = customAdapter
    }
    override fun OnNewsClicked(headlines: NewsHeadlines?) {
        try {
            if (headlines != null) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("data", headlines)
                startActivity(intent)
               // Toast.makeText(this, "Clicked the box", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "Error: Clicked item is null")
                Toast.makeText(this, "Error: Clicked item is null", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving data from intent", e)
            Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onClick(p0: View?) {
        val button = p0 as Button
        var category = button.text.toString()

        dialog.setTitle("Fetching news articles of $category")
        dialog.show()
        val manager =  RequestManager(this);
        manager.getNewsHeadlines(listener, category, null, lastSelectedCountry);
    }


}