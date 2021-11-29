package hu.bme.aut.android.costofliving.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "expenseitem")
data class ExpenseItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "category") var category: String/*Category*/,
    @ColumnInfo(name = "cost") var cost: Int,
    @ColumnInfo(name = "is_expense") var isExpense: Boolean,
    @ColumnInfo(name = "username") var username: String
) {
}
