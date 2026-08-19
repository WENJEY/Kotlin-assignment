$ErrorActionPreference = "SilentlyContinue"

$backend = $PSScriptRoot
$pidFile = Join-Path $backend "chat-servers.pid"
$pidsToStop = @()

if (Test-Path -LiteralPath $pidFile) {
    Get-Content -LiteralPath $pidFile | ForEach-Object {
        if ($_ -match "=\s*(\d+)\s*$") {
            $pidsToStop += [int]$Matches[1]
        }
    }
}

foreach ($port in 5000, 4040) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    foreach ($connection in $connections) {
        $pidsToStop += $connection.OwningProcess
    }
}

$pidsToStop = $pidsToStop | Where-Object { $_ -gt 0 } | Select-Object -Unique
foreach ($processId in $pidsToStop) {
    $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if ($proc -and ($proc.ProcessName -match "python|ngrok")) {
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
    }
}

Remove-Item -LiteralPath $pidFile -ErrorAction SilentlyContinue
