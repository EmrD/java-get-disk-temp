$desktopPath = [Environment]::GetFolderPath("Desktop")
$outputFilePath = "$desktopPath\output.csv"

$disks = Get-Disk | Get-StorageReliabilityCounter

$output = @()
foreach ($disk in $disks) {
    $partitions = Get-Partition -DiskNumber $disk.DeviceId
    $diskName = $partitions | Where-Object { $_.DriveLetter } | Select-Object -First 1 -ExpandProperty DriveLetter
    if (-not $diskName) {
        $diskName = "Disk " + $disk.DeviceId
    }
    $output += [PSCustomObject]@{
        DiskName = $diskName
        Temperature = $disk.Temperature
    }
}

$output | Export-Csv -Path $outputFilePath -NoTypeInformation -Force
