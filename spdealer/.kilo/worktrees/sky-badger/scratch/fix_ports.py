import sys
path = '/usr/local/tomcat10/conf/server.xml'
try:
    with open(path, 'r') as f:
        content = f.read()
    content = content.replace('port="8005"', 'port="8070"')
    content = content.replace('port="8080"', 'port="5070"')
    with open(path, 'w') as f:
        f.write(content)
    print('Ports updated successfully')
except Exception as e:
    print(f'Error: {e}')
    sys.exit(1)
