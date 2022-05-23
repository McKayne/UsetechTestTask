package com.el_nico.usetechtesttask.domain.entity.networking

import com.google.gson.annotations.SerializedName

class ImageLinkResponse(
    @SerializedName("items") var items: Array<ImageItem>?
)