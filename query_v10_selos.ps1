$ErrorActionPreference = 'SilentlyContinue'
$conn = New-Object System.Data.Odbc.OdbcConnection
$conn.ConnectionString = "Driver={MariaDB ODBC 3.1 Driver};Server=100.102.13.23;Port=3306;Database=sptabel;UID=root;PWD=k15720"
try {
    $conn.Open()
    $cmd = $conn.CreateCommand()
    $cmd.CommandText = "SELECT s.ID, s.SELO, LEFT(s.JSON12,300) as JSON12_PREVIEW, p.DTVERSAO_FUNARPEN, s.DATAENVIO FROM selados s JOIN parametros p ON p.CODIGO_PAR = 1 WHERE s.JSON12 IS NOT NULL AND s.JSON12 != '' AND s.DATAENVIO > p.DTVERSAO_FUNARPEN LIMIT 10"
    $reader = $cmd.ExecuteReader()
    while($reader.Read()) {
        Write-Host "========================================"
        Write-Host "ID: $($reader['ID'])"
        Write-Host "SELO: $($reader['SELO'])"
        Write-Host "DATAENVIO: $($reader['DATAENVIO'])"
        Write-Host "DTVERSAO_FUNARPEN: $($reader['DTVERSAO_FUNARPEN'])"
        Write-Host "JSON12_PREVIEW: $($reader['JSON12_PREVIEW'])"
        Write-Host ""
    }
    $reader.Close()
    $conn.Close()
} catch {
    Write-Host "ERRO: $($_.Exception.Message)"
}