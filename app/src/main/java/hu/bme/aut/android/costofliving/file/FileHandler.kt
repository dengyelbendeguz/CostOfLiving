package hu.bme.aut.android.costofliving.file

import android.content.Context
import android.os.Environment
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
    private val expenseDBFieldNames = "id,name,description,category,cost,is_expense,username,year,month,is_shared"
    private val loansDBFieldNames = "id,loaner_name,amount,description,username,is_loaner"

    //for testing export/import
    /*fun addTestItem(){
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
            val filename = "expenses_${user}.csv"
            val file = File(myDir, filename)
            var test = "nice, and good"
            test = escapeComma(test)
            try {
                val out = FileOutputStream(file, true).bufferedWriter().use { out->
                    out.append("111,escaped item,${test},Food,111,TRUE,test,2022,4,FALSE\n")
                }
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    private fun escapeComma(rawString: String): String{
        // this is not elegant, but works
        // "d11c0182" is the CRC32 hash of "comma"
        var raw = rawString
        if (raw.contains(",")) {
            raw = raw.replace(",", "d11c0182")
        }
        return raw
    }

    private fun recoverComma(rawString: String): String{
        // this is not elegant, but works
        // "d11c0182" is the CRC32 hash of "comma"
        var raw = rawString
        if (raw.contains("d11c0182")) {
            raw = raw.replace("d11c0182", ",")
        }
        return raw
    }

    fun exportExpensesDBToCSV() {
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
            val filename = "expenses_${user}.csv"
            val file = File(myDir, filename)
            if (file.exists()) file.delete()
            val expenseDB = ExpenseListDatabase.getDatabase(applicationContext)
            val expenseItems = expenseDB.expenseItemDao().getAll(user)
            try {
                val out = FileOutputStream(file)
                file.printWriter().use { out ->
                    out.println(expenseDBFieldNames)
                    expenseItems.forEach {
                        it.name = escapeComma(it.name)
                        it.description = escapeComma(it.description)
                        it.category = escapeComma(it.category)
                        it.username = escapeComma(it.username)
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
        }
    }

    fun exportLoansDBToCSV() {
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
            val filename = "loans_${user}.csv"
            val file = File(myDir, filename)
            if (file.exists()) file.delete()
            val loanDB = LoanListDatabase.getDatabase(applicationContext)
            val loanItems = loanDB.loanItemDao().getAll(user)
            try {
                val out = FileOutputStream(file)
                file.printWriter().use { out ->
                    out.println(loansDBFieldNames)
                    loanItems.forEach {
                        it.loanerName = escapeComma(it.loanerName)
                        it.description = escapeComma(it.description)
                        it.username = escapeComma(it.username)
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

    fun importExpensesDBFromCSV() {
        thread {
            val state = Environment.getExternalStorageState()
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                return@thread
            }
            val root = applicationContext.getExternalFilesDir(null)
            val myDir = File("$root/exported_databases")
            val filename = "expenses_${user}.csv"
            val file = File(myDir, filename)
            if (!file.exists()) return@thread
            val expenseDB = ExpenseListDatabase.getDatabase(applicationContext)
            expenseDB.expenseItemDao().clearTable(user)
            try {
                val fis = FileInputStream(file)
                file.forEachLine {
                    if(it != expenseDBFieldNames){
                        val subStrings = it.split(',')
                        val expenseItem = ExpenseItem(
                            name = recoverComma(subStrings[1]),
                            description = recoverComma(subStrings[2]),
                            cost = subStrings[4].toInt(),
                            category =  recoverComma(subStrings[3]),
                            isExpense = subStrings[5].toBoolean(),
                            username = recoverComma(subStrings[6]),
                            year = subStrings[7].toInt(),
                            month = subStrings[8].toInt(),
                            isShared = subStrings[9].toBoolean()
                        )
                        expenseDB.expenseItemDao().insert(expenseItem)
                    }
                }
                fis.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importLoansDBFromCSV() {
        thread {
            val state = Environment.getExternalStorageState()
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                return@thread
            }
            val root = applicationContext.getExternalFilesDir(null)
            val myDir = File("$root/exported_databases")
            val filename = "loans_${user}.csv"
            val file = File(myDir, filename)
            if (!file.exists()) return@thread
            val loanDB = LoanListDatabase.getDatabase(applicationContext)
            loanDB.loanItemDao().clearTable(user)
            try {
                val fis = FileInputStream(file)
                file.forEachLine {
                    if(it != loansDBFieldNames){
                        val subStrings = it.split(',')
                        val loanItem = LoanItem(
                            loanerName = recoverComma(subStrings[1]),
                            description = recoverComma(subStrings[3]),
                            amount = subStrings[2].toInt(),
                            isLoaner = subStrings[5].toBoolean(),
                            username = recoverComma(subStrings[4])
                        )
                        loanDB.loanItemDao().insert(loanItem)
                    }
                }
                fis.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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