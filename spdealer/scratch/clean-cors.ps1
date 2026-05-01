$files = Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java"
foreach ($file in $files) {
    if (Select-String -Path $file.FullName -Pattern "@CrossOrigin") {
        Write-Host "Updating $($file.FullName)"
        $content = Get-Content $file.FullName
        $newContent = $content | Where-Object { $_ -notmatch "@CrossOrigin" -and $_ -notmatch "import org.springframework.web.bind.annotation.CrossOrigin" }
        $newContent | Set-Content $file.FullName
    }
}
