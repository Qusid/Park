package com.example.park

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.HashMap

class datastore {

    fun bassic() {
        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("message")
        myRef.setValue("Hello world")

    }


}


