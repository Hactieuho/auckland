package com.liz.auckland.util.slide

import com.liz.auckland.model.Address

class Testkt {
    fun test() {
        var address = generateAddress()
        address?.let {
            it.name = "Ha Noi"
            // Do something
        }
    }

    fun generateAddress() = Address()
}