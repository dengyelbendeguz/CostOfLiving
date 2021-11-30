package hu.bme.aut.android.costofliving.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LoanItem::class], version = 1)
abstract class LoanListDatabase : RoomDatabase() {
    abstract fun loanItemDao(): LoanItemDao

    companion object {
        fun getDatabase(applicationContext: Context): LoanListDatabase {
            return Room.databaseBuilder(
                applicationContext,
                LoanListDatabase::class.java,
                "loan-list"
            ).build();
        }
    }
}
