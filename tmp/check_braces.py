import sys
with open(r'c:\Desenvolvimento\Seprocom\Notas\src\main\java\com\selador\util\SeloJsonSanitizerNotas.java', 'r', encoding='utf-8') as f:
    content = f.read()
level = 0
in_str = False
in_comm = False
in_block_comm = False
for i, c in enumerate(content):
    if not in_comm and not in_block_comm:
        if c == '"' and (i == 0 or content[i-1] != '\\'): in_str = not in_str
        if not in_str:
            if c == '/' and i+1 < len(content) and content[i+1] == '/': in_comm = True
            elif c == '/' and i+1 < len(content) and content[i+1] == '*': in_block_comm = True
            elif c == '{': level += 1
            elif c == '}': 
                level -= 1
                if level < 0: print(f"UNDERFLOW na linha {content[:i+1].count('\n') + 1}")
    elif in_comm and c == '\n': in_comm = False
    elif in_block_comm and c == '*' and i+1 < len(content) and content[i+1] == '/': in_block_comm = False
print(f"Level final: {level}")
