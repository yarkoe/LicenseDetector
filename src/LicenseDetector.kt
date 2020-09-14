import java.io.File

/**
 * This class is used for license detection.
 * @param projectPathString path for project.
 */
class LicenseDetector (private val projectPathString: String) {

    private val slashCommentRegex = Regex("^\\/\\*((.|\\s)+)\\*\\/")
    private val tripleQuotesCommentRegex = Regex("^\"\"\"((.|\\s)+)\"\"\"")

    private val regexLicenseList = listOf(
            RegexLicense(LicenseType.Apache_2_0, licenseTextToRegex(apache2Text)),
            RegexLicense(LicenseType.MIT, licenseTextToRegex(mitText)),
            RegexLicense(LicenseType.GPL_3_0, licenseTextToRegex(gpl30Text)),
            RegexLicense(LicenseType.BSD_3_Clause, licenseTextToRegex(bsd3clauseText)),
            RegexLicense(LicenseType.LGPL_3_0, licenseTextToRegex(lgpl30Text))
    )

    /**
     * detect licenses in the project folder.
     */
    fun detect(): LicenseDetectorResult {

        val rootFolder = File(projectPathString)

        var mainLicenseInfo: ILicenseInfo = NullLicenseInfo()
        val licensesInfo = mutableListOf<LicenseInfo>()

        rootFolder.walk().filter{ it.isFile }.forEach{
            val licenseInfo = tryFindLicense(it)

            if (licenseInfo is LicenseInfo) {
                if (it.nameWithoutExtension.toUpperCase().contains("LICENSE") && it.parentFile == rootFolder)
                    mainLicenseInfo = licenseInfo

                licensesInfo.add(licenseInfo)
            }
        }

        return LicenseDetectorResult(mainLicenseInfo, licensesInfo)
    }

    /**
     * try to find a license in a particular file.
     */
    private fun tryFindLicense(file: File): ILicenseInfo{
        val fullText = file.readText()

        when {
            slashCommentRegex.containsMatchIn(fullText) -> {
                val matchResult = slashCommentRegex.find(fullText)

                val mainText = matchResult!!.groupValues[1].replace("*", "")

                return tryFindLicenseInText(mainText, file.absolutePath)
            }
            tripleQuotesCommentRegex.containsMatchIn(fullText) -> {
                val matchResult = tripleQuotesCommentRegex.find(fullText)

                val mainText = matchResult!!.groupValues[1]

                return tryFindLicenseInText(mainText, file.absolutePath)
            }
            else -> return tryFindLicenseInText(fullText, file.absolutePath)
        }
    }

    /**
     * try to find a license in a particular text.
     */
    private fun tryFindLicenseInText(text: String, textPath: String): ILicenseInfo {
        val regexLicense = regexLicenseList.firstOrNull{
            it.regex.containsMatchIn(text.replace(Regex("(\\s|\\n)+"), ""))
        }

        return if (regexLicense != null) LicenseInfo(text, regexLicense.licenseType, textPath)
        else NullLicenseInfo()
    }
}