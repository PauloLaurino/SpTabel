const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

function generateUUID() {
    return crypto.randomUUID();
}

function fixJrxml(filePath) {
    console.log(`Limpando UUIDs em: ${filePath}`);
    let content = fs.readFileSync(filePath, 'utf8');
    
    // Regex para achar padrões de UUID inválidos (que contém letras além de a-f)
    // Ou simplesmente substituir TODOS os uuid="..." por novos UUIDs válidos para garantir.
    const regex = /uuid="([^"]+)"/g;
    
    let matchCount = 0;
    const newContent = content.replace(regex, (match, p1) => {
        // Verifica se é hexadecimal puro (com hífens) e tem exatamente 36 caracteres
        const isHex = /^[0-9a-fA-F-]+$/.test(p1);
        if (!isHex || p1.length !== 36) {
            matchCount++;
            console.log(`  - Corrigindo UUID inválido ou longo: [${p1}]`);
            return `uuid="${generateUUID()}"`;
        }
        return match;
    });

    if (matchCount > 0) {
        fs.writeFileSync(filePath, newContent, 'utf8');
        console.log(`✅ ${matchCount} UUIDs corrigidos.`);
    } else {
        console.log('✨ Nenhum UUID inválido encontrado.');
    }
}

const reportsDir = 'src/main/resources/reports';
const files = fs.readdirSync(reportsDir).filter(f => f.endsWith('.jrxml'));

files.forEach(f => {
    fixJrxml(path.join(reportsDir, f));
});
