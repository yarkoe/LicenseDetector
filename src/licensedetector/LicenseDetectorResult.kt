package licensedetector

data class LicenseDetectorResult (val mainLicenseInfo: ILicenseInfo, val licensesInfo: Iterable<LicenseInfo>)
