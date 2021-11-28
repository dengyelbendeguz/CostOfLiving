package hu.bme.aut.android.expenselist.data

import androidx.room.*

@Dao
interface ExpenseItemDao {
    @Query("SELECT * FROM expenseitem")
    fun getAll(): List<ExpenseItem>

    @Insert
    fun insert(expenseItems: ExpenseItem): Long

    @Update
    fun update(expenseItem: ExpenseItem)

    @Delete
    fun deleteItem(expenseItem: ExpenseItem)
}
