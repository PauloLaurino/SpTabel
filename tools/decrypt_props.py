import base64, hashlib, sys
from pathlib import Path
try:
    from Crypto.Cipher import DES
except Exception as e:
    print('MISSING_PYCRYPTO', e)
    sys.exit(2)

def derive(password, salt, iterations=1000, dk_len=16):
    out=b''
    prev=b''
    while len(out)<dk_len:
        md=hashlib.md5(prev+password+salt).digest()
        for _ in range(1,iterations):
            md=hashlib.md5(md).digest()
        out+=md
        prev=md
    return out[:dk_len]

master_path = Path('C:/ProgramData/spr/master.key')
props_path = Path('C:/ProgramData/spr/notas/db.properties')
if not master_path.exists():
    print('MASTER_NOT_FOUND'); sys.exit(3)
if not props_path.exists():
    print('PROPS_NOT_FOUND'); sys.exit(4)

master = master_path.read_text().strip().encode('utf-8')
props = props_path.read_text()
for line in props.splitlines():
    if '=' in line:
        k,v=line.split('=',1)
        v=v.strip()
        if v.startswith('ENC(') and v.endswith(')'):
            inner=v[4:-1]
            data=base64.b64decode(inner)
            salt=data[:8]
            ct=data[8:]
            keyiv=derive(master, salt, iterations=1000, dk_len=16)
            key=keyiv[:8]
            iv=keyiv[8:16]
            cipher=DES.new(key, DES.MODE_CBC, iv)
            pt=cipher.decrypt(ct)
            pad=pt[-1]
            pt2=pt[:-pad]
            print(f"{k}={pt2.decode('utf-8')}")
        else:
            print(f"{k}={v}")
