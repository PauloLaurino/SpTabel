$content = Get-Content 'c:\Desenvolvimento\Seprocom\Protesto\Gerencial\src\main\java\com\seprocom\gerencial\service\NotasService.java' -Raw
$content = $content -replace '(?m)\r?\n\s*// Removido.*', ""
$content = $content -replace '\}\s*\}\s*$', "}"
[System.IO.File]::WriteAllText('c:\Desenvolvimento\Seprocom\Protesto\Gerencial\src\main\java\com\seprocom\gerencial\service\NotasService.java', $content, [System.Text.Encoding]::UTF8)
Write-Host "Arquivo corrigido"