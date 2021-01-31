package com.turik2304.developerslifeapp.fragments

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.json.responseJson
import com.turik2304.developerslifeapp.R
import com.turik2304.developerslifeapp.fragments.network.ResponseStatus
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


open class MyFragment : Fragment() {
    private var counter = 0
    private var jSONcounter = 0
    private var index = -1
    private var lastPictureIndex = -1
    private var currObj = JSONArray()
    private var cachedFiles: MutableList<String> = mutableListOf()
    private var descriptions: MutableList<String> = mutableListOf()
    private val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

    protected var category = ""
    protected lateinit var mImageView: ImageView
    protected lateinit var mTextView: TextView

    protected lateinit var circularProgressDrawable: CircularProgressDrawable

    protected fun back() {
        if (index <= 0) return

        --index
        try {
            mTextView.text = descriptions[index]
            Glide.with(this).asGif()
                .placeholder(circularProgressDrawable)
                .transition(DrawableTransitionOptions.withCrossFade())
                .load(cachedFiles[index])
                .transform(RoundedCorners(15))
                .error(R.drawable.ic_broken_image_24px)
                .apply(requestOptions)
                .into(mImageView)
        } catch (e: Exception) {
            val myToast = Toast.makeText(
                requireContext(),
                "Что-то пошло не так. Попробуйте снова нажать на стрелку",
                Toast.LENGTH_LONG
            )
            myToast.show()
            if (index < cachedFiles.size) {
                cachedFiles.removeAt(index)
            }
            if (index < descriptions.size) {
                descriptions.removeAt(index)
            }
        }
    }

    protected fun currGif() {
        if (index < 0) {
            nextGif()
        } else if (lastPictureIndex < 0 || lastPictureIndex >= 0 && index < lastPictureIndex) {
            ++index
            back()
        } else {
            mTextView.text = "В этой категории больше ничего нет"
            mImageView.setImageResource(R.drawable.ic_camera_alt_24px)
        }
    }

    protected open fun nextGif() {
        try {
            ++index
            if (index < cachedFiles.size) {
                mTextView.text = descriptions[index]
                Glide.with(this).asGif()
                    .placeholder(circularProgressDrawable)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .load(cachedFiles[index])
                    .transform(RoundedCorners(15))
                    .error(R.drawable.ic_broken_image_24px)
                    .apply(requestOptions)
                    .into(mImageView)
                return
            }
            index = cachedFiles.size
            while (jSONcounter >= currObj.length()) {
                jSONcounter = 0
                lateinit var response: ResponseStatus
                runBlocking {
                    val job = GlobalScope.launch {
                        response = getRequest()
                    }
                    job.join()
                }
                var flag = false
                if (response.getException() == null) {
                    val (bytes, error) = response.getResponse()
                    if (bytes == null || error != null) {
                        flag = true
                    }
                }
                if (flag || response.getException() != null) {
                    val myToast = Toast.makeText(
                        requireContext(),
                        "Не удалось подключиться к сайту, проверьте подключение к интернету " +
                                "и попробуйте ещё раз",
                        Toast.LENGTH_LONG
                    )
                    myToast.show()
                    --index
                    return
                }
                val resp = response.getResponse()
                ++counter
                val obj: JSONObject = resp.get().obj()
                val totalCount = obj.getString("totalCount").toInt()
                if (totalCount == 0) {
                    mTextView.text = "В этой категории больше ничего нет"
                    mImageView.setImageResource(R.drawable.ic_camera_alt_24px)
                    if (lastPictureIndex < 0 || lastPictureIndex >= 0 && lastPictureIndex > index) {
                        lastPictureIndex = index
                    }
                    return
                }
                currObj = obj["result"] as JSONArray
            }
            val currGif = currObj[jSONcounter] as JSONObject
            ++jSONcounter
            val gifUrl = currGif.getString("gifURL")
            val description = currGif.getString("description")
            mTextView.text = description
            Glide.with(this).asGif()
                .placeholder(circularProgressDrawable)
                .transition(DrawableTransitionOptions.withCrossFade())
                .load(gifUrl)
                .transform(RoundedCorners(15))
                .error(R.drawable.ic_broken_image_24px)
                .apply(requestOptions)
                .into(mImageView)
            cachedFiles.add(gifUrl)
            descriptions.add(description)
        } catch (e: Exception) {
            val myToast = Toast.makeText(
                requireContext(),
                "Что-то пошло не так. Попробуйте снова нажать на стрелку",
                Toast.LENGTH_LONG
            )
            myToast.show()
            if (index < cachedFiles.size) {
                cachedFiles.removeAt(index)
            }
            if (index < descriptions.size && cachedFiles.size != descriptions.size) {
                descriptions.removeAt(index)
            }
            back()
        }
    }

    protected open suspend fun getRequest(): ResponseStatus {
        val responseStatus = ResponseStatus()
        supervisorScope {
            val request = GlobalScope.async {
                val manager: FuelManager = FuelManager().apply {
                    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                        override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) = Unit

                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) = Unit
                    })

                    socketFactory = SSLContext.getInstance("SSL").apply {
                        init(null, trustAllCerts, java.security.SecureRandom())
                    }.socketFactory

                    hostnameVerifier = HostnameVerifier { _, _ -> true }
                }
                val (_, _, result) = manager.request(
                    Method.GET,
                    "https://developerslife.ru/$category/$counter?json=true"
                ).responseJson()
                result
            }
            try {
                responseStatus.setResponse(request.await())
            } catch (e: Exception) {
                responseStatus.setException(e)
            }
        }
        return responseStatus
    }

}