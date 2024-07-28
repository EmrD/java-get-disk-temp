$desktopPath = [Environment]::GetFolderPath("Desktop")
$outputFilePath = "$desktopPath\output.csv"
Get-Disk | Get-StorageReliabilityCounter | Select-Object -Property DeviceId, Temperature | Export-Csv -Path $outputFilePath -NoTypeInformation -Force