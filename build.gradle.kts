// H:\DISCO_D\Desenvolvimento\JAVA\Seprocom\notas\build.gradle.kts
plugins {
    // Remova 'java' e 'application' daqui se eles já estiverem sendo aplicados pelo build.gradle.kts principal.
    // Se você usava Kotlin, mantenha 'kotlin("jvm")' aqui. Exemplo:
    // kotlin("jvm") version "1.9.23"
}

dependencies {
    // Dependência do tabula-java específica para o módulo Notas
    implementation("technology.tabula:tabula:1.0.6")

    // Adicione outras dependências que você usava no seu projeto anterior aqui.
    // Ex: implementation(libs.guava) se você ainda quiser usá-lo neste módulo.
}

application {
    // Defina a classe principal para o módulo Notas.
    // Ajuste o nome da classe e o pacote conforme seu código.
    mainClass = "com.seprocom.notas.NotasApplication" // Exemplo. Você precisará criar esta classe.
}

// Se havia um bloco 'kotlin { jvmToolchain(X) }', você pode removê-lo,
// pois o jvmToolchain já está definido no build.gradle.kts principal.