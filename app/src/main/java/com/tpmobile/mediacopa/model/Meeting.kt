package com.tpmobile.mediacopa.model

import com.google.gson.annotations.SerializedName

data class Meeting(

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("streetAddress")
	val streetAddress: String? = null,

	@field:SerializedName("lat")
	val lat: Any? = null,

	@field:SerializedName("lon")
	val lon: Any? = null,

)
