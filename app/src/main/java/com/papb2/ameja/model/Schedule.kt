package com.papb2.ameja.model

import android.os.Parcel
import android.os.Parcelable

data class Schedule(var id: Int = 0, var agenda: String? = null, var date: String? = null, var start: String? = null, var end: String? = null, var location: String? = null, var status: Int = 0, var important: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(agenda)
        parcel.writeString(date)
        parcel.writeString(start)
        parcel.writeString(end)
        parcel.writeString(location)
        parcel.writeInt(status)
        parcel.writeByte(if (important) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Schedule> {
        override fun createFromParcel(parcel: Parcel): Schedule {
            return Schedule(parcel)
        }

        override fun newArray(size: Int): Array<Schedule?> {
            return arrayOfNulls(size)
        }
    }
}