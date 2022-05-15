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
    private val BACKUP = "BACKUP"
    private val IMPORT = "IMPORT"
    private val EXPORT = "EXPORT"
    private val EXPENSES = "EXPENSES"
    private val LOANS = "LOANS"
    private val EXPORTED_DATABASES = "exported_databases"
    private val EXPENSES_BACKUPS  = "expenses_backups"
    private val LOANS_BACKUPS = "loans_backups"
    private val COMMA_HASH = "d11c0182" // "d11c0182" is the CRC32 hash of "comma"

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

    fun prepareIO(operation: String, dbName: String, user: String){
        //CREATING FILENAME
        val fileName: String
        if (operation == BACKUP){
            val calendar = Calendar.getInstance()
            val date = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-" +
                    "${calendar.get(Calendar.DAY_OF_MONTH)}"
            fileName = dbName+"_"+user+"_"+date+".csv"

        }else
            fileName = dbName+"_"+user+".csv"

        //CHOOSING DIRECTORY
        val directory: String
        if (operation != BACKUP)
            directory = EXPORTED_DATABASES
        else if (dbName == EXPENSES)
            directory = EXPENSES_BACKUPS
        else if (dbName == LOANS)
            directory = LOANS_BACKUPS
        else
            return

        //CHECKING STUFF
        val state = Environment.getExternalStorageState()
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return
        }
        val root = applicationContext.getExternalFilesDir(null)

        //CHECK IF DIRECTORY EXISTS
        val myDir = File("$root/$directory")
        if (operation != IMPORT)
            if (!myDir.exists())
                myDir.mkdirs()

        //FINAL FILE
        val file = File(myDir, fileName)

        if (operation == EXPORT)
            if (dbName == EXPENSES)
                exportExpensesDBToCSV(file)
            else
                exportLoansDBToCSV(file)
        else if (operation == IMPORT)
            if (dbName == EXPENSES)
                importExpensesDBFromCSV(file)
            else
                importLoansDBFromCSV(file)
        else if (operation == BACKUP)
                backupDBs(file, myDir, directory)
    }

    private fun escapeComma(rawString: String): String{
        var raw = rawString
        if (raw.contains(",")) {
            raw = raw.replace(",", COMMA_HASH)
        }
        return raw
    }

    private fun recoverComma(rawString: String): String{
        var raw = rawString
        if (raw.contains(COMMA_HASH)) {
            raw = raw.replace(COMMA_HASH, ",")
        }
        return raw
    }

    private fun exportExpensesDBToCSV(file: File) {
        thread {
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

    private fun exportLoansDBToCSV(file: File) {
        thread {
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

    private fun importExpensesDBFromCSV(file: File) {
        thread {
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

    private fun importLoansDBFromCSV(file: File) {
        thread {
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

    private fun backupDBs(file: File, directory: File, dirName: String){
        //IF DB ALREADY BACKED UP TODAY, DO NOTHING
        if (file.exists()) return

        //CHECK IF THERE ARE NO MORE THAN 10 BACKUPS (AND DELETES OLDEST ONE IF THERE ARE)
        val files: Array<File>? = directory.listFiles()
        var oldestDate = Long.MAX_VALUE
        var oldestFile: File? = null
        if (files != null && files.size >= 10) {
            files.forEach { f ->
                if (f.lastModified() < oldestDate) {
                    oldestDate = f.lastModified()
                    oldestFile = f
                }
            }
            oldestFile?.delete()
        }

        //FINALLY, WRITES THE FRESH BACKUP
        if (dirName == EXPENSES_BACKUPS)
            exportExpensesDBToCSV(file)
        else
            exportLoansDBToCSV(file)
    }
}