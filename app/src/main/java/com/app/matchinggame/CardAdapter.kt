package com.app.matchinggame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val cards: List<MemoryCard>,
    private val spanCount: Int,
    private val cardClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && !cards[position].isMatched) {
                    cardClickListener(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]

        holder.cardImage.setImageResource(
            if (card.isFaceUp || card.isMatched) card.imageResId
            else R.drawable.imgcardback
        )

        // Ensure square card size
        val context = holder.itemView.context
        val screenWidth = context.resources.displayMetrics.widthPixels
        val spacing = (8 * (spanCount + 1)) * context.resources.displayMetrics.density
        val cardSize = ((screenWidth - spacing) / spanCount).toInt()

        holder.itemView.layoutParams = holder.itemView.layoutParams.apply {
            width = cardSize
            height = cardSize
        }
    }

    override fun getItemCount(): Int = cards.size
}
