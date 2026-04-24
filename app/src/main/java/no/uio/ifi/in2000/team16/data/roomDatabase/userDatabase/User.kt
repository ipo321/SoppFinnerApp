package no.uio.ifi.in2000.team16.data.roomDatabase.userDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//Kinda an Example class for data objects to be into the database
//Imagine it set up as an SQL table where you can add more columns for relevant info
//Primary key is a value that has to be unique (I think) for all entries of the class into the
//database, e.g. multiple users cant have the same uid.



@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "navn") val name: String?
)

