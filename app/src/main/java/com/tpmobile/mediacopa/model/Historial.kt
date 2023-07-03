package com.tpmobile.mediacopa.model

import com.google.gson.annotations.SerializedName

data class Historial(

	@field:SerializedName("meetingAddress")
	val meetingAddress: MeetingAddress? = null,

	@field:SerializedName("addresses")
	val addresses: List<AddressesItem?>? = null,

	@field:SerializedName("_id")
	val id: String? = null,

)

data class MeetingAddress(

	@field:SerializedName("streetAddress")
	val streetAddress: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("lon")
	val lon: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("lat")
	val lat: Any? = null,

)
