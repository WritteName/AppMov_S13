package com.example.appmov_s13

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

data class CategorieItem(val imageResId: Int, val title: String, val targetCategorie: String)

class CategorieAdapter(private val items: List<CategorieItem>) : RecyclerView.Adapter<CategorieAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageEvento: ImageView = view.findViewById(R.id.imageEvento)
        val tituloEvento: TextView = view.findViewById(R.id.tituloEvento)
        val irEvento: LinearLayout = view.findViewById(R.id.irEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_activities, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]
        holder.imageEvento.setImageResource(item.imageResId)
        holder.tituloEvento.text = item.title

        holder.irEvento.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("tipo_evento", item.targetCategorie)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}