package hu.bme.aut.android.costofliving.data

import androidx.room.*
import hu.bme.aut.android.costofliving.data.ExpenseItem

@Dao
interface ExpenseItemDao {
    @Query("SELECT * FROM expenseitem WHERE username=:userName")
    fun getAll(userName: String): List<ExpenseItem>

    @Insert
    fun insert(expenseItems: ExpenseItem): Long

    @Update
    fun update(expenseItem: ExpenseItem)

    @Delete
    fun deleteItem(expenseItem: ExpenseItem)
}
