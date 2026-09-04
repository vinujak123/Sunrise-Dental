$libDir = "c:\xampp1\htdocs\Advanced Programming\SunriseDental\lib"

$deps = @(
    @{ Name = "junit-platform-console-standalone-1.10.1.jar"; Url = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar" },
    @{ Name = "mockito-core-5.7.0.jar"; Url = "https://repo1.maven.org/maven2/org/mockito/mockito-core/5.7.0/mockito-core-5.7.0.jar" },
    @{ Name = "byte-buddy-1.14.9.jar"; Url = "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.9/byte-buddy-1.14.9.jar" },
    @{ Name = "byte-buddy-agent-1.14.9.jar"; Url = "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.9/byte-buddy-agent-1.14.9.jar" },
    @{ Name = "objenesis-3.3.jar"; Url = "https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar" },
    @{ Name = "mockito-junit-jupiter-5.7.0.jar"; Url = "https://repo1.maven.org/maven2/org/mockito/mockito-junit-jupiter/5.7.0/mockito-junit-jupiter-5.7.0.jar" }
)

foreach ($dep in $deps) {
    $outFile = Join-Path $libDir $dep.Name
    if (-not (Test-Path $outFile)) {
        Write-Host "Downloading $($dep.Name)..."
        Invoke-WebRequest -Uri $dep.Url -OutFile $outFile
    } else {
        Write-Host "$($dep.Name) already exists."
    }
}
Write-Host "All dependencies downloaded."
