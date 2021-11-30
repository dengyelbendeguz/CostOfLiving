package hu.bme.aut.android.costofliving.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loanitem")
data class LoanItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "loaner_name") var loanerName: String,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "is_loaner") var isLoaner: Boolean
)
