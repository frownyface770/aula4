fun main(){
    val bugDB = BugLoader("src/bugReports.csv")
    bugDB.loadFile()
   // val bugData = bugDB.getData()
    //bugData.forEach { println("Title: ${it.title}\nDescription: ${it.description}\nSeverity:${it.severity}\nStatus: ${it.status}") }
    val filteredBugs = bugDB.filterBySeverity("MinorBug")
    //printBugList(filteredBugs)
    printBug(bugDB.getById("5"))
    printBug(bugDB.getById("3"))
    bugDB.updateStatus("3","Fixed")
    bugDB.updateStatus("5","Pending")
    printBug(bugDB.getById("5"))
    printBug(bugDB.getById("3"))
    bugDB.updateStatus("6","Fixdgsed")
    bugDB.updateDbFile()
    updateStatus(bugDB)
    printBug(bugDB.getById("1"))
}

fun printBugList(bugList: List<BugReport>) {
    bugList.forEach { printBug(it) }
}

//I refuse to write this print statement twice.
fun printBug(bug:BugReport?) {
    if (bug != null) {
        println("Title: ${bug.title}\nDescription: ${bug.description}\nSeverity:${bug.severity}\nStatus: ${bug.status}\n")
    } else {
        println("Can't print a null value.")
    }
}

fun updateStatus(db:BugLoader) {
    println("Enter a bug report id to change status(Int): ")
    val id =  readlnOrNull() ?: run {
        println("Invalid input!")
        return}
    //Logic duplication just so i can use the update method directly
    //Id rather have the update method of the bugreport class
    //Not be accessible outside but given the structure i chose im not
    //sure how to go about it
    val currentBug = db.getById(id) ?: run {
        println("Id not found")
        return
    }
    println("Current state ${currentBug.status}\nInput new state: ")
    val status = readlnOrNull() ?: run {
        println("Invalid input!")
        return
    }
    currentBug.updateState(status)

}