package com.example.pa1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var originalImageView: ImageView
    private lateinit var puzzleGridView: GridView
    private lateinit var linearLayout: LinearLayout
    private lateinit var button3x3: Button
    private lateinit var button4x4: Button
    private lateinit var button5x5: Button
    private lateinit var shuffleButton: Button

    private var puzzlePieces: Array<Bitmap?> = arrayOf()
    private var gridSize: Int = 3
    private var emptyPosition: Int = 0
    private var originalBitmap: Bitmap? = null
    private lateinit var adapter: PuzzleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        originalImageView = findViewById(R.id.originalImageView)
        puzzleGridView = findViewById(R.id.puzzleGridView)
        button3x3 = findViewById(R.id.button3x3)
        button4x4 = findViewById(R.id.button4x4)
        button5x5 = findViewById(R.id.button5x5)
        shuffleButton = findViewById(R.id.shuffleButton)
        linearLayout = findViewById(R.id.linearLayout)

        originalBitmap = loadBitmapFromAssets("img.jpg")?.also {
            originalImageView.setImageBitmap(it)
        } ?: run {
            Toast.makeText(this, "Load Fail", Toast.LENGTH_LONG).show()
            return
        }


        if (savedInstanceState != null) {
            gridSize = savedInstanceState.getInt("gridSize", 3)
            emptyPosition = savedInstanceState.getInt("emptyPosition", gridSize * gridSize - 1)
            puzzleGridView.numColumns = gridSize
            val parcelables = savedInstanceState.getParcelableArray("puzzlePieces")
            puzzlePieces = Array(gridSize * gridSize) { index ->
                parcelables?.get(index) as? Bitmap
            }
        }
        puzzleGridView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                puzzleGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initializePuzzle(gridSize)
                adapter = PuzzleAdapter(this@MainActivity, puzzlePieces, gridSize)
                puzzleGridView.adapter = adapter

            }

        })
        adapter = PuzzleAdapter(this, puzzlePieces, gridSize)
        puzzleGridView.adapter = adapter

        button3x3.setOnClickListener { initializePuzzle(3) }
        button4x4.setOnClickListener { initializePuzzle(4) }
        button5x5.setOnClickListener { initializePuzzle(5) }
        shuffleButton.setOnClickListener { shufflePuzzle() }

        puzzleGridView.setOnItemClickListener { _, _, position, _ ->
            if (isMovable(position)) {
                swapPieces(position, emptyPosition)
                emptyPosition = position
                adapter.updatePuzzlePieces(puzzlePieces)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("gridSize", gridSize)
        outState.putInt("emptyPosition", emptyPosition)
        outState.putParcelableArray("puzzlePieces", puzzlePieces)
    }

    private fun loadBitmapFromAssets(path: String): Bitmap? {
        return try {
            assets.open(path).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            null
        }
    }

    private fun initializePuzzle(size: Int) {
        gridSize = size
        puzzleGridView.numColumns = size
        puzzlePieces = arrayOfNulls(size * size)
        emptyPosition = size * size - 1

        originalBitmap?.let { bitmap ->
            val gridSizePx = puzzleGridView.measuredWidth
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, gridSizePx, gridSizePx, true)
            val pieceWidth = scaledBitmap.width / size
            val pieceHeight = scaledBitmap.height / size

            for (i in 0 until size) {
                for (j in 0 until size) {
                    val index = i * size + j
                    if (index == emptyPosition) {
                        puzzlePieces[index] = null
                    } else {
                        puzzlePieces[index] = Bitmap.createBitmap(
                            scaledBitmap,
                            j * pieceWidth,
                            i * pieceHeight,
                            pieceWidth,
                            pieceHeight
                        )
                    }
                }
            }
        }

        puzzlePieces.forEachIndexed { index, bitmap ->
            println("Piece $index: ${if (bitmap == null) "null" else "Bitmap"}")
        }

        puzzleGridView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                puzzleGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                puzzleGridView.numColumns = size
                adapter = PuzzleAdapter(this@MainActivity, puzzlePieces, size)
                puzzleGridView.adapter = adapter
            }
        })
    }

    private fun isMovable(position: Int): Boolean {
        val row = position / gridSize
        val col = position % gridSize
        val emptyRow = emptyPosition / gridSize
        val emptyCol = emptyPosition % gridSize

        return (row == emptyRow && kotlin.math.abs(col - emptyCol) == 1) ||
                (col == emptyCol && kotlin.math.abs(row - emptyRow) == 1)
    }

    private fun swapPieces(pos1: Int, pos2: Int) {
        val temp = puzzlePieces[pos1]
        puzzlePieces[pos1] = puzzlePieces[pos2]
        puzzlePieces[pos2] = temp
    }

    private fun shufflePuzzle() {
        val random = Random.Default
        repeat(100 * gridSize) {
            val row = emptyPosition / gridSize
            val col = emptyPosition % gridSize
            val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            val (dr, dc) = directions.random(random)
            val newRow = row + dr
            val newCol = col + dc

            if (newRow in 0 until gridSize && newCol in 0 until gridSize) {
                val newPosition = newRow * gridSize + newCol
                swapPieces(emptyPosition, newPosition)
                emptyPosition = newPosition
            }
        }
        adapter.updatePuzzlePieces(puzzlePieces)
    }
}
