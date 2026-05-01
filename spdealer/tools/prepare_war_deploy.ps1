Set-Location 'H:\DISCO_D\Desenvolvimento\Seprocom\spdealer'
New-Item -ItemType Directory -Path .\deploy -Force | Out-Null
Copy-Item -Path .\target\spdealer-1.0.0.war -Destination .\deploy\spdealer-1.0.0.war -Force
$fi = Get-Item .\deploy\spdealer-1.0.0.war
$hash = (Get-FileHash -Path $fi.FullName -Algorithm SHA256).Hash
"$($fi.Name) $($fi.Length) $hash" | Out-File .\deploy\deploy_manifest.txt -Encoding utf8
Write-Output "COPIED_AND_HASHED"