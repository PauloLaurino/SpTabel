
$fileContent = Get-Content 'C:\Program Files (x86)\Softwell Solutions\Maker Studio\Webrun Studio\tomcat\webapps\webrunstudio\classes\wfr\com\systems\system_ttb\forms\safiraformularioprincipal\Menu.xml' -Raw;
if ($fileContent -match '(?s)<PROPERTY KEY=\"XMLMenu\">(.*?)</PROPERTY>') {
    $b64 = $matches[1].Replace("`n", "").Replace("`r", "").Trim();
    try {
        $bytes = [System.Convert]::FromBase64String($b64);
        $stream = [System.IO.MemoryStream]::new($bytes);
        $stream.ReadByte(); $stream.ReadByte();
        $deflate = [System.IO.Compression.DeflateStream]::new($stream, [System.IO.Compression.CompressionMode]::Decompress);
        $reader = [System.IO.StreamReader]::new($deflate);
        $xml_encoded = $reader.ReadToEnd();
        
        # Native .NET URL Decode
        $xml = [uri]::UnescapeDataString($xml_encoded).Replace('+', ' ');

        $xml | Out-File -FilePath 'C:\Desenvolvimento\Seprocom\Notas\menu_decoded.xml' -Encoding utf8;
        Write-Host 'Menu decoded to C:\Desenvolvimento\Seprocom\Notas\menu_decoded.xml';
        Write-Host '--- XML CONTENT ---';
        Write-Host $xml;
    } catch {
        Write-Error $_.Exception.Message;
    }
} else {
    Write-Error 'Could not find XMLMenu in Menu.xml';
}
