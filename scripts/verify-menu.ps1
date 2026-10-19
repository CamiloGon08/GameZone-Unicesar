param(
    [string]$ReferenceClasses,
    [string]$CandidateClasses = (Join-Path $PSScriptRoot '..\target\classes')
)

$ErrorActionPreference = 'Stop'
$CandidateClasses = (Resolve-Path -LiteralPath $CandidateClasses).Path
if ($ReferenceClasses) { $ReferenceClasses = (Resolve-Path -LiteralPath $ReferenceClasses).Path }
$evidenceDir = Join-Path $env:TEMP ('gamezone-menu-evidence-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $evidenceDir | Out-Null
$utf8 = [System.Text.UTF8Encoding]::new($false)
$inputLines = @(
    '1', 'G1', 'Game', '100', '3', 'PC', 'RPG', 'E',
    '2', 'C1', 'Console', '200', '2', 'Sony', 'PS5', '9',
    '3', '4', 'Ana', 'U1', '3001234567', 'a@b.co', '5', '6',
    '7', '1', '1', '1', '0', '8', '9', '1', '10', '1',
    '11', '4', '0', '12', '4', '5', '0', '13', '2', '0',
    '14', '2', '3', '4', '0', '0'
)

function Invoke-MenuScenario([string]$classes, [string]$name) {
    $scenarioDir = Join-Path $evidenceDir $name
    $dataDir = Join-Path $scenarioDir 'data'
    New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
    foreach ($file in @('products', 'persons', 'sales', 'accessories', 'promotions', 'returns', 'warranties')) {
        [System.IO.File]::WriteAllText((Join-Path $dataDir ($file + '.txt')), '', $utf8)
    }
    [System.IO.File]::WriteAllText((Join-Path $dataDir 'persons.txt'),
        "SELLER|S1|Luis|3001234568|EMP1|MORNING`n", $utf8)
    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = (Get-Command java).Source
    $startInfo.WorkingDirectory = $scenarioDir
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.StandardOutputEncoding = $utf8
    $startInfo.StandardErrorEncoding = $utf8
    foreach ($argument in @('-Dfile.encoding=UTF-8', '-Duser.language=en', '-Duser.country=US',
            '-cp', $classes, 'com.mycompany.gamezone.Main')) {
        $startInfo.ArgumentList.Add($argument)
    }
    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    [void]$process.Start()
    $outputTask = $process.StandardOutput.ReadToEndAsync()
    $errorTask = $process.StandardError.ReadToEndAsync()
    $process.StandardInput.Write(($inputLines -join "`n") + "`n")
    $process.StandardInput.Close()
    if (-not $process.WaitForExit(20000)) {
        $process.Kill($true)
        throw "Menu scenario timed out: $name"
    }
    $output = $outputTask.GetAwaiter().GetResult()
    $errors = $errorTask.GetAwaiter().GetResult()
    [System.IO.File]::WriteAllText((Join-Path $scenarioDir 'console.txt'), $output, $utf8)
    if ($process.ExitCode -ne 0 -or $errors) { throw "Menu scenario failed: $name $errors" }
    foreach ($marker in @('Videojuego registrado exitosamente.', 'Consola registrada exitosamente.',
            'Cliente registrado exitosamente.', 'Venta registrada exitosamente!',
            '--- Inventario de productos ---', '--- Clientes registrados ---',
            '--- Vendedores registrados ---', '--- Todas las ventas ---',
            '--- Ventas de Ana ---', '--- Ventas atendidas por Luis ---')) {
        if (-not $output.Contains($marker)) { throw "Missing expected output: $marker" }
    }
    return $scenarioDir
}

$candidateDir = Invoke-MenuScenario $CandidateClasses 'candidate'
if ($ReferenceClasses) {
    $referenceDir = Invoke-MenuScenario $ReferenceClasses 'reference'
    foreach ($relative in @('console.txt', 'data/products.txt', 'data/persons.txt', 'data/sales.txt',
            'data/accessories.txt', 'data/promotions.txt', 'data/returns.txt', 'data/warranties.txt')) {
        $candidateHash = (Get-FileHash -LiteralPath (Join-Path $candidateDir $relative) -Algorithm SHA256).Hash
        $referenceHash = (Get-FileHash -LiteralPath (Join-Path $referenceDir $relative) -Algorithm SHA256).Hash
        if ($candidateHash -ne $referenceHash) { throw "Behavior changed: $relative" }
    }
    Write-Output 'PASS: console output and seven data files match the reference byte for byte.'
}
Write-Output 'PASS: main operations 1-10 and read-only paths through submenus 11-14.'
Write-Output "Evidence: $evidenceDir"
