package com.tpmobile.mediacopa.model

import com.google.gson.annotations.SerializedName

data class RequestMeetings(

	@field:SerializedName("addresses")
	val addresses: List<AddressesItem?>? = null,

	@field:SerializedName("type")
	val type: String? = null
)

data class AddressesItem(

	@field:SerializedName("lon")
	val lon: Any? = null,

	@field:SerializedName("lat")
	val lat: Any? = null
)
