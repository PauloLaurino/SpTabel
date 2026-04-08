# Normaliza valores das verbas em arquivos JSON12 em outputs\
# Regras:
# - Se valor é inteiro (sem ponto) e >= 10, divide por 100 e arredonda para 2 casas.
# - Processa tanto formato compacto (ListaVerbas + verbas) quanto V11 (selo.verbas).

$outputs = Join-Path (Get-Location) 'outputs'
Get-ChildItem -Path $outputs -Filter "json12_*.json" -File | ForEach-Object {
    $file = $_.FullName
    $bak = "$file.bak"
    Copy-Item -Force $file $bak
    Write-Host "Processing: $($_.Name)"
    $raw = Get-Content -Raw -Path $file -ErrorAction Stop
    try {
        $obj = $raw | ConvertFrom-Json -ErrorAction Stop
    } catch {
        Write-Warning "  Failed to parse JSON: $($_.Name)"
        return
    }

    $changed = $false

    function NormalizeValue([ref]$val) {
        if ($val.Value -eq $null) { return }
        # Try parse any numeric-like value to decimal
        $num = $null
        if ($val.Value -is [string]) {
            if (-not [decimal]::TryParse($val.Value, [ref]$num)) { return }
        } else {
            try { $num = [decimal]$val.Value } catch { return }
        }
        # If number has no fractional part (e.g. 1892 or 1892.0) and is >= 10, interpret as centavos -> reais
        $intPart = [decimal][int]$num
        $hasFraction = ($num -ne $intPart)
        if (-not $hasFraction -and $num -ge 10) {
            $new = [math]::Round(($num / 100), 2)
            $val.Value = $new
            $changed = $true
        }
    }

    # Processar ListaVerbas se existir
    if ($obj.PSObject.Properties.Name -contains 'ListaVerbas') {
        $list = $obj.ListaVerbas
        if ($list -is [System.Array] -or $list -is [System.Collections.ArrayList]) {
            foreach ($item in $list) {
                if ($item.PSObject.Properties.Name -contains 'ValorVerba') {
                    $rv = [ref]$item.ValorVerba
                    NormalizeValue $rv
                    $item.ValorVerba = $rv.Value
                }
            }
        }
    }

    # Processar verbas no root
    if ($obj.PSObject.Properties.Name -contains 'verbas') {
        $verbas = $obj.verbas
        foreach ($p in $verbas.PSObject.Properties) {
            $rv = [ref]$verbas.$($p.Name)
            NormalizeValue $rv
            $verbas.$($p.Name) = $rv.Value
        }
    }

    # Processar formato V11: selo.verbas e selo.ListaVerbas
    if ($obj.PSObject.Properties.Name -contains 'selo') {
        $selo = $obj.selo
        if ($selo -ne $null) {
            if ($selo.PSObject.Properties.Name -contains 'ListaVerbas') {
                foreach ($item in $selo.ListaVerbas) {
                    if ($item.PSObject.Properties.Name -contains 'ValorVerba') {
                        $rv = [ref]$item.ValorVerba
                        NormalizeValue $rv
                        $item.ValorVerba = $rv.Value
                    }
                }
            }
            if ($selo.PSObject.Properties.Name -contains 'verbas') {
                foreach ($p in $selo.verbas.PSObject.Properties) {
                    $rv = [ref]$selo.verbas.$($p.Name)
                    NormalizeValue $rv
                    $selo.verbas.$($p.Name) = $rv.Value
                }
            }
        }
    }

    if ($changed) {
        # Escrever JSON com profundidade maior para preservar nested
        $out = $obj | ConvertTo-Json -Depth 10
        Set-Content -Path $file -Value $out -Encoding UTF8
        Write-Host "  Normalized and saved: $($_.Name)"
    } else {
        # Fallback: aplicar normalização via regex no texto (captura casos numéricos que o ConvertFrom-Json pode ter convertido)
        $text = Get-Content -Raw -Path $file

        $patternLista = '"ValorVerba"\s*:\s*([0-9]+(?:\.[0-9]+)?)'
        $newText = [System.Text.RegularExpressions.Regex]::Replace($text, $patternLista, {
            param($m)
            $orig = $m.Groups[1].Value
            if ($orig -match '\\.') {
                $decPart = $orig.Split('\.')[1]
                if ($decPart.Trim('0') -ne '') { return '"ValorVerba": ' + $orig }
            }
            $n = [decimal]$orig
            if ($n -ge 10) {
                $conv = '{0:0.00}' -f ($n/100)
                return '"ValorVerba": ' + $conv
            }
            return '"ValorVerba": ' + $orig
        })

        $patternVerbas = '"(emolumentos|funrejus|iss|fundep|funarpen|distribuidor|vrcExt|valorAdicional)"\s*:\s*([0-9]+(?:\.[0-9]+)?)'
        $newText = [System.Text.RegularExpressions.Regex]::Replace($newText, $patternVerbas, {
            param($m)
            $key = $m.Groups[1].Value
            $orig = $m.Groups[2].Value
            if ($orig -match '\\.') {
                $decPart = $orig.Split('\.')[1]
                if ($decPart.Trim('0') -ne '') { return '"' + $key + '": ' + $orig }
            }
            $n = [decimal]$orig
            if ($n -ge 10) {
                $conv = '{0:0.00}' -f ($n/100)
                return '"' + $key + '": ' + $conv
            }
            return '"' + $key + '": ' + $orig
        })

        if ($newText -ne $text) {
            $newText | Set-Content -Path $file -Encoding UTF8
            Write-Host "  Normalized (regex) and saved: $($_.Name)"
        } else {
            Write-Host "  No change needed: $($_.Name)"
        }
    }
}

Write-Host "Done."