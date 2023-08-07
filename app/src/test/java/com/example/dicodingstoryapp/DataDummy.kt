package com.example.dicodingstoryapp

import com.example.dicodingstoryapp.data.api.model.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                "$i",
                i.toDouble(),
            )
            items.add(quote)
        }
        return items
    }
}