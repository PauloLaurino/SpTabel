import re

raw = '{"ambiente": "|ambiente|", "documentoResponsavel": "0", "codigoEmpresa": "|codigoEmpresa|", "codigoOficio": |codoficio|, "selo": {"seloDigital": "xyz"}}'

# Regex Java: ":\\s*([^\\\"\\d\\-\\{\\[\\stf\\n][^,}\\n]*)([\\s,}\\n])"
# Nota: em Python regex literal:
regex = r':\s*([^"\d\-{\[\stf\n][^,}\n]*)([\s,}\n])'

limpo = re.sub(regex, r': "\1"\2', raw)
print(f"Bruto: {raw}")
print(f"Limpo: {limpo}")
