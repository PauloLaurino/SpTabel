const mysql = require('mysql2/promise');

async function checkTable() {
    const connection = await mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: '',
        database: 'spdealer'
    });

    try {
        console.log('--- ESTRUTURA DA TABELA PAGAR ---');
        const [columns] = await connection.execute('DESCRIBE pagar');
        console.log(columns.map(c => c.Field).join(', '));

        console.log('\n--- DADOS DE EXEMPLO (TOP 3) ---');
        const [rows] = await connection.execute('SELECT tpcob_pag, tipodoc_pag FROM pagar LIMIT 3');
        console.log(JSON.stringify(rows, null, 2));

        console.log('\n--- DADOS DA FILIAL ---');
        const [filiais] = await connection.execute('SELECT * FROM filial LIMIT 1');
        console.log(JSON.stringify(filiais, null, 2));

    } catch (err) {
        console.error(err);
    } finally {
        await connection.end();
    }
}

checkTable();
