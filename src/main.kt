import licensedetector.*

fun printLicenseResult(licenseResult: LicenseDetectorResult) {
    if (licenseResult.mainLicenseInfo is LicenseInfo) {
        println("Main license: ")
        printLicenseInfo(licenseResult.mainLicenseInfo)
    }
    else {
        println("The project does not have the main license in his root.")
    }

    if (licenseResult.licensesInfo.count() != 0) {
        println("Licenses:")
        licenseResult.licensesInfo.forEach { printLicenseInfo(it) }
    }
    else {
        println("The project does not have any licenses.")
    }
}

fun printLicenseInfo(licenseInfo: LicenseInfo) {
    printLicenseType(licenseInfo.licenseType)
    println("Path: " + licenseInfo.fullPath)
}

fun printLicenseType(licenseType: LicenseType) {
    print("Type: ")
    when(licenseType) {
        LicenseType.Apache_2_0 -> println("Apache-2.0")
        LicenseType.MIT -> println("MIT")
        LicenseType.GPL_3_0 -> println("GPL-3.0")
        LicenseType.BSD_3_Clause -> println("BSD-3-Clause")
        LicenseType.LGPL_3_0 -> println("LGPL-3.0")
    }
}

fun main() {
    print("Enter the project path: ")
    val folderString = readLine()

    if (folderString != null) {
        val licenseResult = LicenseDetector(folderString).detect()

        printLicenseResult(licenseResult)
    }
}