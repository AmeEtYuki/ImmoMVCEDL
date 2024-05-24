package com.example.oioj

class gestionToken {

    companion object {

        private var tokenValue: String? = null
        fun setToken(tokenValue: String) {
            this.tokenValue = tokenValue
        }

        fun getToken(): String? {
            return tokenValue;
        }
    }
}