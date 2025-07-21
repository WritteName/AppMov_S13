package com.example.appmov_s13

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategorieAdapter

    private val cardList = listOf(
        CategorieItem(R.drawable.img_restaurant, "Restaurantes", "Restaurantes"),
        CategorieItem(R.drawable.img_hamburguer, "Hamburguesería", "Hamburguesería"),
        CategorieItem(R.drawable.img_postre, "Cafetería", "Cafetería")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CategorieAdapter(cardList)
        recyclerView.adapter = adapter
    }
}