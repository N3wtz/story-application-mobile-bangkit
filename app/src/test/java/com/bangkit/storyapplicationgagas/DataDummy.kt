package com.bangkit.storyapplicationgagas

import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "name + $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i"
            )
            items.add(quote)
        }
        return items
    }
}