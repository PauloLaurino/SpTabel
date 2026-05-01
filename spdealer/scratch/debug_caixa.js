const mysql = require('mysql');

const connection = mysql.createConnection({
  host: '100.126.166.63',
  user: 'root',
  password: 'seprocom123@2024',
  database: 'erp'
});

connection.connect();

console.log('--- ESTRUTURA TABELA CAIXA ---');
connection.query("DESCRIBE caixa", function (error, results) {
  if (error) {
    console.error('ERROR DESCRIBE:', error);
  } else {
    results.forEach(row => {
      console.log(`${row.Field}: ${row.Type} (Null: ${row.Null}, Key: ${row.Key})`);
    });
  }

  console.log('\n--- AMOSTRA DE DADOS (TOP 5) ---');
  connection.query("SELECT * FROM caixa LIMIT 5", function (error, results) {
    if (error) {
      console.error('ERROR SELECT:', error);
    } else {
      results.forEach(row => {
        console.log(JSON.stringify(row));
      });
    }

    console.log('\n--- TESTE FILTRO DATA (2026-04-01 até 2026-04-22) ---');
    // Teste com CAST igual ao do Java
    const sqlTeste = "SELECT COUNT(*) as total FROM caixa WHERE CAST(dtmovi_cai AS UNSIGNED) >= 20260401 AND CAST(dtmovi_cai AS UNSIGNED) <= 20260422";
    connection.query(sqlTeste, function (error, results) {
      if (error) {
        console.error('ERROR TESTE CAST:', error);
      } else {
        console.log('Resultado do teste CAST numeric:', results[0].total);
      }
      
      connection.end();
    });
  });
});
