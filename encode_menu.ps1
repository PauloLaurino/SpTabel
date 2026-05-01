
$xml = Get-Content 'C:\Desenvolvimento\Seprocom\Notas\menu_decoded.xml' -Raw;

# 1. URL Encode (Maker style: spaces as %20, iso-8859-1)
# Note: UnescapeDataString was used in decode, so we use EscapeDataString.
# However, EscapeDataString is very aggressive. We need to match what Maker does.
# Maker usually encodes the whole XML.
$xml_encoded = [uri]::EscapeDataString($xml);

# 2. Convert to bytes (ISO-8859-1 as per the XML header)
$encoding = [System.Text.Encoding]::GetEncoding("iso-8859-1");
$bytes_to_compress = $encoding.GetBytes($xml_encoded);

# 3. Compress using Deflate
$ms = [System.IO.MemoryStream]::new();
# Add Zlib header (0x78 0x9C) - matches the 2-byte skip in decode
$ms.WriteByte(0x78);
$ms.WriteByte(0x9C);

$deflate = [System.IO.Compression.DeflateStream]::new($ms, [System.IO.Compression.CompressionLevel]::Optimal, $true);
$deflate.Write($bytes_to_compress, 0, $bytes_to_compress.Length);
$deflate.Close();

# 4. Base64
$final_bytes = $ms.ToArray();
$b64 = [System.Convert]::ToBase64String($final_bytes);

# 5. Update Menu.xml
$menuPath = 'C:\Program Files (x86)\Softwell Solutions\Maker Studio\Webrun Studio\tomcat\webapps\webrunstudio\classes\wfr\com\systems\system_ttb\forms\safiraformularioprincipal\Menu.xml';
$menuContent = Get-Content $menuPath -Raw;

# Replace the content inside <PROPERTY KEY="XMLMenu">...</PROPERTY>
# Using regex to find the XMLMenu property and replace its content
$newMenuContent = $menuContent -replace '(?s)(<PROPERTY KEY="XMLMenu">)(.*?)(</PROPERTY>)', ('${1}' + $b64 + '${3}')

[System.IO.File]::WriteAllText($menuPath, $newMenuContent, $encoding);

Write-Host "Menu.xml updated successfully!";
