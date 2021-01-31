package com.turik2304.developerslifeapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.turik2304.developerslifeapp.network.RetroInstance
import com.turik2304.developerslifeapp.network.RetroService
import retrofit2.Call
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var tvDesc: TextView
    lateinit var btnNextPost: Button
    lateinit var btnPrevPost: Button
    lateinit var circularProgressDrawable: CircularProgressDrawable

    var queueOfPosts: Deque<PostData> = LinkedList<PostData>()
    var postCache: Deque<PostData> = LinkedList<PostData>()

    val LOG = "myLogs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        tvDesc = findViewById(R.id.tvDesc)
        btnNextPost = findViewById(R.id.btnNextPost)
        btnPrevPost = findViewById(R.id.btnPrevPost)
        btnPrevPost.isEnabled = false

        circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 40f
        circularProgressDrawable.setColorSchemeColors(Color.GRAY)
        circularProgressDrawable.start()

        createData()

        btnNextPost.setOnClickListener {
            if (postCache.isNotEmpty()) {
                queueOfPosts.add(postCache.removeLast())
                val gifURL = queueOfPosts.last.gifURL
                val description = queueOfPosts.last.description
                loadData(gifURL, description)
                Log.d(LOG, "Data loaded from cache")
            } else {
                createData()
            }
            if (queueOfPosts.size > 0) {
                btnPrevPost.isEnabled = true
            }
        }

        btnPrevPost.setOnClickListener {
            if (queueOfPosts.size == 2) {
                it.isEnabled = false
            }
            postCache.add(queueOfPosts.removeLast())
            Log.d(LOG, "sizeOfQueue(PrevPressed) = " + queueOfPosts.size)
            val gifURL = queueOfPosts.last.gifURL
            val description = queueOfPosts.last.description
            loadData(gifURL, description)
        }


    }

    fun createData() {
        val retroInstance = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val call = retroInstance.getLatestData()
        call.enqueue(object : retrofit2.Callback<PostData> {
            override fun onFailure(call: Call<PostData>, t: Throwable) {
                tvDesc.text = "ERROR"
                imageView.setImageResource(R.drawable.error_loading)

            }

            override fun onResponse(call: Call<PostData>, response: Response<PostData>) {
                if (response.isSuccessful) {
                    val gifURL = response.body()?.gifURL
                    val description = response.body()?.description
                    if (gifURL != null && description != null) {
                        queueOfPosts.add(response.body())
                        Log.d(
                            LOG,
                            "Data created, size of queueOfPosts = " + queueOfPosts.size
                        )
                    }
                    loadData(gifURL, description)
                }
            }
        })
    }

    fun loadData(gifURL: String?, description: String?) {
        tvDesc.text = description
        Glide.with(imageView)
            .load(gifURL)
            .placeholder(circularProgressDrawable)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.error_loading)
            .into(imageView)
        Log.d(LOG, "Data loaded")
    }

}