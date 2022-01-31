package hu.bme.aut.android.costofliving.file

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import hu.bme.aut.android.costofliving.data.ExpenseItem
import hu.bme.aut.android.costofliving.data.ExpenseListDatabase
import hu.bme.aut.android.costofliving.data.LoanItem
import hu.bme.aut.android.costofliving.data.LoanListDatabase
import java.io.*
import java.util.*
import kotlin.concurrent.thread

class FileHandler(context: Context, username: String) {
    private val applicationContext: Context = context
    private val user: String = username

    fun exportToCSV() {
        thread {
            val state = Environment.getExternalStorageState()
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                return@thread
            }
            val root = applicationContext.getExternalFilesDir(null)
            val myDir = File("$root/exported_databases")
            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            //export EXPENSES database
            var filename = "expenses_${user}.csv"
            var file = File(myDir, filename)
            if (file.exists()) file.delete()
            val expenseDB = ExpenseListDatabase.getDatabase(applicationContext)
            val expenseItems = expenseDB.expenseItemDao().getAll(user)
            try {
                val out = FileOutputStream(file)
                file.printWriter().use { out ->
                    out.println("id,name,description,category,cost,is_expense,username,year,month,is_shared")
                    expenseItems.forEach {
                        out.println(
                            "${it.id},${it.name},${it.description},${it.category},${it.cost}," +
                                    "${it.isExpense},${it.username},${it.year},${it.month},${it.isShared}"
                        )
                    }
                }
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //export LOANS database
            filename = "loans_${user}.csv"
            file = File(myDir, filename)
            if (file.exists()) file.delete()
            val loanDB = LoanListDatabase.getDatabase(applicationContext)
            val loanItems = loanDB.loanItemDao().getAll(user)
            try {
                val out = FileOutputStream(file)
                file.printWriter().use { out ->
                    out.println("id,loaner_name,amount,description,username,is_loaner")
                    loanItems.forEach {
                        out.println(
                            "${it.id},${it.loanerName},${it.amount},${it.description}," +
                                    "${it.username},${it.isLoaner}"
                        )
                    }
                }
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*fun restoreExpenseDB(fileName: String) {
       thread {
           val inputStream: InputStream = File(fileName).inputStream()
           val lineList = mutableListOf<String>()
           inputStream.bufferedReader().forEachLine { lineList.add(it) }
           val expenseItems = mutableListOf<ExpenseItem>()
           lineList.forEach {
               val arguments = it.split(',')
               val expenseItem = ExpenseItem(
                   arguments[0].toLong(), arguments[1], arguments[2],
                   arguments[3], arguments[4].toInt(), arguments[5].toBoolean(), arguments[6],
                   arguments[7].toInt(), arguments[8].toInt(), arguments[9].toBoolean()
               )
               expenseItems.add(expenseItem)
           }
           if (expenseItems.size > 0) {
               val database = ExpenseListDatabase.getDatabase(applicationContext)
               database.expenseItemDao().clearTable()
               for (item in expenseItems)
                   database.expenseItemDao().insert(item)
           }
       }
   }*/

    /*fun restoreLoanDB(fileName: String) {
        thread {
            val inputStream: InputStream = File(fileName).inputStream()
            val lineList = mutableListOf<String>()
            inputStream.bufferedReader().forEachLine { lineList.add(it) }
            val loanItems = mutableListOf<LoanItem>()
            lineList.forEach {
                val arguments = it.split(',')
                val loanItem = LoanItem(
                    arguments[0].toLong(), arguments[0], arguments[0].toInt(), arguments[0],
                    arguments[0], arguments[0].toBoolean()
                )
                loanItems.add(loanItem)
            }
            if (loanItems.size > 0) {
                val database = LoanListDatabase.getDatabase(applicationContext)
                database.loanItemDao().clearTable()
                for (item in loanItems)
                    database.loanItemDao().insert(item)
            }
        }
    }*/

    /*fun backupDB() {
        thread {
            val myDir = File(applicationContext.filesDir, "backup")
            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            val calendar = Calendar.getInstance()
            val dateFormat = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-" +
                    "${calendar.get(Calendar.DAY_OF_MONTH)}_${calendar.get(Calendar.HOUR_OF_DAY)}-" +
                    "${calendar.get(Calendar.MINUTE)}"

            //export EXPENSES database
            var filename = "expenses_${user}_${dateFormat}.csv"
            var file = File(myDir, filename)
            if (file.exists()) file.delete()
            val expenseDB = ExpenseListDatabase.getDatabase(applicationContext)
            val expenseItems = expenseDB.expenseItemDao().getAll(user)
            try {
                val out = FileOutputStream(file)
                file.printWriter().use { out ->
                    expenseItems.forEach {
                        out.println(
                            "${it.id},${it.name},${it.description},${it.category},${it.cost}," +
                                    "${it.isExpense},${it.username},${it.year},${it.month},${it.isShared}"
                        )
                    }
                }
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //export LOANS database
            filename = "loans_${user}_${dateFormat}.csv"
            file = File(myDir, filename)
            if (file.exists()) file.delete()
            val loanDB = LoanListDatabase.getDatabase(applicationContext)
            val loanItems = loanDB.loanItemDao().getAll(user)
            try {
                val out = FileOutputStream(file)
                file.printWriter().use { out ->
                    loanItems.forEach {
                        out.println(
                            "${it.id},${it.loanerName},${it.amount},${it.description}," +
                                    "${it.username},${it.isLoaner}"
                        )
                    }
                }
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/
}