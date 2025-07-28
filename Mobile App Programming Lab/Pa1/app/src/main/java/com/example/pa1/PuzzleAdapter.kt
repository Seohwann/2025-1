package com.example.pa1

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class PuzzleAdapter(
    private val context: Context,
    private var puzzlePieces: Array<Bitmap?>,
    private val gridSize: Int
) : BaseAdapter() {

    override fun getCount(): Int = puzzlePieces.size

    override fun getItem(position: Int): Bitmap? = puzzlePieces[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = (convertView as? ImageView) ?: ImageView(context).apply {
            val gridWidth = parent.measuredWidth
            val size = gridWidth / gridSize
            layoutParams = ViewGroup.LayoutParams(size, size)
            scaleType = ImageView.ScaleType.FIT_XY
        }
        if (puzzlePieces[position] == null) {
            imageView.setImageBitmap(null)
        } else {
            imageView.setImageBitmap(puzzlePieces[position])
        }
        return imageView
    }

    fun updatePuzzlePieces(newPieces: Array<Bitmap?>) {
        puzzlePieces = newPieces
        notifyDataSetChanged()
    }
}
