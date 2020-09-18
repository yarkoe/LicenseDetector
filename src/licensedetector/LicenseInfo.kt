package licensedetector

interface ILicenseInfo

data class LicenseInfo(val license: String, val licenseType: LicenseType, val fullPath: String) : ILicenseInfo

class NullLicenseInfo : ILicenseInfo
