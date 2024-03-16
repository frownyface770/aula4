import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

open class BugReport( val id: String, val title: String, val description: String, val severity: String, var status: String) {
    //Do the filtering here returning a bugreport or null
    fun filterBySeverity(sevFilter:String):BugReport? {
         if (this.severity == sevFilter) {
             return this
         }
         return null
     }
    //Actually updates state here and return a success boolean
    fun updateState(status: String): Boolean {
        if (status == "Fixed" || status == "Pending") {
            this.status = status
            return true
        } else {
            return false
        }
    }

}

//Right now these might aswell be data classes, i think im supposed to right some methods for them
// but i can't be arsed tbh
class CriticalBug(id: String, title: String,  description: String, severity: String = "CriticalBug", status: String)
    : BugReport( id, title, description, severity,status) {

}
class MinorBug( id: String, title: String,  description: String, severity: String = "MinorBug", status: String)
    : BugReport(id, title, description, severity,status) {
}
class MajorBug(id: String, title: String, description: String, severity: String = "MajorBug", status: String)
    : BugReport(id, title, description, severity,status) {

}


class BugLoader(private val filename:String) {
    private val bugs = mutableListOf<BugReport>()

    fun loadFile() {
        val file = File(filename)
        val lines = file.readLines().drop(1)
        constructBug(lines)
        //return lines
    }
    //Doesn't seem right, it works but IDK
    private fun constructBug(bugList: List<String>) {
        bugList.forEach { line ->
            val sections = line.split(",")
            val (id, title, description, severity, status) = sections

            if (severity.trim() == "CriticalBug") {
                val bug = CriticalBug(id, title, description, severity, status)
                bugs.add(bug)
            } else if (severity.trim() == "MajorBug") {
                val bug = MajorBug(id, title, description, severity, status)
                bugs.add(bug)
            } else {
                val bug = MinorBug(id, title, description, severity, status)
                bugs.add(bug)
            }
        }
    }
    fun getData(): List<BugReport> {
        return bugs
    }
    //Delegate filtering to object IDK if this is correct
    fun filterBySeverity(severity: String):List<BugReport> {
        return bugs.mapNotNull { it.filterBySeverity(severity) }
    }

    fun updateStatus(id:String,newStatus: String){
        val bugToChange = bugs.firstOrNull { it.id == id }
        if (bugToChange == null) {
            println("Id not found!")
            return
        }
        //Let's check if value was changed. This is a roundabout way of doing input validation
        //And a bit cringe. IDK if I should do the input validation here or delegate it to the object as i have.
        //Also, might be easier/smarter if Status was an enum class.
        val success = bugToChange.updateState(newStatus)
        if (!success) {
            println("State change failed. Status remains ${bugToChange.status}")
            return
        } else {
            val index = bugs.indexOfFirst { it.id == bugToChange.id }
            bugs[index] = bugToChange
            println("Changing status of bug$id to $newStatus")
        }
    }
    //I'll allow nulls for now
    fun getById(id:String):BugReport? {
        return bugs.firstOrNull {it.id == id}
    }
    fun updateDbFile() {
       try {
           //Using buffered Writer here adds a buffer to the writing process which helps to reduce
           //actual disk writes by batching multiple write operations together
           //Doesn't matter for our 15 line use but, it's a good practice to do so
           BufferedWriter(FileWriter(filename)).use {writer ->
               writer.write("id,Title,Description,Severity,Status\n")

               bugs.forEach { bug ->
                   writer.write("${bug.id},${bug.title},${bug.description},${bug.severity},${bug.status}\n")
               }
           }
           println("Data successfully written to Database file.")
       } catch (e: IOException) {
           println("Error writing to file: ${e.message}")
       }
    }
}