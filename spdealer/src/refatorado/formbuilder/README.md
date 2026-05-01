# FormBuilder — importação do formulário `masfor`

Arquivo criado para importação no FormBuilder Editor:

- [src/refatorado/formbuilder/masfor.form.json](src/refatorado/formbuilder/masfor.form.json)

Como importar no editor visual (`FormBuilderMain` / rota `/ferramentas/form-builder-editor`):

1. Abra o FormBuilder Editor em dev: `http://localhost:3000/ferramentas/form-builder-editor`.
2. No editor, procure a opção `Import` ou `Import JSON` (normalmente no menu de ações do editor).
3. Cole o conteúdo de `masfor.form.json` ou faça upload do arquivo.
4. Verifique a pré-visualização; ajuste labels/ordem/validações conforme necessário no editor.
5. Salve como novo formulário no sistema (ou exporte o JSON atualizado para gravar no repositório).

Observações:
- O JSON foi gerado a partir do `MasforForm.tsx` presente em `src/refatorado/frontend/masfor` e contém as colunas mínimas `codigo`, `descricao`, `ativo`.
- Campos `id` e `id_fil` estão marcados como `readOnly` na metadata; o editor pode mapear isso para hidden/readonly conforme suporte.
- Se o FormBuilder Editor do projeto exigir um formato específico diferente, posso adaptar o JSON — informe o schema esperado ou cole um exemplo exportado do editor.
