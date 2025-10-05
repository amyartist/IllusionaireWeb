package com.illusionaireweb

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform