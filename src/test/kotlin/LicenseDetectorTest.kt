package test

import licensedetector.LicenseDetector
import licensedetector.LicenseInfo
import licensedetector.LicenseType
import licensedetector.NullLicenseInfo
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse


class LicenseDetectorTest {

    @Test
    fun testLicenseDetectorSimple() {
        val licenseDetector = LicenseDetector(Paths.get("src\\test\\TestProject").toAbsolutePath().toString())

        val actualMainLicenseType = LicenseType.LGPL_3_0
        val actualLicenseTypes = listOf(
                LicenseType.GPL_3_0,
                LicenseType.Apache_2_0,
                LicenseType.LGPL_3_0
        )

        val testResults = licenseDetector.detect()
        val testLicenseTypes = testResults.licensesInfo.map{ it.licenseType }

        assertFalse(testResults.mainLicenseInfo is NullLicenseInfo)
        assertEquals((testResults.mainLicenseInfo as LicenseInfo).licenseType, actualMainLicenseType)

        assertEquals(testResults.licensesInfo.count(), 3)
        assertEquals(testLicenseTypes, actualLicenseTypes)
    }
}