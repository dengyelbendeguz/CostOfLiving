package hu.bme.aut.android.costofliving.data

import androidx.room.*

@Dao
interface LoanItemDao {
    @Query("SELECT * FROM loanitem WHERE username=:userName")
    fun getAll(userName: String): List<LoanItem>

    @Query("SELECT * FROM loanitem WHERE username=:userName AND is_loaner=1")
    fun getLoaners(userName: String): List<LoanItem>

    @Query("SELECT * FROM loanitem WHERE username=:userName AND is_loaner=0")
    fun getOwers(userName: String): List<LoanItem>

    @Insert
    fun insert(loanItems: LoanItem): Long

    @Update
    fun update(loanItems: LoanItem)

    @Delete
    fun deleteItem(loanItems: LoanItem)
}
