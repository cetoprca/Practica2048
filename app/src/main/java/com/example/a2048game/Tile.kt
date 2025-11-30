package com.example.a2048game

data class Tile(var value: Int = 0) {
    fun isEmpty() = value == 0
}
