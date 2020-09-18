package licensedetector

import java.io.File
import java.nio.charset.Charset

/**
 * This class is used for license detection.
 * @param projectPathString path for project.
 */
class LicenseDetector (private val projectPathString: String) {

    companion object {
        private const val HEADER_MAX_SIZE = 32 * 1024
    }


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
     * read header specific size.
     */
    private fun readHeaderText(file: File): String {
        val stream = file.inputStream()
        val bytes = ByteArray(HEADER_MAX_SIZE)
        stream.read(bytes)
        stream.close()

        return bytes.toString(Charset.defaultCharset()).split('\u0000')[0]
    }

    /**
     * try to find a license in a particular file.
     */
    private fun tryFindLicense(file: File): ILicenseInfo {
        val headerText = readHeaderText(file)

        return when {
            headerText.startsWith("/*") -> {
                val purifiedHeaderText = headerText.removePrefix("/*")
                        .split("*/")[0]
                        .replace("*", "")

                tryFindLicenseInText(purifiedHeaderText, file.absolutePath)
            }
            headerText.startsWith("\"\"\"") -> {
                val purifiedHeaderText = headerText.removePrefix("\"\"\"").split("\"\"\"")[0]

                tryFindLicenseInText(purifiedHeaderText, file.absolutePath)
            }
            else -> tryFindLicenseInText(headerText, file.absolutePath)
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