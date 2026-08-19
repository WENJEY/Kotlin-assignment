$ErrorActionPreference = "Stop"

$backend = $PSScriptRoot
$pidFile = Join-Path $backend "chat-servers.pid"
$ngrok = Join-Path $env:LOCALAPPDATA "Microsoft\WinGet\Packages\Ngrok.Ngrok_Microsoft.Winget.Source_8wekyb3d8bbwe\ngrok.exe"
$venvPython = Join-Path $backend "venv\Scripts\python.exe"

function Get-ListenerPid([int]$port) {
    $connection = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($connection) {
        return $connection.OwningProcess
    }
    return $null
}

if (Test-Path -LiteralPath $venvPython) {
    $python = $venvPython
} else {
    $pythonCmd = Get-Command python -ErrorAction SilentlyContinue
    if ($pythonCmd) {
        $python = $pythonCmd.Source
    } else {
        $pyCmd = Get-Command py -ErrorAction SilentlyContinue
        if ($pyCmd) {
            $python = $pyCmd.Source
        }
    }
}

if (-not $python) {
    throw "Python was not found. Create a venv in backend or install Python."
}

if (-not (Test-Path -LiteralPath $ngrok)) {
    $ngrokCmd = Get-Command ngrok -ErrorAction SilentlyContinue
    if ($ngrokCmd) {
        $ngrok = $ngrokCmd.Source
    } else {
        throw "ngrok.exe was not found. Reinstall ngrok first."
    }
}

$flaskPid = Get-ListenerPid 5000
if (-not $flaskPid) {
    $flaskProc = Start-Process -FilePath $python `
        -ArgumentList "`"$backend\app.py`"" `
        -WorkingDirectory $backend `
        -WindowStyle Hidden `
        -RedirectStandardOutput (Join-Path $backend "flask.log") `
        -RedirectStandardError (Join-Path $backend "flask.err.log") `
        -PassThru
    $flaskPid = $flaskProc.Id
    Start-Sleep -Seconds 2
    $listeningPid = Get-ListenerPid 5000
    if ($listeningPid) {
        $flaskPid = $listeningPid
    }
}

$ngrokPid = Get-ListenerPid 4040
if (-not $ngrokPid) {
    $ngrokProc = Start-Process -FilePath $ngrok `
        -ArgumentList "http 5000" `
        -WorkingDirectory $backend `
        -WindowStyle Hidden `
        -RedirectStandardOutput (Join-Path $backend "ngrok.log") `
        -RedirectStandardError (Join-Path $backend "ngrok.err.log") `
        -PassThru
    $ngrokPid = $ngrokProc.Id
    Start-Sleep -Seconds 2
    $listeningPid = Get-ListenerPid 4040
    if ($listeningPid) {
        $ngrokPid = $listeningPid
    }
}

@(
    "flask=$flaskPid"
    "ngrok=$ngrokPid"
) | Set-Content -LiteralPath $pidFile -Encoding ASCII
