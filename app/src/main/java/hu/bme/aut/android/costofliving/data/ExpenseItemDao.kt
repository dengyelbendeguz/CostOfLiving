package hu.bme.aut.android.costofliving.data

import androidx.room.*

@Dao
interface ExpenseItemDao {
    @Query("SELECT * FROM expenseitem WHERE username=:userName")
    fun getAll(userName: String): List<ExpenseItem>

    @Query("SELECT * FROM expenseitem WHERE username=:userName AND year=:year")
    fun getYearlyExpenses(userName: String, year: Int): List<ExpenseItem>

    @Query("SELECT * FROM expenseitem WHERE username=:userName AND year=:year AND month=:month")
    fun getMonthlyExpenses(userName: String, year: Int, month: Int): List<ExpenseItem>

    @Query("SELECT * FROM  expenseitem WHERE is_shared")
    fun getSharedExpenses(): List<ExpenseItem>

    @Insert
    fun insert(expenseItems: ExpenseItem): Long

    @Update
    fun update(expenseItem: ExpenseItem)

    @Delete
    fun deleteItem(expenseItem: ExpenseItem)

    @Query("DELETE FROM expenseitem")
    fun clearTable()
}
