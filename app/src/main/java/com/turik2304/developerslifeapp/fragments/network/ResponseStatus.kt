package com.turik2304.developerslifeapp.fragments.network

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.result.Result

class ResponseStatus() {
    private lateinit var response : Result<FuelJson, FuelError>
    private var e : Exception? = null

    fun setResponse(r: Result<FuelJson, FuelError>) {
        response = r
    }

    fun setException(ex : Exception) {
        e = ex
    }

    fun getResponse() : Result<FuelJson, FuelError> {
        return response
    }

    fun getException() : Exception? {
        return e
    }
}