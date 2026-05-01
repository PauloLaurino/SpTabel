const mysql = require('mysql2'); // Using mysql2 which is more common in some envs

const connection = mysql.createConnection({
  host: '100.126.166.63',
  user: 'root',
  password: 'seprocom123@2024',
  database: 'erp'
});

connection.connect();

connection.query("SHOW TABLES LIKE 'mas%'", function (error, results) {
  if (error) {
    console.error('ERROR:', error);
  } else {
    console.log('--- TABLES STARTING WITH mas ---');
    results.forEach(row => {
      console.log(Object.values(row)[0]);
    });
  }
  connection.end();
});
