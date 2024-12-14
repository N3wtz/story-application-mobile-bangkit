package com.bangkit.storyapplicationgagas.Data.Response

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)