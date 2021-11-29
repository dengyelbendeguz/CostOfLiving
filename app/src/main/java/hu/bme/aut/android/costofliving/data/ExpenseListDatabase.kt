package hu.bme.aut.android.costofliving.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ExpenseItem::class], version = 2)
//@TypeConverters(value = [ExpenseItem.Category::class])
abstract class ExpenseListDatabase : RoomDatabase() {
    abstract fun expenseItemDao(): ExpenseItemDao

    companion object {
        fun getDatabase(applicationContext: Context): ExpenseListDatabase {
            return Room.databaseBuilder(
                applicationContext,
                ExpenseListDatabase::class.java,
                "expense-list"
            ).build();
        }
    }
}
