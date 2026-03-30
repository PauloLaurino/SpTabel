# Limpa e compila
mvn clean compile
mvn dependency:copy-dependencies -DoutputDirectory=target/lib

# Configura ambiente para o ConnectionFactory (evita problemas de escape no shell)
$env:DB_URL = "jdbc:mysql://100.102.13.23:3306/sptabel?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASS = "k15720"

$cp = "target/classes;target/lib/*"
java -cp "$cp" "-Ddb.url=$env:DB_URL" "-Ddb.username=$env:DB_USER" "-Ddb.password=$env:DB_PASS" com.selador.util.ExecutarAmostraNotas
