# Analise Editor via FRZ

## Objetivo

Gerar uma documentacao tecnica, orientada a IA, a partir do arquivo FRZ selecionado localmente.

## Resumo Executivo

- Arquivo analisado: `Editor.frz`
- Caminho de origem: `C:\Users\caues\AppData\Local\Temp\frzdoc_Editor.frz`
- Data da geracao: `2026-04-13 15:06:47`
- Titulo identificado: `M003 - Editor`
- Tabela principal: ``
- Campos detectados: `0`
- Eventos detectados: `45`
- Regras detectadas: `31`
- Lookups detectados: `10`
- Acoes detectadas: `31`
- Submodulos detectados: `0`
- Score de migracao: `0`
- Prontidao: `baixa`

## 1. Formulario Principal

- `FRM_CODIGO = 464567896`
- `FRM_GUID = {ECC86353-9F5B-4696-BBDE-EFB5B09466B5}`
- `FRM_DESCRICAO = M003 - Editor`
- componente raiz: `745130` tipo `F`

### Propriedades relevantes do formulario

- `FRM_DESCRICAO = M003 - Editor`

## 2. Camada de Dados

- tabela principal: `-`

## 3. Arquitetura de Componentes

### Contagem por categoria

- `button`: `31`
- `component`: `119`
- `container`: `1`
- `form_root`: `1`
- `lookup`: `10`

### Componentes mais relevantes

- `BTATUALIZAIMG` [button] tipo `B` caminho `BTATUALIZAIMG`
- `BTATUALIZAIMG1` [button] tipo `B` caminho `BTATUALIZAIMG1`
- `BTATUALIZAIMG2` [button] tipo `B` caminho `BTATUALIZAIMG2`
- `BTATUMINI` [button] tipo `B` caminho `BTATUMINI`
- `btCalcCustas` [button] tipo `B` caminho `btCalcCustas`
- `btEDITORF` [button] tipo `B` caminho `btEDITORF`
- `btEDITORT` [button] tipo `B` caminho `btEDITORT`
- `btFOLHA` [button] tipo `B` caminho `btFOLHA`
- `btGRAVAR` [button] tipo `B` caminho `btGRAVAR`
- `btGRAVAR1` [button] tipo `B` caminho `btGRAVAR1`
- `BTIMPRIMIRLIVRO` [button] tipo `B` caminho `BTIMPRIMIRLIVRO`
- `BTIMPRIMIRTRASLADO` [button] tipo `B` caminho `BTIMPRIMIRTRASLADO`
- `btInconsistencia` [button] tipo `B` caminho `btInconsistencia`
- `btnGravarAto` [button] tipo `B` caminho `btnGravarAto`
- `btnQualAtoProv` [button] tipo `B` caminho `btnQualAtoProv`
- `btQUALIFICAR` [button] tipo `B` caminho `btQUALIFICAR`
- `BTRECIBO` [button] tipo `B` caminho `BTRECIBO`
- `btRecibo2` [button] tipo `B` caminho `btRecibo2`
- `btSAIR` [button] tipo `B` caminho `btSAIR`
- `btSAIRA` [button] tipo `B` caminho `btSAIRA`
- `btSAIRL` [button] tipo `B` caminho `btSAIRL`
- `btTRASLADO` [button] tipo `B` caminho `btTRASLADO`
- `MakerButton1` [button] tipo `B` caminho `MakerButton1`
- `MakerButton10` [button] tipo `B` caminho `MakerButton10`
- `MakerButton2` [button] tipo `B` caminho `MakerButton2`
- `MakerButton3` [button] tipo `B` caminho `MakerButton3`
- `MakerButton4` [button] tipo `B` caminho `MakerButton4`
- `MakerButton6` [button] tipo `B` caminho `MakerButton6`
- `MakerButton7` [button] tipo `B` caminho `MakerButton7`
- `MakerButton8` [button] tipo `B` caminho `MakerButton8`
- `MakerButton9` [button] tipo `B` caminho `MakerButton9`
- `MakerContainer1` [container] tipo `P` caminho `MakerContainer1`
- `ARVATOS` [lookup] tipo `T` fonte `Select
  minuta_ato.DESCRICAO_ATO,
  minuta_ato.PARENT_ATO,
  minuta_ato.ATO_ATO
From
  minuta_ato
Where
  minuta_ato.NIVEL_ATO <= 1

/*WHERE_ADD*/
Order By
  minuta_ato.CODIGO_LIV,
  minuta_ato.NIVEL_ATO,
  minuta_ato.DESCRICAO_ATO

/*ORDER_ADD*/` caminho `ARVATOS`
- `ARVELEMENTOS` [lookup] tipo `T` fonte `Select
  minuta_min.DESCRICAO_MIN,
  minuta_min.PARENT_MIN,
  minuta_min.ATO_MIN,
  minuta_min.ATO_ATO
From
  minuta_min
Where
  (minuta_min.NIVEL_MIN <> 0) And
  (minuta_min.MOSTRA_MIN = 'S') And
:FUNCAO_componente_lblMinuta IS NOT NULL` caminho `ARVELEMENTOS`
- `EDTCODSER` [lookup] tipo `K` fonte `Select
  servicos.CODIGO_SER,
  servicos.DESCR_SER
From
  servicos
Where
  servicos.SISTEMA_SER = 'N'


/*WHERE_ADD*/
/*ORDER_NEW*/` caminho `EDTCODSER`
- `edtref_cart_cid_cdibge` [lookup] tipo `K` fonte `Select
  cidade.CODIGO_CID,
  Concat(cidade.SIGLA_CID, ' - ', cidade.NOME_CID) As Descrição
From
  cidade


/*WHERE_NEW*/
/*ORDER_NEW*/` caminho `edtref_cart_cid_cdibge`
- `edtTipAtoLST` [lookup] tipo `K` fonte `Select
  tabtip.tip_codigo,
  tabtip.tip_descr
From
  tabtip
Where
  tabtip.tip_tipo = :funcao_componente_edtTipTipo


/*WHERE_ADD*/
/*ORDER_NEW*/` caminho `edtTipAtoLST`
- `edtTipNatLST` [lookup] tipo `K` fonte `Select
  tabtin.tin_codigo,
  tabtin.tin_descr,
  tabtin.tin_coddist
From
  tabtin


/*WHERE_NEW*/
/*ORDER_NEW*/` caminho `edtTipNatLST`
- `ldiEscrevente` [lookup] tipo `K` fonte `Select
  fr_usuario.USR_CODIGO,
  fr_usuario.USR_NOME
From
  fr_usuario


/*WHERE_NEW*/
/*ORDER_NEW*/` caminho `ldiEscrevente`
- `ldiJuramentado` [lookup] tipo `K` fonte `select 
  juramentado.codigo_jur,
  juramentado.nome_jur
from
  juramentado
/*WHERE_NEW*/
 order by juramentado.nome_jur 
/*ORDER_ADD*/` caminho `ldiJuramentado`

## 4. Eventos do Formulario

- Nenhum evento explicito foi serializado no formulario principal.

## 5. Eventos por Componente

### 745130

- tipo: `F`
- categoria: `form_root`
- caminho: `745130`
- evento `Ao Entrar` -> regra `M003 - Editor - Ao clicar - Botão Editor`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Ao clicar - Botão Editor`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormSetFocus:server`, `ebfFormSetVisible:server`, `ebfGetSessionAttribute:server`, `ebfRemoveSessionAttribute:server`, `ebfSetSessionAttribute:server`, `isEqual`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfSetSessionAttribute(s_editor, S, false) [Cria variavel de sessão]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, CKTODOS, 1) [CKTODOS = 1]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttempoato, ebfGetSessionAttribute(s_tempoato, false)) [Carrega Tempo]
IF m_protocolo é nulo ou vazio? => isNullOrEmpty(ebfGetSessionAttribute(m_protocolo, false))
  - THEN
    - CONNECTOR FlowConnector2
    - CONNECTOR FlowConnector3
    - IF uf = 'PR' => isEqual(ebfGetSessionAttribute(s_uf, false), PR)
      - THEN
        - EXPRESSION = ebfFormSetVisible(MDQTDE, false) [Esconder componente MDQTDE]
        - CONNECTOR FlowConnector1
        - EXPRESSION = ebfFormSetVisible(edtTraslado, false) [Esconder componente edtTranslado]
        - EXPRESSION = ebfFormSetVisible(TxTraslado, false) [Esconder componente TxTRANSLADO]
        - EXPRESSION = ebfFormSetVisible(btEDITORT, false) [Esconder componente btEDITORT]
        - EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, false) [Esconder componente TxFOLHADOLIVRO]
```
- evento `Ao Sair` -> regra `M003-Editor-Ao Sair`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Editor-Ao Sair`
  funcoes do fluxo: `ebfCloseForm:server`, `ebfConcat:server`
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1461`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1418`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
CALLRULE GER Remover Variaveis de Sessao
EXPRESSION = ebfCloseForm() [Fechar Formulario]
END
```

### ARVATOS

- tipo: `T`
- categoria: `lookup`
- caminho: `ARVATOS`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Clicar-Ato`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Campo Chave=;Campo Lista=;Protocolo={lstProtocolo}`, `Nome=M003-Editor-Ao Clicar-Ato`
  funcoes do fluxo: `ebfConcat:server`, `ebfFormChangeComponentValue:client`, `ebfFormGetComponentValue`, `ebfFormRefreshComponent:server`, `ebfFormSetEnabled:client`, `ebfFormSetLookupName`, `ebfGetElementFromList:server`, `ebfIndexOf:server`, `ebfReplace:server`, `ebfSearchSubstring:server`, `ebfSetSessionAttribute:server`, `ebfStopRuleExecution:server`, `isEqual`, `isNullOrEmpty`, `oprAdd`, `oprIf`, `oprNot`, `oprOr`, `oprSubtract`, `toLong`, `toString`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  fluxo tecnico:

```text
START
IF Campo Lista = NULL => isNullOrEmpty($Campo Lista)
  - THEN
    - END
  - ELSE
    - IF Protocolo é nulo ou vazio? => isNullOrEmpty($Protocolo)
      - THEN
        - EXPRESSION = ebfStopRuleExecution(Para iniciar um ato é necessário um protocolo sem partes já lançadas e ainda não Lavrado) [Não foi selecionado um protocolo]
        - REFERENCE FlowEnd3
      - ELSE
        - CALLRULE Ger Retornar Nomes das Partes
        - IF Ja tem partes lançadas? => oprOr(isGreater(ebfMapGetObject($Mapa, CONTADOR), 0), isEqual(ebfGetSessionAttribute(s_jalavrado, false), 1))
          - THEN
            - REFERENCE FlowExpression29
          - ELSE
            - CALLRULE GER Ler Minuta Pelo Ato (minuta_ato)
```

### ARVELEMENTOS

- tipo: `T`
- categoria: `lookup`
- caminho: `ARVELEMENTOS`
- evento `Ao Duplo Clicar` -> regra `M003-Editor-Ao Duplo Clicar-Elemento`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Campo Chave=;Campo Lista=`, `Nome=M003-Editor-Ao Duplo Clicar-Elemento`
  funcoes do fluxo: `ebfConcat:server`, `ebfFormChangeComponentValue:client`, `ebfFormOpenForm:client`, `ebfFormSetEnabled:client`, `ebfNewLine:server`, `ebfSetSessionAttribute:server`, `ebfStopRuleExecution:server`, `isEqual`, `isNullOrEmpty`, `oprAnd`, `oprOr`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtElemento, $Campo Chave) ['edtElemento - Campo Invisível...' = {Campo Chave}]
CALLRULE GER Remover Variaveis de Sessao
IF Campo Chave = NULO => isNullOrEmpty($Campo Chave)
  - THEN
    - EXPRESSION = ebfStopRuleExecution(Clique no Elemento Abaixo !!!) [Erro: Clique no Elemento Abaixo !!!]
    - CONNECTOR FlowRemark5
    - CONNECTOR FlowRemark4
    - END
  - ELSE
    - IF s_livro > 0 => oprAnd(isEqual(toLong(ebfGetSessionAttribute(s_livro, false)), 0))
      - THEN
        - IF 'Texto' = NULO => isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto))
          - THEN
            - EXPRESSION = ebfFormSetEnabled(btnGravarAto, false) [Desabilitar 'GRAVAR ATO']
            - EXPRESSION = ebfStopRuleExecution(Não existe nenhum Texto para o ATO) [Erro: Não existe nenhum Texto para o ATO]
```

### BTATUALIZAIMG

- tipo: `B`
- categoria: `button`
- caminho: `BTATUALIZAIMG`
- evento `Ao Clicar` -> regra `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Tipo='IMG'`, `Nome=M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`
  funcoes do fluxo: `ebfConcat:server`, `ebfFormRefreshComponent:server`, `ebfGetUserCode:server`, `ebfLength:server`, `ebfListLength:server`, `ebfReplaceAll:server`, `ebfSQLClose:server`, `ebfSQLExecuteQuery:server`, `ebfSQLExecuteUpdate:server`, `ebfSQLField:server`, `isEqual`, `oprAdd`, `oprIf`, `oprNot`, `oprOr`, `oprSubtract`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
EXPRESSION ConsultaPar = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION ConsultaLivro = ebfSQLExecuteQuery() [Consulta Tabela "livro"]
IF TIPO = IMG? => isEqual($Tipo, IMG)
  - THEN
    - EXPRESSION Diretorio = oprIf(isNullOrEmpty(ebfSQLField($ConsultaLivro, NOMEDOC_LIV)), ebfSQLField($ConsultaPar, CAMINHOIMGATO_PAR), ebfConcat(ebfSQLField($ConsultaPar, CAMINHOIMGATO_PAR), /, ebfSQLField($ConsultaLivro, NOMEDOC_LIV))) [Montar Diretório]
    - EXPRESSION = ebfSQLExecuteUpdate() [Executar Atualização "livro"]
    - EXPRESSION Diretorio = ebfConcat($Diretorio, /, $Protocolo) [Concatena caminho do livro  +  protocolo]
    - EXPRESSION Diretorio = ebfReplaceAll($Diretorio, \, /) [Trocar subsequencias]
    - CALLRULE M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade - Subfluxo
    - EXPRESSION QtdeImg = ebfListLength($ListaArquivos) [Obter Quantidade de Imagens no Diretório]
    - EXPRESSION = ebfSQLExecuteUpdate() [Esvaziar Tabela "imgatotemp"]
    - IF TIPO = IMG? => isEqual($Tipo, IMG)
      - THEN
        - WHILE Contador >= QtdeImg? => oprNot(isGreaterOrEqual($Contador, $QtdeImg))
          - EXPRESSION Contador = oprAdd($Contador, 1) [Contador + 1]
```

### BTATUALIZAIMG1

- tipo: `B`
- categoria: `button`
- caminho: `BTATUALIZAIMG1`
- evento `Ao Clicar` -> regra `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Tipo='PDF'`, `Nome=M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`
  funcoes do fluxo: `ebfConcat:server`, `ebfFormRefreshComponent:server`, `ebfGetUserCode:server`, `ebfLength:server`, `ebfListLength:server`, `ebfReplaceAll:server`, `ebfSQLClose:server`, `ebfSQLExecuteQuery:server`, `ebfSQLExecuteUpdate:server`, `ebfSQLField:server`, `isEqual`, `oprAdd`, `oprIf`, `oprNot`, `oprOr`, `oprSubtract`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
EXPRESSION ConsultaPar = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION ConsultaLivro = ebfSQLExecuteQuery() [Consulta Tabela "livro"]
IF TIPO = IMG? => isEqual($Tipo, IMG)
  - THEN
    - EXPRESSION Diretorio = oprIf(isNullOrEmpty(ebfSQLField($ConsultaLivro, NOMEDOC_LIV)), ebfSQLField($ConsultaPar, CAMINHOIMGATO_PAR), ebfConcat(ebfSQLField($ConsultaPar, CAMINHOIMGATO_PAR), /, ebfSQLField($ConsultaLivro, NOMEDOC_LIV))) [Montar Diretório]
    - EXPRESSION = ebfSQLExecuteUpdate() [Executar Atualização "livro"]
    - EXPRESSION Diretorio = ebfConcat($Diretorio, /, $Protocolo) [Concatena caminho do livro  +  protocolo]
    - EXPRESSION Diretorio = ebfReplaceAll($Diretorio, \, /) [Trocar subsequencias]
    - CALLRULE M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade - Subfluxo
    - EXPRESSION QtdeImg = ebfListLength($ListaArquivos) [Obter Quantidade de Imagens no Diretório]
    - EXPRESSION = ebfSQLExecuteUpdate() [Esvaziar Tabela "imgatotemp"]
    - IF TIPO = IMG? => isEqual($Tipo, IMG)
      - THEN
        - WHILE Contador >= QtdeImg? => oprNot(isGreaterOrEqual($Contador, $QtdeImg))
          - EXPRESSION Contador = oprAdd($Contador, 1) [Contador + 1]
```

### BTATUALIZAIMG2

- tipo: `B`
- categoria: `button`
- caminho: `BTATUALIZAIMG2`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao Clicar Botao Upload`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Ao Clicar Botao Upload`
  funcoes do fluxo: `ebfFormOpenForm:client`
  trecho `java` `java_client_wrapper` de `ebfFormOpenForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }
```
  trecho `java` `java_client_wrapper` de `ebfFormOpenForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }

    protected final void ebfFormOpenFormGroup() throws Exception {
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormOpenForm({77A67895-3A6E-4B7D-9C7C-D9393F557F14}) [Abrir Formulário "M003 - Editor - Documentos do Ato - Upload"]
END
```

### BTATUMINI

- tipo: `B`
- categoria: `button`
- caminho: `BTATUMINI`
- evento `Ao Clicar` -> regra `M003 - Editor - Exibir Miniaturas`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Texto={edttexto};Miniatura='true'`, `Nome=M003 - Editor - Exibir Miniaturas`
  funcoes do fluxo: `ebfFormRefreshComponent:server`, `ebfFormSetVisible:server`, `ebfGetElementFromList:server`, `ebfGroupBoxShowComponents:client`, `ebfHtmlRemoveChild`, `ebfReplace:server`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
IF Protocolo nulo ou vazio => isNullOrEmpty($Protocolo)
  - THEN
    - END
  - ELSE
    - IF Mostrar Miniaturas?
      - THEN
        - EXPRESSION = ebfGroupBoxShowComponents(MakerBevel1, false) [Oculta Moldura]
        - EXPRESSION = ebfFormSetVisible(divMiniatura, true) [Mostrar Componente "divMiniatura"]
        - EXPRESSION = ebfFormSetVisible(MakerButton4, false) [Esconde Botão "Miniaturas"]
        - EXPRESSION = ebfFormSetVisible(MakerButton7, true) [Mostrar Botão "Ocultar"]
        - EXPRESSION = ebfFormSetVisible(BTATUMINI, true) [Mostra Botão "Recarregar"]
        - EXPRESSION = ebfFormRefreshComponent(divMiniatura) [Atualizar "divMiniatura"]
        - EXPRESSION = ebfHtmlRemoveChild(ebfHtmlGetElementById(divMiniatura), ebfGetElementFromList(ebfHtmlChildNodes(ebfHtmlGetElementById(divMiniatura)), 1)) [Limpar Miniaturas]
        - CALLRULE M003 - Editor - Miniatura do PDF - Gerar PDF
        - CALLRULE GER - Gerar GUID - Generico
```

### btCalcCustas

- tipo: `B`
- categoria: `button`
- caminho: `btCalcCustas`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao Modificar Lista Tipo Ato`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Tipo='P';Protocolo={edtProtocolo};Servico={EDTCODSER};vlrdeclar={EDTVLRDECLAR_PR...`, `Nome=M003 - Editor - Ao Modificar Lista Tipo Ato`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormRefreshComponent:server`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
IF codigo_pro nulo ou vazio? => isNullOrEmpty($Protocolo)
  - THEN
    - CONNECTOR FlowConnector1
    - END
  - ELSE
    - CALLRULE C001 - Inserir Dados Automaticamente - fin_protocolo
    - CALLRULE C001 - Protocolo - Calcular Emolumentos
    - EXPRESSION = ebfFormRefreshComponent(grdCUSTAS1) [Atualizar Grade]
    - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTEMOLUMENTOS_PRO, $Emolumentos) [Alterar Valor do Componente]
    - EXPRESSION = ebfFormRefreshComponent(EDTEMOLUMENTOS_PRO) [Atualiza componente EMOLUMENTOS]
    - REFERENCE FlowEnd1
```

### btEDITORF

- tipo: `B`
- categoria: `button`
- caminho: `btEDITORF`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao clicar - Botão Editor`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Ao clicar - Botão Editor`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormSetFocus:server`, `ebfFormSetVisible:server`, `ebfGetSessionAttribute:server`, `ebfRemoveSessionAttribute:server`, `ebfSetSessionAttribute:server`, `isEqual`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfSetSessionAttribute(s_editor, S, false) [Cria variavel de sessão]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, CKTODOS, 1) [CKTODOS = 1]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttempoato, ebfGetSessionAttribute(s_tempoato, false)) [Carrega Tempo]
IF m_protocolo é nulo ou vazio? => isNullOrEmpty(ebfGetSessionAttribute(m_protocolo, false))
  - THEN
    - CONNECTOR FlowConnector2
    - CONNECTOR FlowConnector3
    - IF uf = 'PR' => isEqual(ebfGetSessionAttribute(s_uf, false), PR)
      - THEN
        - EXPRESSION = ebfFormSetVisible(MDQTDE, false) [Esconder componente MDQTDE]
        - CONNECTOR FlowConnector1
        - EXPRESSION = ebfFormSetVisible(edtTraslado, false) [Esconder componente edtTranslado]
        - EXPRESSION = ebfFormSetVisible(TxTraslado, false) [Esconder componente TxTRANSLADO]
        - EXPRESSION = ebfFormSetVisible(btEDITORT, false) [Esconder componente btEDITORT]
        - EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, false) [Esconder componente TxFOLHADOLIVRO]
```

### btEDITORT

- tipo: `B`
- categoria: `button`
- caminho: `btEDITORT`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao clicar - Botão Editor`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Ao clicar - Botão Editor`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormSetFocus:server`, `ebfFormSetVisible:server`, `ebfGetSessionAttribute:server`, `ebfRemoveSessionAttribute:server`, `ebfSetSessionAttribute:server`, `isEqual`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfSetSessionAttribute(s_editor, S, false) [Cria variavel de sessão]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, CKTODOS, 1) [CKTODOS = 1]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttempoato, ebfGetSessionAttribute(s_tempoato, false)) [Carrega Tempo]
IF m_protocolo é nulo ou vazio? => isNullOrEmpty(ebfGetSessionAttribute(m_protocolo, false))
  - THEN
    - CONNECTOR FlowConnector2
    - CONNECTOR FlowConnector3
    - IF uf = 'PR' => isEqual(ebfGetSessionAttribute(s_uf, false), PR)
      - THEN
        - EXPRESSION = ebfFormSetVisible(MDQTDE, false) [Esconder componente MDQTDE]
        - CONNECTOR FlowConnector1
        - EXPRESSION = ebfFormSetVisible(edtTraslado, false) [Esconder componente edtTranslado]
        - EXPRESSION = ebfFormSetVisible(TxTraslado, false) [Esconder componente TxTRANSLADO]
        - EXPRESSION = ebfFormSetVisible(btEDITORT, false) [Esconder componente btEDITORT]
        - EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, false) [Esconder componente TxFOLHADOLIVRO]
```

### btFOLHA

- tipo: `B`
- categoria: `button`
- caminho: `btFOLHA`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao clicar - Botão Folha`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Ao clicar - Botão Folha`
  funcoes do fluxo: `ebfFormSetFocus:server`, `ebfFormSetVisible:server`
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRTRASLADO, false) [Esconder Botão Imprimir Traslado]
EXPRESSION = ebfFormSetVisible(TxTraslado, false) [Esconder componente TxTraslado]
EXPRESSION = ebfFormSetVisible(btEDITORT, false) [Esconder componente btEDITORT]
EXPRESSION = ebfFormSetVisible(edtTraslado, false) [Esconder componente edtTraslado]
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRLIVRO, true) [Mostrar Botão Imprimir Folha do Livro]
EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, true) [Mostrar TxFolhaDoLivro]
EXPRESSION = ebfFormSetVisible(btEDITORF, true) [Mostrar componente btEDITORF]
EXPRESSION = ebfFormSetVisible(edtFolha, true) [Mostrar componente edtFolha]
EXPRESSION = ebfFormSetFocus(edtFolha) [Focar texto do edtFolha]
END
```

### btGRAVAR

- tipo: `B`
- categoria: `button`
- caminho: `btGRAVAR`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao clicar - Botão Gravar Dados`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};CodTab={};CodJur={};CodEsc={};Gratuidade={edtSeloGratui...`, `Nome=M003 - Editor - Ao clicar - Botão Gravar Dados`
  funcoes do fluxo: `ebfFormGetComponentValue`, `ebfFormGetLookupName`, `ebfSQLGetFieldFromForm:server`, `ebfSubstring:server`
  fluxo tecnico:

```text
START
CALLRULE GER Regravar Protocolo Minuta
CALLRULE GER Regravar Not1
END
```

### btGRAVAR1

- tipo: `B`
- categoria: `button`
- caminho: `btGRAVAR1`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao clicar - Botão Gravar Dados`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};CodTab={};CodJur={};CodEsc={};Gratuidade={edtSeloGratui...`, `Nome=M003 - Editor - Ao clicar - Botão Gravar Dados`
  funcoes do fluxo: `ebfFormGetComponentValue`, `ebfFormGetLookupName`, `ebfSQLGetFieldFromForm:server`, `ebfSubstring:server`
  fluxo tecnico:

```text
START
CALLRULE GER Regravar Protocolo Minuta
CALLRULE GER Regravar Not1
END
```

### BTIMPRIMIRLIVRO

- tipo: `B`
- categoria: `button`
- caminho: `BTIMPRIMIRLIVRO`
- evento `Ao Clicar` -> regra `M003-Editor-botao impressao`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Impressao='L'`, `Nome=M003-Editor-botao impressao`
  funcoes do fluxo: `ebfConcat:server`, `ebfReplaceAll:server`, `ebfSQLExecuteQuery:server`, `ebfSQLExecuteUpdate:server`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfReplaceAll` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  trecho `java` `java_runtime` de `ebfReplaceAll` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  fluxo tecnico:

```text
START
EXPRESSION ConsultaPar = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION ConsultaAto = ebfSQLExecuteQuery() [Abrir Consulta Tabela 'ato']
EXPRESSION Texto = ebfReplaceAll(ebfSQLField($ConsultaAto, TEXTO_ATO), ebfConcat(/, ebfSQLField($ConsultaPar, CONTEXTO_PAR), /components/tinymce5/imagens), ebfConcat(ebfFileBase(), components/tinymce5/imagens)) [Trocar Diretório para Exibir no RB]
EXPRESSION = ebfSQLExecuteUpdate() [Inserir Texto no Campo "IMPRIMIR_ATO"]
CALLRULE M003-Editor-Visualizar Impressao
END
```

### BTIMPRIMIRTRASLADO

- tipo: `B`
- categoria: `button`
- caminho: `BTIMPRIMIRTRASLADO`
- evento `Ao Clicar` -> regra `M003-Editor-botao impressao`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Impressao='T'`, `Nome=M003-Editor-botao impressao`
  funcoes do fluxo: `ebfConcat:server`, `ebfReplaceAll:server`, `ebfSQLExecuteQuery:server`, `ebfSQLExecuteUpdate:server`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfReplaceAll` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  trecho `java` `java_runtime` de `ebfReplaceAll` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  fluxo tecnico:

```text
START
EXPRESSION ConsultaPar = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION ConsultaAto = ebfSQLExecuteQuery() [Abrir Consulta Tabela 'ato']
EXPRESSION Texto = ebfReplaceAll(ebfSQLField($ConsultaAto, TEXTO_ATO), ebfConcat(/, ebfSQLField($ConsultaPar, CONTEXTO_PAR), /components/tinymce5/imagens), ebfConcat(ebfFileBase(), components/tinymce5/imagens)) [Trocar Diretório para Exibir no RB]
EXPRESSION = ebfSQLExecuteUpdate() [Inserir Texto no Campo "IMPRIMIR_ATO"]
CALLRULE M003-Editor-Visualizar Impressao
END
```

### btInconsistencia

- tipo: `B`
- categoria: `button`
- caminho: `btInconsistencia`
- evento `Ao Clicar` -> regra `Geral - Abrir Formulário`, tipo `ui`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Formulário='{D10CB527-8E3E-4CF4-865B-ECE7A069B973}'`, `Nome=Geral - Abrir Formulário`

### btnGravarAto

- tipo: `B`
- categoria: `button`
- caminho: `btnGravarAto`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Clicar-GRAVAR ATO`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={lstProtocolo};Texto={edttexto};Folha do Livro={edtFolha};Traslado={ed...`, `Nome=M003-Editor-Ao Clicar-GRAVAR ATO`
  funcoes do fluxo: `ebfFormRefreshComponent:server`, `ebfGetSessionAttribute:server`, `ebfStopRuleExecution:server`, `isGreater`, `isNullOrEmpty`, `l2sFlowRunWithMessage`
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfGetSessionAttribute` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`

```java
protected final Variant ebfFirebaseStopMonitoringData(Variant variant, Variant variant2) throws Exception {
        DatabaseReference databaseReference = (DatabaseReference)variant.getObject();
        TreeMap treeMap = (TreeMap)this.ebfGetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(true)).getObject();
        try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
```
  trecho `java` `java_runtime` de `ebfGetSessionAttribute` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`

```java
protected Variant ebfGetComponentProperty(Variant variant, Variant variant2, Variant variant3) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        Variant variant4 = this.ebfGetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), Variant.VARIANT_FALSE);
        if (variant3 != null && variant3.getString().length() > 0) {
            if (variant4.getObject() != null) {
                return new Variant(variant4.getObject());
            }
            if (wFRForm.getComponentByName(variant2.getString()) != null) {
                return new Variant(wFRForm.getComponentByName(variant2.getString()).g...
```
  fluxo tecnico:

```text
START
EXPRESSION Existe = ebfGetSessionAttribute(s_existeato, false) [Obter variavel de sessão s_existeato]
IF edtAto = NULO => isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto))
  - THEN
    - EXPRESSION = ebfStopRuleExecution(Minuta não definida para o Ato, caso o Ato ja tenha sido iniciado/editado, copie (CRTL+C), selecione uma minuta e cole (CRTL+V) e grave o texto.) [Erro: Escolha o Elemento]
    - END
  - ELSE
    - IF {edtprotocolo} <> NULO?
{Regravação} => isGreater(toLong(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, lstProtocolo)), 0)
      - THEN
        - EXPRESSION = ebfFormRefreshComponent(GDANOTACOES) [Atualizar GDANOTACOES]
        - EXPRESSION = l2sFlowRunWithMessage(Gravando Ato , Aguarde!, M003-Editor-Ao Clicar-GRAVAR ATO Servidor, ebfListParamsCreate($Protocolo, $Texto, $Folha do Livro, $Traslado, $Existe)) [Atualiza dados no editor]
        - END
      - ELSE
        - EXPRESSION = ebfStopRuleExecution(É necessario informar um PROTOCOLO para gravar ATO!) [Erro: Falta Protocolo!]
        - END
```

### btnQualAtoProv

- tipo: `B`
- categoria: `button`
- caminho: `btnQualAtoProv`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Clicar Qualificar Ato Provisório`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Texto={};Protocolo={edtProtocolo};Traslado={};Fim={};dtato={edtDataAto};NUMREC={...`, `Nome=M003-Editor-Ao Clicar Qualificar Ato Provisório`
  funcoes do fluxo: `ebfAppend:server`, `ebfConcat:server`, `ebfFormatDateTime:server`, `ebfFormChangeComponentValue:client`, `ebfFormGetComponentValue`, `ebfFormGetLookupName`, `ebfFormRefreshComponent:server`, `ebfGetElementFromList:server`, `ebfListParamsCreate:server`, `ebfReplaceAll:server`, `ebfSetSessionAttribute:server`, `ebfSQLGetFieldFromForm:server`, `ebfStopRuleExecution:server`, `ebfSubstring:server`, `ebfTrim:server`, `isEqual`, `isNullOrEmpty`, `oprAnd`, `oprNot`, `toLong`
  trecho `java` `java_runtime` de `ebfAppend` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1078`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
  trecho `java` `java_runtime` de `ebfAppend` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1035`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
IF CENSEC = É nulo ou vazio? => isNullOrEmpty(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo))
  - THEN
    - EXPRESSION = ebfStopRuleExecution(Falta definir o CENSEC) [Interrompe Fluxo]
    - END
  - ELSE
    - IF Tipo de Ato = É nulo ou vazio? => isNullOrEmpty(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST))
      - THEN
        - EXPRESSION = ebfStopRuleExecution(ebfConcat(Falta definir o Tipo de Ato para o, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo), -, ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST))) [Interrompe Fluxo]
        - REFERENCE FlowEnd2
      - ELSE
        - IF Procuração ou Natureza do Ato NÃO É nulo ou vazio => oprAnd(isEqual(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST), Escritura), isNullOrEmpty(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST)))
          - THEN
            - EXPRESSION = ebfStopRuleExecution(ebfConcat(Falta definir o Tipo de Ato para o, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo), -, ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST), -, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST))) [Interrompe Fluxo]
            - REFERENCE FlowEnd2
          - ELSE
```

### btQUALIFICAR

- tipo: `B`
- categoria: `button`
- caminho: `btQUALIFICAR`
- evento `Ao Clicar` -> regra `M003 - Editor - Atualiza Rascunho - Cliente`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Tipo='P';Protocolo={edtProtocolo}`, `Nome=M003 - Editor - Atualiza Rascunho - Cliente`
  funcoes do fluxo: `ebfFormGetLookupName`, `ebfGetSessionAttribute:server`, `ebfListParamsCreate:server`, `ebfSetSessionAttribute:server`, `ebfSQLGetFieldFromForm:server`, `ebfSubstring:server`, `l2sFlowRunWithMessage`, `toDouble`
  trecho `java` `java_runtime` de `ebfGetSessionAttribute` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`

```java
protected final Variant ebfFirebaseStopMonitoringData(Variant variant, Variant variant2) throws Exception {
        DatabaseReference databaseReference = (DatabaseReference)variant.getObject();
        TreeMap treeMap = (TreeMap)this.ebfGetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(true)).getObject();
        try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
```
  trecho `java` `java_runtime` de `ebfGetSessionAttribute` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`

```java
protected Variant ebfGetComponentProperty(Variant variant, Variant variant2, Variant variant3) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        Variant variant4 = this.ebfGetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), Variant.VARIANT_FALSE);
        if (variant3 != null && variant3.getString().length() > 0) {
            if (variant4.getObject() != null) {
                return new Variant(variant4.getObject());
            }
            if (wFRForm.getComponentByName(variant2.getString()) != null) {
                return new Variant(wFRForm.getComponentByName(variant2.getString()).g...
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfSetSessionAttribute(s_texto, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto), false) [Obtem valor do componente edttexto
cria variavel de sessão s_texto]
EXPRESSION = ebfSetSessionAttribute(s_folha, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtFolha), false) [Obtem valor do componente edtfolha cria variavel de sessão s_folha]
EXPRESSION = ebfSetSessionAttribute(s_traslado, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTraslado), false) [Obtem valor do componente edttraslado
cria variavel de sessão s_traslado]
EXPRESSION Escrevente = ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente) [Obtem valor da lista dinamica Responsavel]
EXPRESSION Juramentado = ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiJuramentado) [Obtem valor da lista dinamica Juramentado]
EXPRESSION Tabeliao = ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiTabeliao) [Obtem valor da lista dinamica Tabelião]
EXPRESSION UF = ebfSubstring(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_cart_cid_cdibge), 1, 2) [UF do Local da Lavratura de dados complementares (Revogação,Retificação ou Substabelecimento)]
EXPRESSION Lista = ebfListParamsCreate(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtMinuta), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto), CALLRULE GER Data para Extenso, $Escrevente, $Juramentado, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ed_LivroProtocolo), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ed_NroMatricula), $Tabeliao, toDouble(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTEMOLUMENTOS_PRO)), $UF, $Tipo, $Protocolo, ebfGetSessionAttribute(s_texto, false), ebfGetSessionAttribute(s_folha, false), ebfGetSessionAttribute(s_traslado, false), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtLivro1), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtfolha1), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtLetra1), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCodMinuta), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCodLiv), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCRECI), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, cbbDOI), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtNomCorr), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtFolhaUsa), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtQTIMOVEIS), toDouble(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTVLRDECLAR_PRO)), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_CNS), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtProcolo_seq_liv), toDouble(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTVLRAVALIACAO_PRO)), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtDtDistrib), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtImoveis), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiTabeliao), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTCODSER), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_Livro), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_letra), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_Folha), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_cart_nome), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_datalavra), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_cart_cid_cdibge), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtDISTRIBUICAO)) [Lista com elementos]
EXPRESSION = l2sFlowRunWithMessage(Atualizado Rascunho..., ALM 003 CAMPOS, ebfListParamsCreate($Tipo, $Protocolo, $Lista)) [Executar Fluxo com notificação
ALM 003 CAMPOS]
END
```

### btrecalcula

- tipo: `I`
- categoria: `component`
- caminho: `btrecalcula`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao Modificar Grade Custas`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Tipo='P';Protocolo={edtProtocolo};Servico={EDTCODSER};vlrdeclar={EDTVLRDECLAR_PR...`, `Nome=M003 - Editor - Ao Modificar Grade Custas`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormRefreshComponent:server`, `ebfSQLGetFieldFromForm:server`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  fluxo tecnico:

```text
START
CALLRULE C001 - Protocolo - Ao Modificar Grade - Calcular Base FRJ
CALLRULE C001 - Protocolo - Calcular Selos Grade Custas
CALLRULE C001 - Protocolo - Calcular Emolumentos
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTEMOLUMENTOS_PRO, $Emolumentos) [Alterar Valor do Componente]
EXPRESSION = ebfFormRefreshComponent(EDTEMOLUMENTOS_PRO) [Atualizar componente EMOLUMENTOS]
EXPRESSION = ebfFormRefreshComponent(grdCUSTAS1) [Atualizar Grade]
END
```

### BTRECIBO

- tipo: `B`
- categoria: `button`
- caminho: `BTRECIBO`
- evento `Ao Clicar` -> regra `F001 - Recibo - Abrir Relatorio Recibo`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=ID={EDTNUMREC}`, `Nome=F001 - Recibo - Abrir Relatorio Recibo`

### btRecibo2

- tipo: `B`
- categoria: `button`
- caminho: `btRecibo2`
- evento `Ao Clicar` -> regra `M003 - Editor - Gerar Recibo`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Tipo='P';Protocolo={edtProtocolo};Livro={edtNumeroLivro};Folha={edtNumeroFolha};...`, `Nome=M003 - Editor - Gerar Recibo`
  funcoes do fluxo: `ebfAppend:server`, `ebfSubstring:server`, `isMinor`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfAppend` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1078`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
  trecho `java` `java_runtime` de `ebfAppend` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1035`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
  trecho `java` `java_runtime` de `ebfSubstring` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`

```java
protected final Variant ebfSubstring(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        int n = string.length();
        int n2 = variant2.getLong().intValue() - 1;
        int n3 = n2 + variant3.getLong().intValue();
        n2 = n2 < 0 ? 0 : n2;
        int n4 = n3 = n3 > n ? n : n3;
        if (n2 > n || n2 >= n3) {
            return VariantPool.get("");
        }
        String string2 = "";
        try {
            string2 = string.substring(n2, n3);
```
  trecho `java` `java_runtime` de `ebfSubstring` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }

    protected final Variant ebfTextMD5(Variant variant) throws Exception {
```
  fluxo tecnico:

```text
START
EXPRESSION Data_rec = ebfAppend(ebfSubstring($Data, 7, 4), ebfSubstring($Data, 4, 2), ebfSubstring($Data, 1, 2)) [Trata data do recibo]
EXPRESSION Data_venc = ebfAppend(ebfSubstring($DtVenc, 7, 4), ebfSubstring($DtVenc, 4, 2), ebfSubstring($DtVenc, 1, 2)) [Trata data de vencimento]
IF Cliente é Nulo ou Vazio? => isNullOrEmpty($CodCli)
  - THEN
    - CALLRULE SweetAlert - Notificar - Botão Executar
    - CONNECTOR FlowConnector1
    - CONNECTOR FlowConnector2
    - END
  - ELSE
    - IF Data é Nulo ou Vazio? => isNullOrEmpty($Data_rec)
      - THEN
        - CALLRULE SweetAlert - Notificar - Botão Executar
        - REFERENCE FlowConnector2
      - ELSE
        - IF DtVenc é nulo ou vazio? => isNullOrEmpty($Data_venc)
```

### btSAIR

- tipo: `B`
- categoria: `button`
- caminho: `btSAIR`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Sair`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Editor-Ao Sair`
  funcoes do fluxo: `ebfCloseForm:server`, `ebfConcat:server`
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1461`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1418`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
CALLRULE GER Remover Variaveis de Sessao
EXPRESSION = ebfCloseForm() [Fechar Formulario]
END
```

### btSAIRA

- tipo: `B`
- categoria: `button`
- caminho: `btSAIRA`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Sair`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Editor-Ao Sair`
  funcoes do fluxo: `ebfCloseForm:server`, `ebfConcat:server`
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1461`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1418`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
CALLRULE GER Remover Variaveis de Sessao
EXPRESSION = ebfCloseForm() [Fechar Formulario]
END
```

### btSAIRL

- tipo: `B`
- categoria: `button`
- caminho: `btSAIRL`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Sair`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Editor-Ao Sair`
  funcoes do fluxo: `ebfCloseForm:server`, `ebfConcat:server`
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1461`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfCloseForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1418`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
CALLRULE GER Remover Variaveis de Sessao
EXPRESSION = ebfCloseForm() [Fechar Formulario]
END
```

### btTRASLADO

- tipo: `B`
- categoria: `button`
- caminho: `btTRASLADO`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao clicar - Botão Traslado`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Ao clicar - Botão Traslado`
  funcoes do fluxo: `ebfFormSetFocus:server`, `ebfFormSetVisible:server`
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetFocus` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRLIVRO, false) [Esconder Botão Imprimir Folha do Livro]
EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, false) [Esconder componente TxFolhaDoLivro]
EXPRESSION = ebfFormSetVisible(btEDITORF, false) [Esconder componente btEDITORF]
EXPRESSION = ebfFormSetVisible(edtFolha, false) [Esconder componente edtFolha]
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRTRASLADO, true) [Mostrar Botão Imprimir Traslado]
EXPRESSION = ebfFormSetVisible(TxTraslado, true) [Mostrar TxTraslado]
EXPRESSION = ebfFormSetVisible(btEDITORT, true) [Mostrar componente btEDITORT]
EXPRESSION = ebfFormSetVisible(edtTraslado, true) [Mostrar componente edtTraslado]
EXPRESSION = ebfFormSetFocus(edtTraslado) [Focar texto do edtTraslado]
END
```

### ed_textoAux

- tipo: `M`
- categoria: `component`
- caminho: `ed_textoAux`
- evento `Ao Modificar` -> regra `M003 - Ato - Alterar Texto Auxiliar`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Texto={ed_textoAux}`, `Nome=M003 - Ato - Alterar Texto Auxiliar`

### edtFolhaUsa

- tipo: `E`
- categoria: `component`
- caminho: `edtFolhaUsa`
- evento `Ao Sair` -> regra `M003 - Editor - Buscar ultima folha do livro`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=CodLivro={edtCodLiv};QtdeFolhas={edtFolhaUsa};FolhaInicial={edtfolha1};DtAto={ed...`, `Nome=M003 - Editor - Buscar ultima folha do livro`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormSetEnabled:client`, `ebfFormSetFocus:server`, `ebfSQLCommitTransaction:server`, `ebfSQLEOF:server`, `ebfSQLExecuteQuery:server`, `ebfSQLExecuteUpdate:server`, `ebfSQLField:server`, `ebfSQLNext:server`, `ebfStopRuleExecution:server`, `isEqual`, `isMinorOrEqual`, `isNullOrEmpty`, `oprAdd`, `oprNot`, `oprOr`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormSetEnabled` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetEnabled` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
IF QtdeFolhas É nulo ou Vazio? => oprOr(isNullOrEmpty($QtdeFolhas), isEqual($QtdeFolhas, 0))
  - THEN
    - EXPRESSION = ebfFormSetFocus(btInconsistencia) [Focar componente botão  inconsistencias]
    - EXPRESSION = ebfStopRuleExecution(Não foi informado quantidade de folhas) [Interromper Fluxo]
    - CONNECTOR FlowConnector7
    - CONNECTOR FlowConnector3
    - END
  - ELSE
    - IF DataAto É nulo ou vazio => isNullOrEmpty(ebfFormatDateTime($DtAto, dd/MM/yyyy))
      - THEN
        - EXPRESSION ConsultaInc = ebfSQLExecuteQuery() [Consulta Tabela "inconsistencia"]
        - WHILE Existem Registros? => ebfSQLEOF($ConsultaInc)
          - IF FOLHA? => isEqual(ebfSQLField($ConsultaInc, TIPO_INC), FOLHA)
            - THEN
              - IF QtdeFolhas foi Informado? => oprNot(oprOr(isNullOrEmpty($QtdeFolhas), isEqual($QtdeFolhas, 0)))
```

### edtpsq_Folha

- tipo: `E`
- categoria: `component`
- caminho: `edtpsq_Folha`
- evento `Ao Pressionar Tecla` -> regra `M003 - Editor - Ao Precionar Tecla - psq_Folha`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Alt=;Ctrl=;Shift=;Código da Tecla=;Caracter da Tecla=;Nome=;Livro={edtpsq_Livro...`, `Nome=M003 - Editor - Ao Precionar Tecla - psq_Folha`
  funcoes do fluxo: `isEqual`
  fluxo tecnico:

```text
START
IF Tecla Pressionada = CR? => isEqual($Código da Tecla, 13)
  - THEN
    - CALLRULE M003 - Editor - Ao Modificar - psq_folha
    - CONNECTOR FlowConnector1
    - END
  - ELSE
    - REFERENCE FlowEnd1
```

### EDTTEXTO1

- tipo: `M`
- categoria: `component`
- caminho: `EDTTEXTO1`
- evento `Ao Sair` -> regra `M003 - Editor - Ao Sair Componente Anotacao`, tipo `navegacao`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Anotacao={EDTTEXTO1}`, `Nome=M003 - Editor - Ao Sair Componente Anotacao`
  funcoes do fluxo: `ebfGridGetValue`, `isNullOrEmpty`
  fluxo tecnico:

```text
START
EXPRESSION ID = ebfGridGetValue(GDANOTACOES, ebfGridGetSelectedRow(GDANOTACOES), ID) [Obter ID da Anotação]
IF ID é Nulo ou Vazio? => isNullOrEmpty($ID)
  - THEN
    - CONNECTOR FlowConnector1
    - CONNECTOR FlowConnector2
    - CONNECTOR FlowConnector3
    - END
  - ELSE
    - IF Anotação é Nulo ou Vazio? => isNullOrEmpty($Anotacao)
      - THEN
        - REFERENCE FlowConnector2
      - ELSE
        - CALLRULE M003 - Editor - Ao Sair Componente Anotacao - Subfluxo
        - REFERENCE FlowEnd1
```

### edtTipAtoLST

- tipo: `K`
- categoria: `lookup`
- caminho: `edtTipAtoLST`
- evento `Ao Modificar` -> regra `M003 - Editor - Ao Modificar Lista TipodeAto`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Chave={edtTipAtoLST}`, `Nome=M003 - Editor - Ao Modificar Lista TipodeAto`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAto, $Chave) [Alterar Valor do Componente]
END
```

### edtTipNatLST

- tipo: `K`
- categoria: `lookup`
- caminho: `edtTipNatLST`
- evento `Ao Modificar` -> regra `M003 - Editor - Ao Modificar Lista TipodeNatureza`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Chave={edtTipNatLST}`, `Nome=M003 - Editor - Ao Modificar Lista TipodeNatureza`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNat, $Chave) [Alterar Valor do Componente]
END
```

### GDANOTACOES

- tipo: `G`
- categoria: `component`
- caminho: `GDANOTACOES`
- evento `Ao Modificar` -> regra `M003 - Editor - Grade Anotação - Atualizar texto`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Linha=`, `Nome=M003 - Editor - Grade Anotação - Atualizar texto`
  funcoes do fluxo: `ebfFormChangeComponentValue:client`, `ebfFormSetEnabled:client`, `ebfGetFormResultset:server`, `ebfResultSetAbsolute:server`, `ebfSQLExecuteQuery:server`
  trecho `java` `java_runtime` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
  trecho `java` `java_client_wrapper` de `ebfFormChangeComponentValue` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  trecho `java` `java_runtime` de `ebfFormSetEnabled` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetEnabled` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
EXPRESSION Tabela = ebfGetFormResultset({6F4DCC6B-8867-40ED-889A-AD9636EDA508}) [Obter tabela do formulario]
EXPRESSION = ebfResultSetAbsolute($Tabela, $Linha) [Posiciona registro]
EXPRESSION Tabela = ebfSQLExecuteQuery() [Consulta tabela "atoanotacoes"]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTTEXTO1, ebfSQLField($Tabela, TEXTO)) [Atualiza o componente texto da anotação]
EXPRESSION = ebfFormSetEnabled(EDTTEXTO1, true) [Habilita Lembrete]
END
```

### GRDIMG

- tipo: `G`
- categoria: `component`
- caminho: `GRDIMG`
- evento `Ao Duplo Clicar` -> regra `M003 - Editor - Ao Duplo Clicar GRDIMG`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Linha=`, `Nome=M003 - Editor - Ao Duplo Clicar GRDIMG`
  funcoes do fluxo: `ebfConcat:server`, `ebfFormGetComponentValue`, `ebfGridGetValue`, `ebfLength:server`, `ebfRichTextInsertTextAtPosition`, `ebfSubstringInverse:server`, `isEqual`, `oprIf`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
CALLRULE M003 - Editor - Ao Duplo Clicar GRDIMG - Subfluxo
EXPRESSION Caminho = ebfGridGetValue(GRDIMG, $Linha, Caminho) [Obter Caminho da Imagem]
EXPRESSION CaminhoTMCE = ebfConcat(/, ebfGetElementFromList($ListaPar, 2), /components/tinymce5/imagens, ebfSubstringInverse($Caminho, oprSubtract(ebfLength($Caminho), ebfLength(ebfGetElementFromList($ListaPar, 1))))) [Obter Caminho a ser Utilizado na Tag <img>]
IF Centralizar? => isEqual(ebfGridGetValue(GRDIMG, $Linha, Centralizar), 1)
  - THEN
    - CONNECTOR FlowConnector1
    - EXPRESSION Tag = oprIf(isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA)), ebfConcat(<center><img src=", $CaminhoTMCE, " alt="" style="max-width:100%; max-height:100%;"></center><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">), ebfConcat(<center><img src=", $CaminhoTMCE, " alt="" style="width:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTLARGURA), px; height:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA), px;"></center><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">)) [Monta tag <img>]
    - CONNECTOR FlowConnector2
    - EXPRESSION = ebfRichTextInsertTextAtPosition(edttexto, $Tag) [Inserir Tag <img> no Componente "EDTTEXTO"]
    - END
  - ELSE
    - EXPRESSION Tag = oprIf(isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA)), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="max-width:100%; max-height:100%;"><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="width:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTLARGURA), px; height:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA), px;"><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">)) [Monta tag <img>]
    - REFERENCE FlowExpression5
```

### GRDPDF

- tipo: `G`
- categoria: `component`
- caminho: `GRDPDF`
- evento `Ao Duplo Clicar` -> regra `M003 - Editor - Download Arquivos do Ato`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Linha=;Protocolo={edtProtocolo}`, `Nome=M003 - Editor - Download Arquivos do Ato`
  funcoes do fluxo: `ebfGetComponentValueFromOtherForm`, `ebfGridGetValue`
  fluxo tecnico:

```text
START
EXPRESSION Caminho = ebfGridGetValue(GRDPDF, $Linha, Caminho) [Obter Caminho do Arquivo]
EXPRESSION Nome = ebfGridGetValue(GRDPDF, $Linha, Arquivo) [Obter Nome do Arquivo]
CALLRULE M003 - Editor - Download Arquivos do Ato - Subfluxo
END
```

### GRSelos

- tipo: `G`
- categoria: `component`
- caminho: `GRSelos`
- evento `Ao Clicar` -> regra `M003 - Editor - Grade Selos gerados`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003 - Editor - Grade Selos gerados`
  funcoes do fluxo: `ebfChangeComponentValueOtherForm`, `ebfRefreshComponentOtherForm`
  fluxo tecnico:

```text
START
EXPRESSION = ebfChangeComponentValueOtherForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtselo, ebfGridGetValue(GRSelos, ebfGridGetSelectedRow(GRSelos), Chavedigital)) [Obtem o Selo da GRID]
EXPRESSION = ebfRefreshComponentOtherForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtselo) [Atualiza componente edtselo]
END
```

### lstProtocolo

- tipo: `K`
- categoria: `lookup`
- caminho: `lstProtocolo`
- evento `Ao Modificar` -> regra `M003-Lista Protocolo-Altera valor do protocolo Cliente`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Lista Protocolo-Altera valor do protocolo Cliente`

### MakerButton1

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton1`
- evento `Ao Clicar` -> regra `M003 - Editor - Importar todas as imagens`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Altura={EDTALTURA};Largura={EDTLARGURA}`, `Nome=M003 - Editor - Importar todas as imagens`
  funcoes do fluxo: `ebfConcat:server`, `ebfLength:server`, `ebfSQLEOF:server`, `ebfSQLExecuteQuery:server`, `ebfSQLNext:server`, `ebfSubstringInverse:server`, `isEqual`, `oprIf`, `oprOr`, `oprSubtract`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfLength` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
  trecho `java` `java_runtime` de `ebfLength` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
  fluxo tecnico:

```text
START
EXPRESSION Parametros = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION Consulta = ebfSQLExecuteQuery() [Le imagens]
WHILE Existem Registros? => ebfSQLEOF($Consulta)
  - IF Extensão JPG? => oprOr(isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), jpg), isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), JPEG), isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), jpeg), isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), JPG))
    - THEN
      - EXPRESSION CaminhoTMCE = ebfConcat(/, ebfSQLField($Parametros, CONTEXTO_PAR), /components/tinymce5/imagens, ebfSubstringInverse(ebfSQLField($Consulta, ARQUIVO_IAT), oprSubtract(ebfLength(ebfSQLField($Consulta, ARQUIVO_IAT)), ebfLength(ebfSQLField($Parametros, CAMINHOIMGATO_PAR))))) [Obter Caminho a ser Utilizado na Tag <img>]
      - EXPRESSION Tag = oprIf(isNullOrEmpty($Altura), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="max-width:100%; max-height:100%;"><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="width:, $Largura, px; height:, $Altura, px;">)) [Monta tag <img>]
      - CALLRULE M003 - Editor - Importar todas as imagens - inserir no texto
      - EXPRESSION = ebfSQLNext($Consulta) [Proximo Registro]
    - ELSE
      - CONNECTOR FlowConnector1
      - REFERENCE FlowExpression5
END
```

### MakerButton10

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton10`
- evento `Ao Clicar` -> regra `M003 - Editor - Ao Clicar Botao Abrir Protocolo`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={lstProtocolo}`, `Nome=M003 - Editor - Ao Clicar Botao Abrir Protocolo`
  funcoes do fluxo: `ebfConcat:server`
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  trecho `java` `java_runtime` de `ebfConcat` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  fluxo tecnico:

```text
START
CALLRULE Abrir Formulario Filtrado - Generico
END
```

### MakerButton2

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton2`
- evento `Ao Clicar` -> regra `M003-Editor-Atualizar RELACAO DE ATOS`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Editor-Atualizar RELACAO DE ATOS`
  funcoes do fluxo: `ebfFormRefreshComponent:server`, `ebfFormSetEnabled:client`, `oprIf`
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetEnabled` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetEnabled` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormRefreshComponent(ARVATOS) [Atualizar 'RELAÇÃO DE ATOS']
EXPRESSION = ebfFormRefreshComponent(ARVELEMENTOS) [Atualizar 'RELAÇÃO DE ELEMENTOS']
EXPRESSION = oprIf(isNullOrEmpty(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto)), ebfFormSetEnabled(btnGravarAto, false), ebfFormSetEnabled(btnGravarAto, true)) [Habilitar/Desabilitar 'GRAVAR ATO']
EXPRESSION = oprIf(isNullOrEmpty(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtNumeroLivro)), ebfFormSetEnabled(btnQualAtoProv, false), ebfFormSetEnabled(btnQualAtoProv, true)) [Habilitar/Desabilitar LAVRAR ATO']
END
```

### MakerButton3

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton3`
- evento `Ao Clicar` -> regra `M003 - Editor - Abrir Form C001 Filtrado`, tipo `ui`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={lstProtocolo}`, `Nome=M003 - Editor - Abrir Form C001 Filtrado`
  funcoes do fluxo: `ebfFormOpenFilteredForm:server`, `ebfSetSessionAttribute:server`
  trecho `java` `java_runtime` de `ebfFormOpenFilteredForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3772`

```java
protected final Variant ebfFormOpenFilteredForm(Variant variant, Variant variant2) throws Exception {
        this.client.openFilteredForm(this.manager.getSys().getFormByGuid(variant.getString()), variant2.toString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormOpenFilteredForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3702`

```java
protected final Variant ebfFormOpenFilteredForm(Variant variant, Variant variant2) throws Exception {
        this.client.openFilteredForm(this.manager.getSys().getFormByGuid(variant.getString()), variant2.toString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfSetSessionAttribute` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
  trecho `java` `java_runtime` de `ebfSetSessionAttribute` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfSetSessionAttribute(abaimovel, true, false) [Definir Variável da Sessão 'abaimovel']
EXPRESSION = ebfFormOpenFilteredForm({28ADA629-4C27-4F89-857E-F433304D64B8}, ebfConcat(protocolo.codigo_pro=, $Protocolo)) [Abrir Formulário Filtrado]
END
```

### MakerButton4

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton4`
- evento `Ao Clicar` -> regra `M003 - Editor - Exibir Miniaturas`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Texto={edttexto};Miniatura='true'`, `Nome=M003 - Editor - Exibir Miniaturas`
  funcoes do fluxo: `ebfFormRefreshComponent:server`, `ebfFormSetVisible:server`, `ebfGetElementFromList:server`, `ebfGroupBoxShowComponents:client`, `ebfHtmlRemoveChild`, `ebfReplace:server`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
IF Protocolo nulo ou vazio => isNullOrEmpty($Protocolo)
  - THEN
    - END
  - ELSE
    - IF Mostrar Miniaturas?
      - THEN
        - EXPRESSION = ebfGroupBoxShowComponents(MakerBevel1, false) [Oculta Moldura]
        - EXPRESSION = ebfFormSetVisible(divMiniatura, true) [Mostrar Componente "divMiniatura"]
        - EXPRESSION = ebfFormSetVisible(MakerButton4, false) [Esconde Botão "Miniaturas"]
        - EXPRESSION = ebfFormSetVisible(MakerButton7, true) [Mostrar Botão "Ocultar"]
        - EXPRESSION = ebfFormSetVisible(BTATUMINI, true) [Mostra Botão "Recarregar"]
        - EXPRESSION = ebfFormRefreshComponent(divMiniatura) [Atualizar "divMiniatura"]
        - EXPRESSION = ebfHtmlRemoveChild(ebfHtmlGetElementById(divMiniatura), ebfGetElementFromList(ebfHtmlChildNodes(ebfHtmlGetElementById(divMiniatura)), 1)) [Limpar Miniaturas]
        - CALLRULE M003 - Editor - Miniatura do PDF - Gerar PDF
        - CALLRULE GER - Gerar GUID - Generico
```

### MakerButton6

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton6`
- evento `Ao Clicar` -> regra `M003-Editor-Ao Clicar-PARTES`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Nome=M003-Editor-Ao Clicar-PARTES`
  funcoes do fluxo: `ebfFormOpenForm:client`
  trecho `java` `java_client_wrapper` de `ebfFormOpenForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }
```
  trecho `java` `java_client_wrapper` de `ebfFormOpenForm` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }

    protected final void ebfFormOpenFormGroup() throws Exception {
```
  fluxo tecnico:

```text
START
EXPRESSION = ebfFormOpenForm({0EE5C3E9-873C-4972-A68A-3933054206FF}) [Abrir Formulário {PARTES}]
END
```

### MakerButton7

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton7`
- evento `Ao Clicar` -> regra `M003 - Editor - Exibir Miniaturas`, tipo `desconhecido`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Protocolo={edtProtocolo};Texto={edttexto};Miniatura='false'`, `Nome=M003 - Editor - Exibir Miniaturas`
  funcoes do fluxo: `ebfFormRefreshComponent:server`, `ebfFormSetVisible:server`, `ebfGetElementFromList:server`, `ebfGroupBoxShowComponents:client`, `ebfHtmlRemoveChild`, `ebfReplace:server`, `isNullOrEmpty`
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormRefreshComponent` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  trecho `java` `java_runtime` de `ebfFormSetVisible` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  fluxo tecnico:

```text
START
IF Protocolo nulo ou vazio => isNullOrEmpty($Protocolo)
  - THEN
    - END
  - ELSE
    - IF Mostrar Miniaturas?
      - THEN
        - EXPRESSION = ebfGroupBoxShowComponents(MakerBevel1, false) [Oculta Moldura]
        - EXPRESSION = ebfFormSetVisible(divMiniatura, true) [Mostrar Componente "divMiniatura"]
        - EXPRESSION = ebfFormSetVisible(MakerButton4, false) [Esconde Botão "Miniaturas"]
        - EXPRESSION = ebfFormSetVisible(MakerButton7, true) [Mostrar Botão "Ocultar"]
        - EXPRESSION = ebfFormSetVisible(BTATUMINI, true) [Mostra Botão "Recarregar"]
        - EXPRESSION = ebfFormRefreshComponent(divMiniatura) [Atualizar "divMiniatura"]
        - EXPRESSION = ebfHtmlRemoveChild(ebfHtmlGetElementById(divMiniatura), ebfGetElementFromList(ebfHtmlChildNodes(ebfHtmlGetElementById(divMiniatura)), 1)) [Limpar Miniaturas]
        - CALLRULE M003 - Editor - Miniatura do PDF - Gerar PDF
        - CALLRULE GER - Gerar GUID - Generico
```

### MakerButton8

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton8`
- evento `Ao Clicar` -> regra `-`, tipo `desconhecido`, criticidade `media`
  acoes: `1`
  parametros: `Código de Formulário={2B7C8983-BF3F-4832-BE46-4E9CDA828385}`, `Modo de Abertura=0`

### MakerButton9

- tipo: `B`
- categoria: `button`
- caminho: `MakerButton9`
- evento `Ao Clicar` -> regra `Geral - Abrir Formulário`, tipo `ui`, criticidade `media`
  acoes: `12`
  parametros: `Entrada=Formulário='{58DE50E7-13C7-4C13-97CF-3A03DAF5F2BC}'`, `Nome=Geral - Abrir Formulário`

## 6. Regras, Funcoes Maker e Fluxos Provaveis

### M003 - Editor - Abrir Form C001 Filtrado

- codigo: `1081`
- tipo inferido: `ui`
- exige migracao manual: `false`
- eventos que chamam a regra: `MakerButton3:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormOpenFilteredForm`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfSetSessionAttribute(abaimovel, true, false) [Definir Variável da Sessão 'abaimovel']
EXPRESSION = ebfFormOpenFilteredForm({28ADA629-4C27-4F89-857E-F433304D64B8}, ebfConcat(protocolo.codigo_pro=, $Protocolo)) [Abrir Formulário Filtrado]
END
```
- funcoes maker mapeadas:
  - `ebfFormOpenFilteredForm` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3772`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3702`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3772`

```java
protected final Variant ebfFormOpenFilteredForm(Variant variant, Variant variant2) throws Exception {
        this.client.openFilteredForm(this.manager.getSys().getFormByGuid(variant.getString()), variant2.toString());
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3702`

```java
protected final Variant ebfFormOpenFilteredForm(Variant variant, Variant variant2) throws Exception {
        this.client.openFilteredForm(this.manager.getSys().getFormByGuid(variant.getString()), variant2.toString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfSetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10014`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3423`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9764`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9771`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:131`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1081" NAME="M003 - Editor - Abrir Form C001 Filtrado" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>31/07/2021 18:03:50</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression2" DESCRIPTION="Definir Variável da Sessão 'abaimovel'">
<FUNCTION NAME="Definir variável da sessão" REALNAME="ebfSetSessionAttribute">
<PARAMS>
<CONSTANT TYPE="Letras" NU...
```

### M003 - Editor - Ao clicar - Botão Editor

- codigo: `903`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `745130:Ao Entrar`, `btEDITORT:Ao Clicar`, `btEDITORF:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION isNullOrEmpty`, `FUNCTION isEqual`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormSetFocus`, `FUNCTION ebfGetSessionAttribute`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfRemoveSessionAttribute`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfSetSessionAttribute(s_editor, S, false) [Cria variavel de sessão]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, CKTODOS, 1) [CKTODOS = 1]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttempoato, ebfGetSessionAttribute(s_tempoato, false)) [Carrega Tempo]
IF m_protocolo é nulo ou vazio? => isNullOrEmpty(ebfGetSessionAttribute(m_protocolo, false))
  - THEN
    - CONNECTOR FlowConnector2
    - CONNECTOR FlowConnector3
    - IF uf = 'PR' => isEqual(ebfGetSessionAttribute(s_uf, false), PR)
      - THEN
        - EXPRESSION = ebfFormSetVisible(MDQTDE, false) [Esconder componente MDQTDE]
        - CONNECTOR FlowConnector1
        - EXPRESSION = ebfFormSetVisible(edtTraslado, false) [Esconder componente edtTranslado]
        - EXPRESSION = ebfFormSetVisible(TxTraslado, false) [Esconder componente TxTRANSLADO]
        - EXPRESSION = ebfFormSetVisible(btEDITORT, false) [Esconder componente btEDITORT]
        - EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, false) [Esconder componente TxFOLHADOLIVRO]
        - EXPRESSION = ebfFormSetVisible(btEDITORF, false) [Esconder componente btEDITORF]
        - EXPRESSION = ebfFormSetVisible(edtFolha, false) [Esconder componente edtFolha]
        - EXPRESSION = ebfFormSetVisible(edttexto, true) [Mostrar componente edtEditor]
        - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ed_Cidade, ebfGetSessionAttribute(s_cidade, false)) [Alterar Valor do Componente "edtcidade"]
        - EXPRESSION = ebfFormSetFocus(lstProtocolo) [Foca lstprotocolo]
        - END
      - ELSE
        - REFERENCE FlowExpression6
  - ELSE
    - EXPRESSION m_protocolo = ebfGetSessionAttribute(m_protocolo, false) [Obter Variável de Sessão "m_protocolo"]
    - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, lstProtocolo, $m_protocolo) [Vem da pesquisa]
    - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtpsq_protocolo, $m_protocolo) [Vem da pesquisa]
    - EXPRESSION = ebfRemoveSessionAttribute(m_protocolo, false) [Remove variavel de sessão m_protocolo]
    - REFERENCE FlowDecision1
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormSetFocus` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfFormSetVisible` [cliente] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Calend\u00e1rio - Mostrar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3854`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3784`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `ebfGetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10008`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3418`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3986`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9765`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:136`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:152`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:166`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`

```java
protected final Variant ebfFirebaseStopMonitoringData(Variant variant, Variant variant2) throws Exception {
        DatabaseReference databaseReference = (DatabaseReference)variant.getObject();
        TreeMap treeMap = (TreeMap)this.ebfGetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(true)).getObject();
        try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`

```java
protected Variant ebfGetComponentProperty(Variant variant, Variant variant2, Variant variant3) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        Variant variant4 = this.ebfGetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), Variant.VARIANT_FALSE);
        if (variant3 != null && variant3.getString().length() > 0) {
            if (variant4.getObject() != null) {
                return new Variant(variant4.getObject());
            }
            if (wFRForm.getComponentByName(variant2.getString()) != null) {
                return new Variant(wFRForm.getComponentByName(variant2.getString()).g...
```
  - `ebfRemoveSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:2162`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:2119`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:165`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:2162`

```java
protected final Variant ebfDestroyMakerFlowComponent(Variant variant) throws Exception {
        this.ebfRemoveSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant.getString()), Variant.VARIANT_FALSE);
        return Variant.VARIANT_NULL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void ebfDocxToPdf(Variant variant, Variant variant2) throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:2119`

```java
protected final Variant ebfDestroyMakerFlowComponent(Variant variant) throws Exception {
        this.ebfRemoveSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant.getString()), Variant.VARIANT_FALSE);
        return Variant.VARIANT_NULL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void ebfDocxToPdf(Variant variant, Variant variant2) throws Exception {
```
  - `ebfSetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10014`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3423`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9764`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9771`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:131`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="903" NAME="M003 - Editor - Ao clicar - Botão Editor" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>18/06/2024 13:27:49</DATE>
<RANKING>4,0</RANKING>
</HEADER>
<VAR TYPE="Letras" SIZE="50">m_protocolo</VAR>
<VAR TYPE="Fracionado" SIZE="">Tempo</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression14" DESCRIPTION="Cria variavel de sessão">
<FUNCTION NAME="Definir variável da sessão" REALNAME="ebfSetSessionAttribute">
<PARAMS>
<...
```

### M003 - Editor - Ao clicar - Botão Folha

- codigo: `902`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btFOLHA:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetFocus`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRTRASLADO, false) [Esconder Botão Imprimir Traslado]
EXPRESSION = ebfFormSetVisible(TxTraslado, false) [Esconder componente TxTraslado]
EXPRESSION = ebfFormSetVisible(btEDITORT, false) [Esconder componente btEDITORT]
EXPRESSION = ebfFormSetVisible(edtTraslado, false) [Esconder componente edtTraslado]
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRLIVRO, true) [Mostrar Botão Imprimir Folha do Livro]
EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, true) [Mostrar TxFolhaDoLivro]
EXPRESSION = ebfFormSetVisible(btEDITORF, true) [Mostrar componente btEDITORF]
EXPRESSION = ebfFormSetVisible(edtFolha, true) [Mostrar componente edtFolha]
EXPRESSION = ebfFormSetFocus(edtFolha) [Focar texto do edtFolha]
END
```
- funcoes maker mapeadas:
  - `ebfFormSetFocus` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfFormSetVisible` [cliente] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Calend\u00e1rio - Mostrar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3854`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3784`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="902" NAME="M003 - Editor - Ao clicar - Botão Folha" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>24/02/2022 21:44:07</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression2" DESCRIPTION="Esconder Botão Imprimir Traslado">
<FUNCTION NAME="Mostrar Componente" REALNAME="ebfFormSetVisible">
<PARAMS>
<CONSTANT TYPE="Componente" NULL="FALSE"  GUID = "{ECC86353-9F5B-4696-BBDE-EFB5B09466B5}">BTIMPRIMI...
```

### M003 - Editor - Ao clicar - Botão Gravar Dados

- codigo: `968`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btGRAVAR:Ao Clicar`, `btGRAVAR1:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`
- fluxo tecnico linearizado:

```text
START
CALLRULE GER Regravar Protocolo Minuta
CALLRULE GER Regravar Not1
END
```
- funcoes maker mapeadas:
  - `ebfFormGetComponentValue` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfFormGetLookupName` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfSQLGetFieldFromForm` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
  - `ebfSubstring` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10054`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10079`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`

```java
protected final Variant ebfSubstring(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        int n = string.length();
        int n2 = variant2.getLong().intValue() - 1;
        int n3 = n2 + variant3.getLong().intValue();
        n2 = n2 < 0 ? 0 : n2;
        int n4 = n3 = n3 > n ? n : n3;
        if (n2 > n || n2 >= n3) {
            return VariantPool.get("");
        }
        String string2 = "";
        try {
            string2 = string.substring(n2, n3);
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }

    protected final Variant ebfTextMD5(Variant variant) throws Exception {
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="968" NAME="M003 - Editor - Ao clicar - Botão Gravar Dados" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>16/03/2026 23:49:47</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodTab</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodJur</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodEsc</PARAM>
<PARAM TYPE="Letras" SIZE="50">Gratuidade</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="967" NAME...
```

### M003 - Editor - Ao clicar - Botão Traslado

- codigo: `901`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btTRASLADO:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetFocus`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRLIVRO, false) [Esconder Botão Imprimir Folha do Livro]
EXPRESSION = ebfFormSetVisible(TxFolhaDoLivro, false) [Esconder componente TxFolhaDoLivro]
EXPRESSION = ebfFormSetVisible(btEDITORF, false) [Esconder componente btEDITORF]
EXPRESSION = ebfFormSetVisible(edtFolha, false) [Esconder componente edtFolha]
EXPRESSION = ebfFormSetVisible(BTIMPRIMIRTRASLADO, true) [Mostrar Botão Imprimir Traslado]
EXPRESSION = ebfFormSetVisible(TxTraslado, true) [Mostrar TxTraslado]
EXPRESSION = ebfFormSetVisible(btEDITORT, true) [Mostrar componente btEDITORT]
EXPRESSION = ebfFormSetVisible(edtTraslado, true) [Mostrar componente edtTraslado]
EXPRESSION = ebfFormSetFocus(edtTraslado) [Focar texto do edtTraslado]
END
```
- funcoes maker mapeadas:
  - `ebfFormSetFocus` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfFormSetVisible` [cliente] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Calend\u00e1rio - Mostrar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3854`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3784`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="901" NAME="M003 - Editor - Ao clicar - Botão Traslado" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>06/10/2022 11:49:39</DATE>
<RANKING>7,5</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression2" DESCRIPTION="Esconder Botão Imprimir Folha do Livro">
<FUNCTION NAME="Mostrar Componente" REALNAME="ebfFormSetVisible">
<PARAMS>
<CONSTANT TYPE="Componente" NULL="FALSE"  GUID = "{ECC86353-9F5B-4696-BBDE-EFB5B09466B5}">B...
```

### M003 - Editor - Ao Clicar Botao Abrir Protocolo

- codigo: `1490`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `MakerButton10:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfConcat`
- fluxo tecnico linearizado:

```text
START
CALLRULE Abrir Formulario Filtrado - Generico
END
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1490" NAME="M003 - Editor - Ao Clicar Botao Abrir Protocolo" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Weslyn Scotti</AUTHOR>
<DATE>09/08/2022 12:01:47</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="230" NAME="Abrir Formulario Filtrado - Generico" RETURN="" PARAMSCOUNT="2" COMPONENTNAME="FlowSubRoutine1" DESCRIPTION="Abrir Formulario Filtrado - Generico">
<PARAMS>
<CONSTANT TYPE="Letras" NULL="...
```

### M003 - Editor - Ao Clicar Botao Upload

- codigo: `1298`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `BTATUALIZAIMG2:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfFormOpenForm`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormOpenForm({77A67895-3A6E-4B7D-9C7C-D9393F557F14}) [Abrir Formulário "M003 - Editor - Documentos do Ato - Upload"]
END
```
- funcoes maker mapeadas:
  - `ebfFormOpenForm` [cliente] camada `client` tipo `navegacao` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3707`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3708`
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }

    protected final void ebfFormOpenFormGroup() throws Exception {
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1298" NAME="M003 - Editor - Ao Clicar Botao Upload" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>06/10/2021 12:46:38</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Abrir Formulário "M003 - Editor - Documentos do Ato - Upload"">
<FUNCTION NAME="Abrir Formulário" REALNAME="ebfFormOpenForm">
<PARAMS>
<CONSTANT TYPE="Formulário" NULL="FALSE"  GUID = "{77A67895-3A6E-4B7D-9C7...
```

### M003 - Editor - Ao Duplo Clicar GRDIMG

- codigo: `1273`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `GRDIMG:Ao Duplo Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfGridGetValue`, `FUNCTION ebfConcat`, `FUNCTION ebfSubstringInverse`, `FUNCTION ebfLength`, `FUNCTION isEqual`, `FUNCTION oprIf`, `FUNCTION ebfConcat`, `FUNCTION ebfConcat`, `FUNCTION ebfFormGetComponentValue`, `FUNCTION ebfRichTextInsertTextAtPosition`, `FUNCTION oprIf`, `FUNCTION ebfConcat`, `FUNCTION ebfConcat`, `FUNCTION ebfFormGetComponentValue`
- fluxo tecnico linearizado:

```text
START
CALLRULE M003 - Editor - Ao Duplo Clicar GRDIMG - Subfluxo
EXPRESSION Caminho = ebfGridGetValue(GRDIMG, $Linha, Caminho) [Obter Caminho da Imagem]
EXPRESSION CaminhoTMCE = ebfConcat(/, ebfGetElementFromList($ListaPar, 2), /components/tinymce5/imagens, ebfSubstringInverse($Caminho, oprSubtract(ebfLength($Caminho), ebfLength(ebfGetElementFromList($ListaPar, 1))))) [Obter Caminho a ser Utilizado na Tag <img>]
IF Centralizar? => isEqual(ebfGridGetValue(GRDIMG, $Linha, Centralizar), 1)
  - THEN
    - CONNECTOR FlowConnector1
    - EXPRESSION Tag = oprIf(isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA)), ebfConcat(<center><img src=", $CaminhoTMCE, " alt="" style="max-width:100%; max-height:100%;"></center><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">), ebfConcat(<center><img src=", $CaminhoTMCE, " alt="" style="width:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTLARGURA), px; height:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA), px;"></center><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">)) [Monta tag <img>]
    - CONNECTOR FlowConnector2
    - EXPRESSION = ebfRichTextInsertTextAtPosition(edttexto, $Tag) [Inserir Tag <img> no Componente "EDTTEXTO"]
    - END
  - ELSE
    - EXPRESSION Tag = oprIf(isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA)), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="max-width:100%; max-height:100%;"><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="width:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTLARGURA), px; height:, ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTALTURA), px;"><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">)) [Monta tag <img>]
    - REFERENCE FlowExpression5
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfFormGetComponentValue` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfGridGetValue` [hibrida] camada `-` tipo `-` natureza `-`
  - `ebfLength` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
  - `ebfRichTextInsertTextAtPosition` [cliente] camada `-` tipo `-` natureza `-`
  - `ebfSubstringInverse` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10317`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10074`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10317`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10074`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `oprIf` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1273" NAME="M003 - Editor - Ao Duplo Clicar GRDIMG" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>master</AUTHOR>
<DATE>14/08/2024 17:13:11</DATE>
<RANKING>6,5</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="50">Linha</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">Tag</VAR>
<VAR TYPE="Variante" SIZE="">ListaPar</VAR>
<VAR TYPE="Letras" SIZE="50">CaminhoTMCE</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="1274" NAME="M003 - Editor - Ao Duplo Clicar...
```

### M003 - Editor - Ao Modificar Grade Custas

- codigo: `859`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btrecalcula:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormRefreshComponent`, `FUNCTION ebfFormRefreshComponent`
- fluxo tecnico linearizado:

```text
START
CALLRULE C001 - Protocolo - Ao Modificar Grade - Calcular Base FRJ
CALLRULE C001 - Protocolo - Calcular Selos Grade Custas
CALLRULE C001 - Protocolo - Calcular Emolumentos
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTEMOLUMENTOS_PRO, $Emolumentos) [Alterar Valor do Componente]
EXPRESSION = ebfFormRefreshComponent(EDTEMOLUMENTOS_PRO) [Atualizar componente EMOLUMENTOS]
EXPRESSION = ebfFormRefreshComponent(grdCUSTAS1) [Atualizar Grade]
END
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfSQLGetFieldFromForm` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="859" NAME="M003 - Editor - Ao Modificar Grade Custas" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>02/04/2026 19:32:21</DATE>
<RANKING>8,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Servico</PARAM>
<PARAM TYPE="Fracionado" SIZE="">vlrdeclar</PARAM>
<PARAM TYPE="Fracionado" SIZE="">vlravaliacao</PARAM>
<PARAM TYPE="Letras" SIZE="50">Gratuidade</PARAM>
<VAR TYPE="Frac...
```

### M003 - Editor - Ao Modificar Lista Tipo Ato

- codigo: `1012`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btCalcCustas:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfFormRefreshComponent`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormRefreshComponent`
- fluxo tecnico linearizado:

```text
START
IF codigo_pro nulo ou vazio? => isNullOrEmpty($Protocolo)
  - THEN
    - CONNECTOR FlowConnector1
    - END
  - ELSE
    - CALLRULE C001 - Inserir Dados Automaticamente - fin_protocolo
    - CALLRULE C001 - Protocolo - Calcular Emolumentos
    - EXPRESSION = ebfFormRefreshComponent(grdCUSTAS1) [Atualizar Grade]
    - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTEMOLUMENTOS_PRO, $Emolumentos) [Alterar Valor do Componente]
    - EXPRESSION = ebfFormRefreshComponent(EDTEMOLUMENTOS_PRO) [Atualiza componente EMOLUMENTOS]
    - REFERENCE FlowEnd1
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1012" NAME="M003 - Editor - Ao Modificar Lista Tipo Ato" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>20/03/2026 22:24:00</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Servico</PARAM>
<PARAM TYPE="Fracionado" SIZE="">vlrdeclar</PARAM>
<PARAM TYPE="Fracionado" SIZE="">vlravaliado</PARAM>
<PARAM TYPE="Letras" SIZE="50">Gratuidade</PARAM>
<VAR TYPE="F...
```

### M003 - Editor - Ao Modificar Lista TipodeAto

- codigo: `1200`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `edtTipAtoLST:Ao Modificar`
- passos do fluxo decodificado: `FUNCTION ebfFormChangeComponentValue`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAto, $Chave) [Alterar Valor do Componente]
END
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1200" NAME="M003 - Editor - Ao Modificar Lista TipodeAto" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>04/07/2021 20:50:42</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Chave</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Alterar Valor do Componente">
<FUNCTION NAME="Alterar Valor do Componente" REALNAME="ebfFormChangeComponentValue">
<PARAMS>
<CONSTANT TYPE="Formulário" NULL="...
```

### M003 - Editor - Ao Modificar Lista TipodeNatureza

- codigo: `1198`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `edtTipNatLST:Ao Modificar`
- passos do fluxo decodificado: `FUNCTION ebfFormChangeComponentValue`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNat, $Chave) [Alterar Valor do Componente]
END
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1198" NAME="M003 - Editor - Ao Modificar Lista TipodeNatureza" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>04/07/2021 18:00:55</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Chave</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Alterar Valor do Componente">
<FUNCTION NAME="Alterar Valor do Componente" REALNAME="ebfFormChangeComponentValue">
<PARAMS>
<CONSTANT TYPE="Formulário" N...
```

### M003 - Editor - Ao Precionar Tecla - psq_Folha

- codigo: `1477`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `edtpsq_Folha:Ao Pressionar Tecla`
- passos do fluxo decodificado: `DECISION`, `FUNCTION isEqual`
- fluxo tecnico linearizado:

```text
START
IF Tecla Pressionada = CR? => isEqual($Código da Tecla, 13)
  - THEN
    - CALLRULE M003 - Editor - Ao Modificar - psq_folha
    - CONNECTOR FlowConnector1
    - END
  - ELSE
    - REFERENCE FlowEnd1
```
- funcoes maker mapeadas:
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1477" NAME="M003 - Editor - Ao Precionar Tecla - psq_Folha" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>08/12/2022 13:28:02</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Lógico" SIZE="">Alt</PARAM>
<PARAM TYPE="Lógico" SIZE="">Ctrl</PARAM>
<PARAM TYPE="Lógico" SIZE="">Shift</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Código da Tecla</PARAM>
<PARAM TYPE="Letras" SIZE="50">Caracter da Tecla</PARAM>
<PARAM TYPE="Letras" SIZE="50">Nome</PARAM>
<PARAM TYPE="Int...
```

### M003 - Editor - Ao Sair Componente Anotacao

- codigo: `1255`
- tipo inferido: `navegacao`
- exige migracao manual: `false`
- eventos que chamam a regra: `EDTTEXTO1:Ao Sair`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfGridGetValue`, `FUNCTION isNullOrEmpty`, `FUNCTION isNullOrEmpty`
- fluxo tecnico linearizado:

```text
START
EXPRESSION ID = ebfGridGetValue(GDANOTACOES, ebfGridGetSelectedRow(GDANOTACOES), ID) [Obter ID da Anotação]
IF ID é Nulo ou Vazio? => isNullOrEmpty($ID)
  - THEN
    - CONNECTOR FlowConnector1
    - CONNECTOR FlowConnector2
    - CONNECTOR FlowConnector3
    - END
  - ELSE
    - IF Anotação é Nulo ou Vazio? => isNullOrEmpty($Anotacao)
      - THEN
        - REFERENCE FlowConnector2
      - ELSE
        - CALLRULE M003 - Editor - Ao Sair Componente Anotacao - Subfluxo
        - REFERENCE FlowEnd1
```
- funcoes maker mapeadas:
  - `ebfGridGetValue` [hibrida] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1252" NAME="M003 - Editor - Ao Sair Componente Anotacao" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Weslyn Scotti</AUTHOR>
<DATE>13/09/2021 14:28:53</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Anotacao</PARAM>
<VAR TYPE="Letras" SIZE="50">ID</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION RETURN="ID" COMPONENTNAME="FlowExpression1" DESCRIPTION="Obter ID da Anotação">
<FUNCTION NAME="Grade - Obter Valor" REALNAME="ebfGridGetValue">
<PARAMS>
<CONSTANT...
```

### M003 - Editor - Atualiza Rascunho - Cliente

- codigo: `1417`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btQUALIFICAR:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfSubstring`, `FUNCTION ebfListParamsCreate`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION toDouble`, `FUNCTION ebfGetSessionAttribute`, `FUNCTION ebfGetSessionAttribute`, `FUNCTION ebfGetSessionAttribute`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfSetSessionAttribute(s_texto, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto), false) [Obtem valor do componente edttexto
cria variavel de sessão s_texto]
EXPRESSION = ebfSetSessionAttribute(s_folha, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtFolha), false) [Obtem valor do componente edtfolha cria variavel de sessão s_folha]
EXPRESSION = ebfSetSessionAttribute(s_traslado, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTraslado), false) [Obtem valor do componente edttraslado
cria variavel de sessão s_traslado]
EXPRESSION Escrevente = ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente) [Obtem valor da lista dinamica Responsavel]
EXPRESSION Juramentado = ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiJuramentado) [Obtem valor da lista dinamica Juramentado]
EXPRESSION Tabeliao = ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiTabeliao) [Obtem valor da lista dinamica Tabelião]
EXPRESSION UF = ebfSubstring(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_cart_cid_cdibge), 1, 2) [UF do Local da Lavratura de dados complementares (Revogação,Retificação ou Substabelecimento)]
EXPRESSION Lista = ebfListParamsCreate(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtMinuta), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto), CALLRULE GER Data para Extenso, $Escrevente, $Juramentado, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ed_LivroProtocolo), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ed_NroMatricula), $Tabeliao, toDouble(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTEMOLUMENTOS_PRO)), $UF, $Tipo, $Protocolo, ebfGetSessionAttribute(s_texto, false), ebfGetSessionAttribute(s_folha, false), ebfGetSessionAttribute(s_traslado, false), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtLivro1), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtfolha1), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtLetra1), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCodMinuta), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCodLiv), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCRECI), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, cbbDOI), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtNomCorr), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtFolhaUsa), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtQTIMOVEIS), toDouble(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTVLRDECLAR_PRO)), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_CNS), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtProcolo_seq_liv), toDouble(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTVLRAVALIACAO_PRO)), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtDtDistrib), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtImoveis), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiTabeliao), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTCODSER), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_Livro), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_letra), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_Folha), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_cart_nome), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_datalavra), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtref_cart_cid_cdibge), ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtDISTRIBUICAO)) [Lista com elementos]
EXPRESSION = l2sFlowRunWithMessage(Atualizado Rascunho..., ALM 003 CAMPOS, ebfListParamsCreate($Tipo, $Protocolo, $Lista)) [Executar Fluxo com notificação
ALM 003 CAMPOS]
END
```
- funcoes maker mapeadas:
  - `ebfFormGetLookupName` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfGetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10008`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3418`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3986`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9765`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:136`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:152`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:166`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`

```java
protected final Variant ebfFirebaseStopMonitoringData(Variant variant, Variant variant2) throws Exception {
        DatabaseReference databaseReference = (DatabaseReference)variant.getObject();
        TreeMap treeMap = (TreeMap)this.ebfGetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(true)).getObject();
        try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`

```java
protected Variant ebfGetComponentProperty(Variant variant, Variant variant2, Variant variant3) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        Variant variant4 = this.ebfGetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), Variant.VARIANT_FALSE);
        if (variant3 != null && variant3.getString().length() > 0) {
            if (variant4.getObject() != null) {
                return new Variant(variant4.getObject());
            }
            if (wFRForm.getComponentByName(variant2.getString()) != null) {
                return new Variant(wFRForm.getComponentByName(variant2.getString()).g...
```
  - `ebfListParamsCreate` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4284`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4289`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:6001`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4206`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4211`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5932`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\sys\HTMLInterface\HTMLIntegration.java:498`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4284`

```java
for (int i = 0; i < stringArray.length; ++i) {
            variantArray[i] = VariantPool.get(stringArray[i]);
        }
        return this.ebfListParamsCreate(variantArray);
    }

    protected final Variant ebfGetInputValues() throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4289`

```java
protected final Variant ebfGetInputValues() throws Exception {
        Variant[] variantArray = this.getCurrentInputs();
        return this.ebfListParamsCreate(variantArray);
    }

    protected final Variant ebfGetJSONSessionsSystems(Variant variant) throws Exception {
```
  - `ebfSetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10014`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3423`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9764`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9771`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:131`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
  - `ebfSQLGetFieldFromForm` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
  - `ebfSubstring` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10054`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10079`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`

```java
protected final Variant ebfSubstring(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        int n = string.length();
        int n2 = variant2.getLong().intValue() - 1;
        int n3 = n2 + variant3.getLong().intValue();
        n2 = n2 < 0 ? 0 : n2;
        int n4 = n3 = n3 > n ? n : n3;
        if (n2 > n || n2 >= n3) {
            return VariantPool.get("");
        }
        String string2 = "";
        try {
            string2 = string.substring(n2, n3);
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }

    protected final Variant ebfTextMD5(Variant variant) throws Exception {
```
  - `l2sFlowRunWithMessage` [servidor] camada `-` tipo `-` natureza `-`
  - `toDouble` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1417" NAME="M003 - Editor - Atualiza Rascunho - Cliente" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>20/03/2026 14:18:40</DATE>
<RANKING>3,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<VAR TYPE="Letras" SIZE="50">Escrevente</VAR>
<VAR TYPE="Letras" SIZE="50">Juramentado</VAR>
<VAR TYPE="Letras" SIZE="50">Tabeliao</VAR>
<VAR TYPE="Letras" SIZE="50">UF</VAR>
<VAR TYPE="Variante" SIZE="">Lista</VAR...
```

### M003 - Editor - Buscar ultima folha do livro

- codigo: `1214`
- tipo inferido: `navegacao`
- exige migracao manual: `false`
- eventos que chamam a regra: `edtFolhaUsa:Ao Sair`
- passos do fluxo decodificado: `DECISION`, `FUNCTION oprOr`, `FUNCTION isEqual`, `FUNCTION ebfFormSetFocus`, `FUNCTION ebfStopRuleExecution`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfSQLEOF`, `FUNCTION isEqual`, `FUNCTION oprNot`, `FUNCTION isEqual`, `FUNCTION ebfSQLExecuteUpdate`, `FUNCTION ebfSQLNext`, `FUNCTION ebfFormSetEnabled`, `FUNCTION ebfFormSetFocus`, `FUNCTION ebfStopRuleExecution`, `FUNCTION isEqual`, `FUNCTION oprNot`, `FUNCTION isEqual`, `FUNCTION isEqual`
- fluxo tecnico linearizado:

```text
START
IF QtdeFolhas É nulo ou Vazio? => oprOr(isNullOrEmpty($QtdeFolhas), isEqual($QtdeFolhas, 0))
  - THEN
    - EXPRESSION = ebfFormSetFocus(btInconsistencia) [Focar componente botão  inconsistencias]
    - EXPRESSION = ebfStopRuleExecution(Não foi informado quantidade de folhas) [Interromper Fluxo]
    - CONNECTOR FlowConnector7
    - CONNECTOR FlowConnector3
    - END
  - ELSE
    - IF DataAto É nulo ou vazio => isNullOrEmpty(ebfFormatDateTime($DtAto, dd/MM/yyyy))
      - THEN
        - EXPRESSION ConsultaInc = ebfSQLExecuteQuery() [Consulta Tabela "inconsistencia"]
        - WHILE Existem Registros? => ebfSQLEOF($ConsultaInc)
          - IF FOLHA? => isEqual(ebfSQLField($ConsultaInc, TIPO_INC), FOLHA)
            - THEN
              - IF QtdeFolhas foi Informado? => oprNot(oprOr(isNullOrEmpty($QtdeFolhas), isEqual($QtdeFolhas, 0)))
                - THEN
                  - EXPRESSION = ebfSQLExecuteUpdate() [Atualiza Verificado]
                  - EXPRESSION = ebfSQLNext($ConsultaInc) [Próximo Registro]
                  - CONNECTOR FlowConnector16
                - ELSE
                  - CONNECTOR FlowConnector12
                  - CONNECTOR FlowConnector13
                  - CONNECTOR FlowConnector14
                  - CONNECTOR FlowConnector8
                  - CONNECTOR FlowConnector15
                  - EXPRESSION = ebfFormSetEnabled(MakerButton5, true) [Habilitar Botão "Inconsistências"]
                  - EXPRESSION = ebfFormSetFocus(btInconsistencia) [Focar componente botão  inconsistencias]
                  - EXPRESSION = ebfStopRuleExecution(Existem Inconsistências a Serem Corrigidas) [Interromper Fluxo]
                  - END
            - ELSE
              - IF TIPOATO? => isEqual(ebfSQLField($ConsultaInc, TIPO_INC), TIPOATO)
                - THEN
                  - IF Tipo do Ato foi Informado? => oprNot(oprOr(isNullOrEmpty($TipoAto), isEqual($TipoAto, 0)))
                    - THEN
                      - CONNECTOR FlowConnector10
                      - REFERENCE FlowExpression19
                    - ELSE
                      - REFERENCE FlowConnector13
                - ELSE
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormSetEnabled` [cliente] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - rotulo maker: `Calend\u00e1rio - Habilitar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3827`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3828`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3757`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3758`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `ebfFormSetFocus` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3831`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3761`

```java
protected final Variant ebfFormSetFocus(Variant variant) throws Exception {
        this.client.setFocus(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfSQLCommitTransaction` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8317`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8074`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8317`

```java
protected final Variant ebfSQLCommitTransaction(Variant variant) throws Exception {
        DBConnection dBConnection = this.connection;
        if (variant.getObject() != null && variant.getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)variant.getObject();
        }
        dBConnection.commitTransaction();
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8074`

```java
protected final Variant ebfSQLCommitTransaction(Variant variant) throws Exception {
        DBConnection dBConnection = this.connection;
        if (variant.getObject() != null && variant.getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)variant.getObject();
        }
        dBConnection.commitTransaction();
        return Variant.VARIANT_NULL;
    }
```
  - `ebfSQLEOF` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8453`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8210`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:465`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:42`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8453`

```java
protected final Variant ebfSQLEOF(Variant variant) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        String string = resultSet.toString();
        try {
            if (string.contains("net.sourceforge.jtds") || string.contains("oracle.jdbc.driver.OracleResultSetImpl") || string.contains("org.apache.commons.dbcp.DelegatingResultSet")) {
                if (resultSet instanceof WFRResultSet) {
                    return VariantPool.get(resultSet.getRow() != 0);
                }
                resultSet.getObject(1);
                return Variant.VARIANT_TRUE;
            }
            return VariantPool.get(resultSet.getRow() != 0);
        }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8210`

```java
protected final Variant ebfSQLEOF(Variant variant) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        String string = resultSet.toString();
        try {
            if (string.contains("net.sourceforge.jtds") || string.contains("oracle.jdbc.driver.OracleResultSetImpl") || string.contains("org.apache.commons.dbcp.DelegatingResultSet")) {
                if (resultSet instanceof WFRResultSet) {
                    return VariantPool.get(resultSet.getRow() != 0);
                }
                resultSet.getObject(1);
                return Variant.VARIANT_TRUE;
            }
            return VariantPool.get(resultSet.getRow() != 0);
        }
```
  - `ebfSQLExecuteQuery` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9065`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8755`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8822`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:460`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:466`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1067`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1072`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1271`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:723`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:729`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`

```java
} else {
            int n = variantArray.length;
            variantArray2 = new Variant[n + 1];
            System.arraycopy(variantArray, 0, variantArray2, 0, n);
            variantArray2[n] = variant2;
        }
        return this.ebfSQLExecuteQuery(variant.getString(), variant3.getLong().intValue(), variantArray2);
    }

    protected final Variant ebfSQLDynamicScrollQuery(Variant variant, Variant variant2, Variant variant3, Variant ... variantArray) throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`

```java
protected final Variant ebfSQLExecuteQuery(String string, int n, Variant ... variantArray) throws Exception {
        Object object;
        Functions.checkInvalidCommand(string);
        Long l = System.currentTimeMillis();
        ResultSet resultSet = null;
        DBConnection dBConnection = null;
        if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)((Variant)object).getObject();
        }
        if (this.developmentMode) {
            System.out.println("### SELECT BEGIN [" + this.ruleName + "] ###");
            System.out.println(string);
            System.out.println();
```
  - `ebfSQLExecuteUpdate` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9143`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9234`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9240`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8900`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8991`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8997`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9143`

```java
protected final Variant ebfSQLExecuteUpdate(String string, int n, Variant ... variantArray) throws Exception {
        Serializable serializable;
        Long l = System.currentTimeMillis();
        PreparedStatement preparedStatement = null;
        long l2 = -1L;
        try {
            Object object;
            serializable = null;
            if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
                serializable = (DBConnection)((Variant)object).getObject();
            }
            if (this.developmentMode) {
                System.out.println("### SQL BEGIN [" + this.ruleName + "] ###");
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9234`

```java
if (this.developmentMode) {
                    System.out.println("### SQL END [" + this.ruleName + "] ###");
                    System.out.println();
                }
                DBConnection.close(preparedStatement);
                Long l3 = System.currentTimeMillis();
                MonitoringManager.getInstance().addMonitoringDatabase(this.getSystem(), this.ruleName, "ebfSQLExecuteUpdate", l, l3);
                throw throwable;
            }
        }
        DBConnection.close(preparedStatement);
        serializable = Long.valueOf(System.currentTimeMillis());
        MonitoringManager.getInstance().addMonitoringDatabase(this.getSystem(), this.ruleName, "ebfSQLExecuteUpdate", l, (Long)serializable);
```
  - `ebfSQLField` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9244`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9001`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:459`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:40`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9244`

```java
protected final Variant ebfSQLField(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        ResultSet resultSet = variant.getResultSet();
        if (this.hasData(resultSet)) {
            Object object = resultSet.getObject(variant2.getString());
            if (object instanceof TIMESTAMP) {
                TIMESTAMP tIMESTAMP = (TIMESTAMP)object;
                variant3 = VariantPool.get(tIMESTAMP.dateValue());
            } else {
                variant3 = VariantPool.get(object);
            }
        }
        return variant3;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9001`

```java
protected final Variant ebfSQLField(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        ResultSet resultSet = variant.getResultSet();
        if (this.hasData(resultSet)) {
            Object object = resultSet.getObject(variant2.getString());
            if (object instanceof TIMESTAMP) {
                TIMESTAMP tIMESTAMP = (TIMESTAMP)object;
                variant3 = VariantPool.get(tIMESTAMP.dateValue());
            } else {
                variant3 = VariantPool.get(object);
            }
        }
        return variant3;
```
  - `ebfSQLNext` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9511`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9268`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9511`

```java
protected final Variant ebfSQLNext(Variant variant) throws Exception {
        return VariantPool.get(variant.getResultSet().next());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9268`

```java
protected final Variant ebfSQLNext(Variant variant) throws Exception {
        return VariantPool.get(variant.getResultSet().next());
    }
```
  - `ebfStopRuleExecution` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `isMinorOrEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
  - `oprAdd` [servidor] camada `-` tipo `-` natureza `-`
  - `oprNot` [servidor] camada `-` tipo `-` natureza `-`
  - `oprOr` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1214" NAME="M003 - Editor - Buscar ultima folha do livro" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>14/06/2024 16:17:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">CodLivro</PARAM>
<PARAM TYPE="Inteiro" SIZE="">QtdeFolhas</PARAM>
<PARAM TYPE="Inteiro" SIZE="">FolhaInicial</PARAM>
<PARAM TYPE="Data" SIZE="">DtAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Ato</PARAM>
<PARAM TYPE="Letras" SIZE="...
```

### M003 - Editor - Download Arquivos do Ato

- codigo: `1276`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `GRDPDF:Ao Duplo Clicar`
- passos do fluxo decodificado: `FUNCTION ebfGridGetValue`, `FUNCTION ebfGridGetValue`, `FUNCTION ebfGetComponentValueFromOtherForm`
- fluxo tecnico linearizado:

```text
START
EXPRESSION Caminho = ebfGridGetValue(GRDPDF, $Linha, Caminho) [Obter Caminho do Arquivo]
EXPRESSION Nome = ebfGridGetValue(GRDPDF, $Linha, Arquivo) [Obter Nome do Arquivo]
CALLRULE M003 - Editor - Download Arquivos do Ato - Subfluxo
END
```
- funcoes maker mapeadas:
  - `ebfGetComponentValueFromOtherForm` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfGridGetValue` [hibrida] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1276" NAME="M003 - Editor - Download Arquivos do Ato" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>11/09/2023 20:12:37</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="50">Linha</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">Nome</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION RETURN="Caminho" COMPONENTNAME="FlowExpression2" DESCRIPTION="Obter Caminho do Arquivo">
<FUNCTION NAME="Grade - Obter Valor...
```

### M003 - Editor - Exibir Miniaturas

- codigo: `1324`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `BTATUMINI:Ao Clicar`, `MakerButton4:Ao Clicar`, `MakerButton7:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfGroupBoxShowComponents`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormRefreshComponent`, `FUNCTION ebfHtmlRemoveChild`, `FUNCTION ebfGetElementFromList`, `FUNCTION ebfReplace`, `FUNCTION ebfGroupBoxShowComponents`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormSetVisible`, `FUNCTION ebfFormRefreshComponent`, `FUNCTION ebfHtmlRemoveChild`, `FUNCTION ebfGetElementFromList`
- fluxo tecnico linearizado:

```text
START
IF Protocolo nulo ou vazio => isNullOrEmpty($Protocolo)
  - THEN
    - END
  - ELSE
    - IF Mostrar Miniaturas?
      - THEN
        - EXPRESSION = ebfGroupBoxShowComponents(MakerBevel1, false) [Oculta Moldura]
        - EXPRESSION = ebfFormSetVisible(divMiniatura, true) [Mostrar Componente "divMiniatura"]
        - EXPRESSION = ebfFormSetVisible(MakerButton4, false) [Esconde Botão "Miniaturas"]
        - EXPRESSION = ebfFormSetVisible(MakerButton7, true) [Mostrar Botão "Ocultar"]
        - EXPRESSION = ebfFormSetVisible(BTATUMINI, true) [Mostra Botão "Recarregar"]
        - EXPRESSION = ebfFormRefreshComponent(divMiniatura) [Atualizar "divMiniatura"]
        - EXPRESSION = ebfHtmlRemoveChild(ebfHtmlGetElementById(divMiniatura), ebfGetElementFromList(ebfHtmlChildNodes(ebfHtmlGetElementById(divMiniatura)), 1)) [Limpar Miniaturas]
        - CALLRULE M003 - Editor - Miniatura do PDF - Gerar PDF
        - CALLRULE GER - Gerar GUID - Generico
        - CALLRULE M003 - Miniatura do PDF - Exibir Miniaturas - Subfluxo
        - END
      - ELSE
        - CONNECTOR FlowConnector1
        - EXPRESSION = ebfGroupBoxShowComponents(MakerBevel1, true) [Mostra Moldura]
        - EXPRESSION = ebfFormSetVisible(divMiniatura, false) [Esconde Componente "divMiniatura"]
        - EXPRESSION = ebfFormSetVisible(MakerButton7, false) [Esconder Botão "Ocultar"]
        - EXPRESSION = ebfFormSetVisible(MakerButton4, true) [Mostra Botão "Miniaturas"]
        - EXPRESSION = ebfFormSetVisible(BTATUMINI, false) [Esconde Botão "Recarregar"]
        - EXPRESSION = ebfFormRefreshComponent(divMiniatura) [Atualizar "divMiniatura"]
        - EXPRESSION = ebfHtmlRemoveChild(ebfHtmlGetElementById(divMiniatura), ebfGetElementFromList(ebfHtmlChildNodes(ebfHtmlGetElementById(divMiniatura)), 1)) [Limpar Miniaturas]
        - END
```
- funcoes maker mapeadas:
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfFormSetVisible` [cliente] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Calend\u00e1rio - Mostrar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3854`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3784`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:181`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:188`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `ebfGetElementFromList` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4095`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4017`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4095`

```java
protected final Variant ebfGetElementFromList(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            int n = variant2.getLong().intValue() - 1;
            if (n < 0) {
                n = 0;
            } else if (n >= list.size()) {
                n = list.size() - 1;
            }
            variant3 = VariantPool.get(list.get(n));
        }
        return variant3;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4017`

```java
protected final Variant ebfGetElementFromList(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            int n = variant2.getLong().intValue() - 1;
            if (n < 0) {
                n = 0;
            } else if (n >= list.size()) {
                n = list.size() - 1;
            }
            variant3 = VariantPool.get(list.get(n));
        }
        return variant3;
```
  - `ebfGroupBoxShowComponents` [servidor] camada `client` tipo `sistema` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4983`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4984`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4905`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4906`
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4983`

```java
protected final Variant ebfGroupBoxShowComponents(Variant variant, Variant variant2) throws Exception {
        return this.callClientFunction("ebfGroupBoxShowComponents", variant, variant2);
    }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4984`

```java
protected final Variant ebfGroupBoxShowComponents(Variant variant, Variant variant2) throws Exception {
        return this.callClientFunction("ebfGroupBoxShowComponents", variant, variant2);
    }

    protected final Variant ebfHTMLTableCreate(Variant variant, Variant variant2, Variant variant3, Variant variant4, Variant variant5, Variant variant6, Variant variant7, Variant variant8, Variant variant9, Variant variant10, Variant variant11) throws Exception {
```
  - `ebfHtmlRemoveChild` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfReplace` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7162`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7088`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7162`

```java
protected final Variant ebfReplace(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceFirst(string, string2, string3));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7088`

```java
protected final Variant ebfReplace(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceFirst(string, string2, string3));
    }
```
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1324" NAME="M003 - Editor - Exibir Miniaturas" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>06/08/2022 21:18:01</DATE>
<RANKING>5,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Miniatura</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">IFrame</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<DECISION COMPONENTNAME="FlowDecision2" DESC...
```

### M003 - Editor - Gerar Recibo

- codigo: `964`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btRecibo2:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfAppend`, `FUNCTION ebfSubstring`, `FUNCTION ebfSubstring`, `FUNCTION ebfAppend`, `FUNCTION ebfSubstring`, `FUNCTION ebfSubstring`, `FUNCTION isNullOrEmpty`, `FUNCTION isNullOrEmpty`, `FUNCTION isNullOrEmpty`, `FUNCTION isMinor`
- fluxo tecnico linearizado:

```text
START
EXPRESSION Data_rec = ebfAppend(ebfSubstring($Data, 7, 4), ebfSubstring($Data, 4, 2), ebfSubstring($Data, 1, 2)) [Trata data do recibo]
EXPRESSION Data_venc = ebfAppend(ebfSubstring($DtVenc, 7, 4), ebfSubstring($DtVenc, 4, 2), ebfSubstring($DtVenc, 1, 2)) [Trata data de vencimento]
IF Cliente é Nulo ou Vazio? => isNullOrEmpty($CodCli)
  - THEN
    - CALLRULE SweetAlert - Notificar - Botão Executar
    - CONNECTOR FlowConnector1
    - CONNECTOR FlowConnector2
    - END
  - ELSE
    - IF Data é Nulo ou Vazio? => isNullOrEmpty($Data_rec)
      - THEN
        - CALLRULE SweetAlert - Notificar - Botão Executar
        - REFERENCE FlowConnector2
      - ELSE
        - IF DtVenc é nulo ou vazio? => isNullOrEmpty($Data_venc)
          - THEN
            - CONNECTOR FlowConnector4
            - CONNECTOR FlowConnector3
            - CALLRULE M003 - Editor - Gerar Recibo - Inserir Dados Reccab e Recitem
            - CALLRULE SweetAlert - Notificar - Botão Executar
            - END
          - ELSE
            - IF Data do vencimento menor que a data do recibo => isMinor($Data_venc, $Data_rec)
              - THEN
                - CALLRULE SweetAlert - Notificar - Botão Executar
                - REFERENCE FlowEnd2
              - ELSE
                - REFERENCE FlowSubRoutine1
```
- funcoes maker mapeadas:
  - `ebfAppend` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1078`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1035`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1078`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1035`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
  - `ebfSubstring` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10054`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10079`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`

```java
protected final Variant ebfSubstring(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        int n = string.length();
        int n2 = variant2.getLong().intValue() - 1;
        int n3 = n2 + variant3.getLong().intValue();
        n2 = n2 < 0 ? 0 : n2;
        int n4 = n3 = n3 > n ? n : n3;
        if (n2 > n || n2 >= n3) {
            return VariantPool.get("");
        }
        String string2 = "";
        try {
            string2 = string.substring(n2, n3);
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }

    protected final Variant ebfTextMD5(Variant variant) throws Exception {
```
  - `isMinor` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="964" NAME="M003 - Editor - Gerar Recibo" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>20/03/2026 22:23:25</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Livro</PARAM>
<PARAM TYPE="Letras" SIZE="50">Folha</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoCli</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodCli</PARAM>
<PARAM TYPE="Letras" SIZE="50">Servico</PARAM...
```

### M003 - Editor - Grade Anotação - Atualizar texto

- codigo: `894`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `GDANOTACOES:Ao Modificar`
- passos do fluxo decodificado: `FUNCTION ebfGetFormResultset`, `FUNCTION ebfResultSetAbsolute`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormSetEnabled`
- fluxo tecnico linearizado:

```text
START
EXPRESSION Tabela = ebfGetFormResultset({6F4DCC6B-8867-40ED-889A-AD9636EDA508}) [Obter tabela do formulario]
EXPRESSION = ebfResultSetAbsolute($Tabela, $Linha) [Posiciona registro]
EXPRESSION Tabela = ebfSQLExecuteQuery() [Consulta tabela "atoanotacoes"]
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, EDTTEXTO1, ebfSQLField($Tabela, TEXTO)) [Atualiza o componente texto da anotação]
EXPRESSION = ebfFormSetEnabled(EDTTEXTO1, true) [Habilita Lembrete]
END
```
- funcoes maker mapeadas:
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormSetEnabled` [cliente] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - rotulo maker: `Calend\u00e1rio - Habilitar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3827`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3828`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3757`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3758`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `ebfGetFormResultset` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4249`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4171`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4249`

```java
protected final Variant ebfGetFormResultset(Variant variant) throws Exception {
        WFRForm wFRForm = this.client.getSystem().getFormByGuid(variant.getString());
        ResultSet resultSet = this.client.getData().getFormResultSet(wFRForm.getCodeStr());
        return new Variant(resultSet);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4171`

```java
protected final Variant ebfGetFormResultset(Variant variant) throws Exception {
        WFRForm wFRForm = this.client.getSystem().getFormByGuid(variant.getString());
        ResultSet resultSet = this.client.getData().getFormResultSet(wFRForm.getCodeStr());
        return new Variant(resultSet);
    }
```
  - `ebfResultSetAbsolute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7677`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7429`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7677`

```java
protected final Variant ebfResultSetAbsolute(Variant variant, Variant variant2) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        if (!(resultSet instanceof CachedResultSet)) {
            throw new Exception("A tabela n\u00e3o d\u00e1 suporte a esse tipo de navega\u00e7\u00e3o");
        }
        resultSet.absolute(variant2.getLong().intValue());
        return new Variant(resultSet);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7429`

```java
protected final Variant ebfResultSetAbsolute(Variant variant, Variant variant2) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        if (!(resultSet instanceof CachedResultSet)) {
            throw new Exception("A tabela n\u00e3o d\u00e1 suporte a esse tipo de navega\u00e7\u00e3o");
        }
        resultSet.absolute(variant2.getLong().intValue());
        return new Variant(resultSet);
    }
```
  - `ebfSQLExecuteQuery` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9065`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8755`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8822`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:460`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:466`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1067`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1072`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1271`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:723`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:729`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`

```java
} else {
            int n = variantArray.length;
            variantArray2 = new Variant[n + 1];
            System.arraycopy(variantArray, 0, variantArray2, 0, n);
            variantArray2[n] = variant2;
        }
        return this.ebfSQLExecuteQuery(variant.getString(), variant3.getLong().intValue(), variantArray2);
    }

    protected final Variant ebfSQLDynamicScrollQuery(Variant variant, Variant variant2, Variant variant3, Variant ... variantArray) throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`

```java
protected final Variant ebfSQLExecuteQuery(String string, int n, Variant ... variantArray) throws Exception {
        Object object;
        Functions.checkInvalidCommand(string);
        Long l = System.currentTimeMillis();
        ResultSet resultSet = null;
        DBConnection dBConnection = null;
        if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)((Variant)object).getObject();
        }
        if (this.developmentMode) {
            System.out.println("### SELECT BEGIN [" + this.ruleName + "] ###");
            System.out.println(string);
            System.out.println();
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="894" NAME="M003 - Editor - Grade Anotação - Atualizar texto" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Weslyn Scotti</AUTHOR>
<DATE>15/09/2021 14:35:12</DATE>
<RANKING>5,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Linha</PARAM>
<VAR TYPE="Tabela" SIZE="">Tabela</VAR>
<VAR TYPE="Letras" SIZE="50">Modo</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION RETURN="Tabela" COMPONENTNAME="FlowExpression2" DESCRIPTION="Obter tabela do formulario">
<FUNCTION NAME="Obter Tabela do F...
```

### M003 - Editor - Grade Selos gerados

- codigo: `1173`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `GRSelos:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfChangeComponentValueOtherForm`, `FUNCTION ebfRefreshComponentOtherForm`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfChangeComponentValueOtherForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtselo, ebfGridGetValue(GRSelos, ebfGridGetSelectedRow(GRSelos), Chavedigital)) [Obtem o Selo da GRID]
EXPRESSION = ebfRefreshComponentOtherForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtselo) [Atualiza componente edtselo]
END
```
- funcoes maker mapeadas:
  - `ebfChangeComponentValueOtherForm` [hibrida] camada `-` tipo `-` natureza `-`
  - `ebfRefreshComponentOtherForm` [hibrida] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1173" NAME="M003 - Editor - Grade Selos gerados" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>03/06/2021 16:41:57</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Obtem o Selo da GRID">
<FUNCTION NAME="Alterar Valor do Componente em Outro Formulário" REALNAME="ebfChangeComponentValueOtherForm">
<PARAMS>
<CONSTANT TYPE="Formulário" NULL="FALSE"  GUID = "{ECC86353-9F5B-4696-...
```

### M003 - Editor - Importar todas as imagens

- codigo: `1318`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `MakerButton1:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfSQLEOF`, `FUNCTION oprOr`, `FUNCTION isEqual`, `FUNCTION isEqual`, `FUNCTION isEqual`, `FUNCTION ebfConcat`, `FUNCTION ebfSubstringInverse`, `FUNCTION oprSubtract`, `FUNCTION ebfLength`, `FUNCTION oprIf`, `FUNCTION ebfConcat`, `FUNCTION ebfConcat`, `FUNCTION ebfSQLNext`
- fluxo tecnico linearizado:

```text
START
EXPRESSION Parametros = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION Consulta = ebfSQLExecuteQuery() [Le imagens]
WHILE Existem Registros? => ebfSQLEOF($Consulta)
  - IF Extensão JPG? => oprOr(isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), jpg), isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), JPEG), isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), jpeg), isEqual(ebfGetFileExtension(ebfSQLField($Consulta, ARQUIVO_IAT)), JPG))
    - THEN
      - EXPRESSION CaminhoTMCE = ebfConcat(/, ebfSQLField($Parametros, CONTEXTO_PAR), /components/tinymce5/imagens, ebfSubstringInverse(ebfSQLField($Consulta, ARQUIVO_IAT), oprSubtract(ebfLength(ebfSQLField($Consulta, ARQUIVO_IAT)), ebfLength(ebfSQLField($Parametros, CAMINHOIMGATO_PAR))))) [Obter Caminho a ser Utilizado na Tag <img>]
      - EXPRESSION Tag = oprIf(isNullOrEmpty($Altura), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="max-width:100%; max-height:100%;"><p class="default" style="text-align: justify;" data-mce-style="text-align: justify;">), ebfConcat(<img src=", $CaminhoTMCE, " alt="" style="width:, $Largura, px; height:, $Altura, px;">)) [Monta tag <img>]
      - CALLRULE M003 - Editor - Importar todas as imagens - inserir no texto
      - EXPRESSION = ebfSQLNext($Consulta) [Proximo Registro]
    - ELSE
      - CONNECTOR FlowConnector1
      - REFERENCE FlowExpression5
END
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfLength` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
  - `ebfSQLEOF` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8453`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8210`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:465`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:42`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8453`

```java
protected final Variant ebfSQLEOF(Variant variant) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        String string = resultSet.toString();
        try {
            if (string.contains("net.sourceforge.jtds") || string.contains("oracle.jdbc.driver.OracleResultSetImpl") || string.contains("org.apache.commons.dbcp.DelegatingResultSet")) {
                if (resultSet instanceof WFRResultSet) {
                    return VariantPool.get(resultSet.getRow() != 0);
                }
                resultSet.getObject(1);
                return Variant.VARIANT_TRUE;
            }
            return VariantPool.get(resultSet.getRow() != 0);
        }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8210`

```java
protected final Variant ebfSQLEOF(Variant variant) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        String string = resultSet.toString();
        try {
            if (string.contains("net.sourceforge.jtds") || string.contains("oracle.jdbc.driver.OracleResultSetImpl") || string.contains("org.apache.commons.dbcp.DelegatingResultSet")) {
                if (resultSet instanceof WFRResultSet) {
                    return VariantPool.get(resultSet.getRow() != 0);
                }
                resultSet.getObject(1);
                return Variant.VARIANT_TRUE;
            }
            return VariantPool.get(resultSet.getRow() != 0);
        }
```
  - `ebfSQLExecuteQuery` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9065`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8755`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8822`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:460`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:466`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1067`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1072`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1271`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:723`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:729`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`

```java
} else {
            int n = variantArray.length;
            variantArray2 = new Variant[n + 1];
            System.arraycopy(variantArray, 0, variantArray2, 0, n);
            variantArray2[n] = variant2;
        }
        return this.ebfSQLExecuteQuery(variant.getString(), variant3.getLong().intValue(), variantArray2);
    }

    protected final Variant ebfSQLDynamicScrollQuery(Variant variant, Variant variant2, Variant variant3, Variant ... variantArray) throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`

```java
protected final Variant ebfSQLExecuteQuery(String string, int n, Variant ... variantArray) throws Exception {
        Object object;
        Functions.checkInvalidCommand(string);
        Long l = System.currentTimeMillis();
        ResultSet resultSet = null;
        DBConnection dBConnection = null;
        if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)((Variant)object).getObject();
        }
        if (this.developmentMode) {
            System.out.println("### SELECT BEGIN [" + this.ruleName + "] ###");
            System.out.println(string);
            System.out.println();
```
  - `ebfSQLNext` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9511`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9268`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9511`

```java
protected final Variant ebfSQLNext(Variant variant) throws Exception {
        return VariantPool.get(variant.getResultSet().next());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9268`

```java
protected final Variant ebfSQLNext(Variant variant) throws Exception {
        return VariantPool.get(variant.getResultSet().next());
    }
```
  - `ebfSubstringInverse` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10317`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10074`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10317`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10074`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `oprIf` [servidor] camada `-` tipo `-` natureza `-`
  - `oprOr` [servidor] camada `-` tipo `-` natureza `-`
  - `oprSubtract` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1318" NAME="M003 - Editor - Importar todas as imagens" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>05/08/2024 23:01:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Altura</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Largura</PARAM>
<VAR TYPE="Tabela" SIZE="">Consulta</VAR>
<VAR TYPE="Letras" SIZE="50">Tag</VAR>
<VAR TYPE="Tabela" SIZE="">Parametros</VAR>
<VAR TYPE="Letras" SIZE="50">CaminhoTMCE</VAR>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<...
```

### M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade

- codigo: `1271`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `BTATUALIZAIMG:Ao Clicar`, `BTATUALIZAIMG1:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION isEqual`, `FUNCTION oprIf`, `FUNCTION ebfSQLField`, `FUNCTION ebfConcat`, `FUNCTION ebfSQLField`, `FUNCTION ebfSQLExecuteUpdate`, `FUNCTION ebfConcat`, `FUNCTION ebfReplaceAll`, `FUNCTION ebfListLength`, `FUNCTION ebfSQLExecuteUpdate`, `FUNCTION isEqual`, `FUNCTION oprNot`, `FUNCTION oprAdd`, `FUNCTION oprOr`, `FUNCTION isEqual`, `FUNCTION isEqual`, `FUNCTION isEqual`
- fluxo tecnico linearizado:

```text
START
EXPRESSION ConsultaPar = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION ConsultaLivro = ebfSQLExecuteQuery() [Consulta Tabela "livro"]
IF TIPO = IMG? => isEqual($Tipo, IMG)
  - THEN
    - EXPRESSION Diretorio = oprIf(isNullOrEmpty(ebfSQLField($ConsultaLivro, NOMEDOC_LIV)), ebfSQLField($ConsultaPar, CAMINHOIMGATO_PAR), ebfConcat(ebfSQLField($ConsultaPar, CAMINHOIMGATO_PAR), /, ebfSQLField($ConsultaLivro, NOMEDOC_LIV))) [Montar Diretório]
    - EXPRESSION = ebfSQLExecuteUpdate() [Executar Atualização "livro"]
    - EXPRESSION Diretorio = ebfConcat($Diretorio, /, $Protocolo) [Concatena caminho do livro  +  protocolo]
    - EXPRESSION Diretorio = ebfReplaceAll($Diretorio, \, /) [Trocar subsequencias]
    - CALLRULE M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade - Subfluxo
    - EXPRESSION QtdeImg = ebfListLength($ListaArquivos) [Obter Quantidade de Imagens no Diretório]
    - EXPRESSION = ebfSQLExecuteUpdate() [Esvaziar Tabela "imgatotemp"]
    - IF TIPO = IMG? => isEqual($Tipo, IMG)
      - THEN
        - WHILE Contador >= QtdeImg? => oprNot(isGreaterOrEqual($Contador, $QtdeImg))
          - EXPRESSION Contador = oprAdd($Contador, 1) [Contador + 1]
          - IF Extensão JPG? => oprOr(isEqual(ebfGetFileExtension(ebfGetElementFromList($ListaArquivos, $Contador)), jpg), isEqual(ebfGetFileExtension(ebfGetElementFromList($ListaArquivos, $Contador)), JPG), isEqual(ebfGetFileExtension(ebfGetElementFromList($ListaArquivos, $Contador)), jpeg), isEqual(ebfGetFileExtension(ebfGetElementFromList($ListaArquivos, $Contador)), JPEG))
            - THEN
              - EXPRESSION = ebfSQLExecuteUpdate() [Inserir Registros "imgatotemp"]
            - ELSE
        - CALLRULE M003 - Editor - Ao Entrar - Grade Imagens do Ato
        - EXPRESSION = ebfSQLClose($ConsultaPar) [Fechar Consulta Parametros]
        - EXPRESSION = ebfSQLClose($ConsultaLivro) [Fechar Consulta Livro]
        - EXPRESSION = ebfFormRefreshComponent(GRDIMG) [Atualiza GRADE GRDIMG]
        - END
      - ELSE
        - WHILE Contador >= QtdeImg? => oprNot(isGreaterOrEqual($Contador, $QtdeImg))
          - EXPRESSION Contador = oprAdd($Contador, 1) [Contador + 1]
          - CONNECTOR FlowConnector1
          - EXPRESSION = ebfSQLExecuteUpdate() [Inserir Registros "imgatotemp"]
        - CALLRULE M003 - Editor - Ao Entrar - Grade Imagens do Ato
        - EXPRESSION = ebfSQLClose($ConsultaPar) [Fechar Consulta Parametros]
        - EXPRESSION = ebfSQLClose($ConsultaLivro) [Fechar Consulta Livro]
        - EXPRESSION = ebfFormRefreshComponent(GRDPDF) [Atualiza GRADE GRDPDF]
        - END
  - ELSE
    - EXPRESSION Diretorio = oprIf(isNullOrEmpty(ebfSQLField($ConsultaLivro, NOMEDOC_LIV)), ebfConcat(ebfSQLField($ConsultaPar, CAMINHOPDFATO_PAR)), ebfConcat(ebfSQLField($ConsultaPar, CAMINHOPDFATO_PAR), /, ebfSQLField($ConsultaLivro, NOMEDOC_LIV))) [Montar Diretório]
    - REFERENCE FlowExpression7
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfGetUserCode` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4506`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4428`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4506`

```java
protected final Variant ebfGetUserCode() throws Exception {
        return VariantPool.get(this.getData().getUser().getCode());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4428`

```java
protected final Variant ebfGetUserCode() throws Exception {
        return VariantPool.get(this.getData().getUser().getCode());
    }
```
  - `ebfLength` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5973`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5904`

```java
protected final Variant ebfLength(Variant variant) throws Exception {
        return VariantPool.get(Integer.valueOf(variant.getString().length()).longValue());
    }
```
  - `ebfListLength` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5992`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5923`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5992`

```java
protected final Variant ebfListLength(Variant variant) throws Exception {
        Variant variant2 = VariantPool.get(0L);
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            variant2 = VariantPool.get(list.size());
        }
        return variant2;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5923`

```java
protected final Variant ebfListLength(Variant variant) throws Exception {
        Variant variant2 = VariantPool.get(0L);
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            variant2 = VariantPool.get(list.size());
        }
        return variant2;
    }
```
  - `ebfReplaceAll` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  - `ebfSQLClose` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8300`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8057`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8300`

```java
protected final Variant ebfSQLClose(Variant variant) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        if (resultSet != null) {
            this.closeDBObject(resultSet);
        }
        return Variant.VARIANT_TRUE;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8057`

```java
protected final Variant ebfSQLClose(Variant variant) throws Exception {
        ResultSet resultSet = variant.getResultSet();
        if (resultSet != null) {
            this.closeDBObject(resultSet);
        }
        return Variant.VARIANT_TRUE;
    }
```
  - `ebfSQLExecuteQuery` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9065`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8755`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8822`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:460`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:466`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1067`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1072`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1271`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:723`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:729`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`

```java
} else {
            int n = variantArray.length;
            variantArray2 = new Variant[n + 1];
            System.arraycopy(variantArray, 0, variantArray2, 0, n);
            variantArray2[n] = variant2;
        }
        return this.ebfSQLExecuteQuery(variant.getString(), variant3.getLong().intValue(), variantArray2);
    }

    protected final Variant ebfSQLDynamicScrollQuery(Variant variant, Variant variant2, Variant variant3, Variant ... variantArray) throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`

```java
protected final Variant ebfSQLExecuteQuery(String string, int n, Variant ... variantArray) throws Exception {
        Object object;
        Functions.checkInvalidCommand(string);
        Long l = System.currentTimeMillis();
        ResultSet resultSet = null;
        DBConnection dBConnection = null;
        if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)((Variant)object).getObject();
        }
        if (this.developmentMode) {
            System.out.println("### SELECT BEGIN [" + this.ruleName + "] ###");
            System.out.println(string);
            System.out.println();
```
  - `ebfSQLExecuteUpdate` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9143`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9234`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9240`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8900`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8991`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8997`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9143`

```java
protected final Variant ebfSQLExecuteUpdate(String string, int n, Variant ... variantArray) throws Exception {
        Serializable serializable;
        Long l = System.currentTimeMillis();
        PreparedStatement preparedStatement = null;
        long l2 = -1L;
        try {
            Object object;
            serializable = null;
            if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
                serializable = (DBConnection)((Variant)object).getObject();
            }
            if (this.developmentMode) {
                System.out.println("### SQL BEGIN [" + this.ruleName + "] ###");
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9234`

```java
if (this.developmentMode) {
                    System.out.println("### SQL END [" + this.ruleName + "] ###");
                    System.out.println();
                }
                DBConnection.close(preparedStatement);
                Long l3 = System.currentTimeMillis();
                MonitoringManager.getInstance().addMonitoringDatabase(this.getSystem(), this.ruleName, "ebfSQLExecuteUpdate", l, l3);
                throw throwable;
            }
        }
        DBConnection.close(preparedStatement);
        serializable = Long.valueOf(System.currentTimeMillis());
        MonitoringManager.getInstance().addMonitoringDatabase(this.getSystem(), this.ruleName, "ebfSQLExecuteUpdate", l, (Long)serializable);
```
  - `ebfSQLField` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9244`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9001`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:459`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:40`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9244`

```java
protected final Variant ebfSQLField(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        ResultSet resultSet = variant.getResultSet();
        if (this.hasData(resultSet)) {
            Object object = resultSet.getObject(variant2.getString());
            if (object instanceof TIMESTAMP) {
                TIMESTAMP tIMESTAMP = (TIMESTAMP)object;
                variant3 = VariantPool.get(tIMESTAMP.dateValue());
            } else {
                variant3 = VariantPool.get(object);
            }
        }
        return variant3;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9001`

```java
protected final Variant ebfSQLField(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        ResultSet resultSet = variant.getResultSet();
        if (this.hasData(resultSet)) {
            Object object = resultSet.getObject(variant2.getString());
            if (object instanceof TIMESTAMP) {
                TIMESTAMP tIMESTAMP = (TIMESTAMP)object;
                variant3 = VariantPool.get(tIMESTAMP.dateValue());
            } else {
                variant3 = VariantPool.get(object);
            }
        }
        return variant3;
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `oprAdd` [servidor] camada `-` tipo `-` natureza `-`
  - `oprIf` [servidor] camada `-` tipo `-` natureza `-`
  - `oprNot` [servidor] camada `-` tipo `-` natureza `-`
  - `oprOr` [servidor] camada `-` tipo `-` natureza `-`
  - `oprSubtract` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1271" NAME="M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>master</AUTHOR>
<DATE>14/08/2024 20:40:46</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<VAR TYPE="Variante" SIZE="">ListaArquivos</VAR>
<VAR TYPE="Inteiro" SIZE="">Contador</VAR>
<VAR TYPE="Inteiro" SIZE="">QtdeImg</VAR>
<VAR TYPE="Tabela" SIZE="">ConsultaPar</VAR>
<VAR TYPE="Tabela" SIZ...
```

### M003-Editor-Ao Clicar Qualificar Ato Provisório

- codigo: `826`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btnQualAtoProv:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfStopRuleExecution`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfStopRuleExecution`, `FUNCTION ebfFormGetLookupName`, `FUNCTION oprAnd`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfStopRuleExecution`, `FUNCTION ebfFormGetLookupName`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`, `FUNCTION ebfSQLGetFieldFromForm`
- fluxo tecnico linearizado:

```text
START
IF CENSEC = É nulo ou vazio? => isNullOrEmpty(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo))
  - THEN
    - EXPRESSION = ebfStopRuleExecution(Falta definir o CENSEC) [Interrompe Fluxo]
    - END
  - ELSE
    - IF Tipo de Ato = É nulo ou vazio? => isNullOrEmpty(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST))
      - THEN
        - EXPRESSION = ebfStopRuleExecution(ebfConcat(Falta definir o Tipo de Ato para o, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo), -, ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST))) [Interrompe Fluxo]
        - REFERENCE FlowEnd2
      - ELSE
        - IF Procuração ou Natureza do Ato NÃO É nulo ou vazio => oprAnd(isEqual(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST), Escritura), isNullOrEmpty(ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST)))
          - THEN
            - EXPRESSION = ebfStopRuleExecution(ebfConcat(Falta definir o Tipo de Ato para o, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo), -, ebfFormGetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST), -, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST))) [Interrompe Fluxo]
            - REFERENCE FlowEnd2
          - ELSE
            - CONNECTOR FlowConnector1
            - EXPRESSION Texto = ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto) [Obter valor do componente edttexto]
            - EXPRESSION Fim = ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtFolha) [Obter valor do componente edtfolha]
            - EXPRESSION Traslado = ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTraslado) [Obter valor do componente edttraslado]
            - CALLRULE GER Regravar Protocolo Minuta
            - CALLRULE M003-Editor-Ao Clicar Qualificar Ato Provisório validações
            - IF O Retorno é Erro? => isEqual(ebfGetElementFromList($Lista Livro, 1), Erro)
              - THEN
                - CONNECTOR FlowConnector5
                - REFERENCE FlowEnd2
              - ELSE
                - EXPRESSION dtato = ebfAppend(ebfSubstring(ebfDateToday(), 1, 2), /, ebfSubstring(ebfDateToday(), 4, 2), /, ebfSubstring(ebfDateToday(), 7, 4)) [dtato = hoje]
                - IF Existe p#DataExtenso => oprNot(isNullOrEmpty(ebfSearchSubstring(toString($Texto), p#DataExtenso)))
                  - THEN
                    - CALLRULE GER Data para Extenso
                    - EXPRESSION Texto = ebfReplaceAll(toString($Texto), p#DataExtenso, $extenso) [Substituir p#DataExtensono {Texto}]
                    - IF Existe p#Data => oprNot(isNullOrEmpty(ebfSearchSubstring(toString($Texto), p#Data)))
                      - THEN
                        - EXPRESSION Texto = ebfReplaceAll(toString($Texto), p#Data, $dtato) [Substituir p#Data no {Texto}]
                        - EXPRESSION Traslado = ebfReplaceAll(toString($Traslado), p#Data, $dtato) [Substituir p#Data no {Traslado}]
                        - EXPRESSION Fim = ebfReplaceAll(toString($Fim), p#Data, $dtato) [Substituir p#Data no {Fim}]
                        - CALLRULE SweetAlert - Notificar - Botão Executar
                        - CALLRULE GER Ler Ato (ato)
                        - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtLivro1, toLong(ebfGetElementFromList($Lista Livro, 1))) [Atualiza Livro]
```
- funcoes maker mapeadas:
  - `ebfAppend` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1078`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1035`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1078`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1035`

```java
protected final Variant ebfAppend(Variant variant, Variant ... variantArray) throws Exception {
        return variant.append(variantArray);
    }
```
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfFormatDateTime` [servidor] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3873`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3803`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3873`

```java
protected final Variant ebfFormatDateTime(Variant variant, Variant variant2) throws Exception {
        String string = "dd/MM/yyyy";
        if (!variant2.isNull().booleanValue() && variant2.getString().trim().length() > 0) {
            string = variant2.getString();
        }
        if (variant.isNull().booleanValue()) {
            return Variant.VARIANT_NULL;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(string);
        return VariantPool.get(simpleDateFormat.format(variant.getDate()));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3803`

```java
protected final Variant ebfFormatDateTime(Variant variant, Variant variant2) throws Exception {
        String string = "dd/MM/yyyy";
        if (!variant2.isNull().booleanValue() && variant2.getString().trim().length() > 0) {
            string = variant2.getString();
        }
        if (variant.isNull().booleanValue()) {
            return Variant.VARIANT_NULL;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(string);
        return VariantPool.get(simpleDateFormat.format(variant.getDate()));
    }
```
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormGetComponentValue` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfFormGetLookupName` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfGetElementFromList` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4095`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4017`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4095`

```java
protected final Variant ebfGetElementFromList(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            int n = variant2.getLong().intValue() - 1;
            if (n < 0) {
                n = 0;
            } else if (n >= list.size()) {
                n = list.size() - 1;
            }
            variant3 = VariantPool.get(list.get(n));
        }
        return variant3;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4017`

```java
protected final Variant ebfGetElementFromList(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            int n = variant2.getLong().intValue() - 1;
            if (n < 0) {
                n = 0;
            } else if (n >= list.size()) {
                n = list.size() - 1;
            }
            variant3 = VariantPool.get(list.get(n));
        }
        return variant3;
```
  - `ebfListParamsCreate` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4284`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4289`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:6001`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4206`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4211`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5932`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\sys\HTMLInterface\HTMLIntegration.java:498`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4284`

```java
for (int i = 0; i < stringArray.length; ++i) {
            variantArray[i] = VariantPool.get(stringArray[i]);
        }
        return this.ebfListParamsCreate(variantArray);
    }

    protected final Variant ebfGetInputValues() throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4289`

```java
protected final Variant ebfGetInputValues() throws Exception {
        Variant[] variantArray = this.getCurrentInputs();
        return this.ebfListParamsCreate(variantArray);
    }

    protected final Variant ebfGetJSONSessionsSystems(Variant variant) throws Exception {
```
  - `ebfReplaceAll` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  - `ebfSetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10014`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3423`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9764`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9771`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:131`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
  - `ebfSQLGetFieldFromForm` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9300`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9057`

```java
protected final Variant ebfSQLGetFieldFromForm(Variant variant, Variant variant2) throws Exception {
        WFRComponent wFRComponent = this.findComponent(variant.toString(), variant2.toString());
        if (wFRComponent != null) {
            if (!this.fields.getForm().getGuid().equals(variant.toString())) {
                ResultSet resultSet = this.client.getData().getFormResultSet(wFRComponent.getForm().getCodeStr());
                if (this.hasData(resultSet)) {
                    return VariantPool.get(resultSet.getObject(wFRComponent.getField()));
                }
            } else {
                return VariantPool.get(this.fields.getValue(wFRComponent.getField()));
            }
        }
        return Variant.VARIANT_NULL;
```
  - `ebfStopRuleExecution` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
  - `ebfSubstring` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10054`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10079`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10297`

```java
protected final Variant ebfSubstring(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        int n = string.length();
        int n2 = variant2.getLong().intValue() - 1;
        int n3 = n2 + variant3.getLong().intValue();
        n2 = n2 < 0 ? 0 : n2;
        int n4 = n3 = n3 > n ? n : n3;
        if (n2 > n || n2 >= n3) {
            return VariantPool.get("");
        }
        String string2 = "";
        try {
            string2 = string.substring(n2, n3);
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10322`

```java
protected Variant ebfSubstringInverse(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        string = this.ebfSubstring(VariantPool.get(string), VariantPool.get(1L), variant2).getString();
        stringBuilder = new StringBuilder(string);
        stringBuilder.reverse();
        string = stringBuilder.toString();
        return VariantPool.get(string);
    }

    protected final Variant ebfTextMD5(Variant variant) throws Exception {
```
  - `ebfTrim` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10380`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10137`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10380`

```java
protected final Variant ebfTrim(Variant variant) throws Exception {
        String string = variant.getString();
        return VariantPool.get(string.trim());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10137`

```java
protected final Variant ebfTrim(Variant variant) throws Exception {
        String string = variant.getString();
        return VariantPool.get(string.trim());
    }
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
  - `oprAnd` [servidor] camada `-` tipo `-` natureza `-`
  - `oprNot` [servidor] camada `-` tipo `-` natureza `-`
  - `toLong` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="826" NAME="M003-Editor-Ao Clicar Qualificar Ato Provisório" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>23/03/2026 12:39:58</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Traslado</PARAM>
<PARAM TYPE="Variante" SIZE="">Fim</PARAM>
<PARAM TYPE="Data" SIZE="">dtato</PARAM>
<PARAM TYPE="Letras" SIZE="50">NUMREC</PARAM>
<PARAM TYPE="Letras" SIZE="50"...
```

### M003-Editor-Ao Clicar-Ato

- codigo: `293`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `ARVATOS:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION isNullOrEmpty`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfStopRuleExecution`, `FUNCTION oprOr`, `FUNCTION isEqual`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION toLong`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormChangeComponentValue`
- fluxo tecnico linearizado:

```text
START
IF Campo Lista = NULL => isNullOrEmpty($Campo Lista)
  - THEN
    - END
  - ELSE
    - IF Protocolo é nulo ou vazio? => isNullOrEmpty($Protocolo)
      - THEN
        - EXPRESSION = ebfStopRuleExecution(Para iniciar um ato é necessário um protocolo sem partes já lançadas e ainda não Lavrado) [Não foi selecionado um protocolo]
        - REFERENCE FlowEnd3
      - ELSE
        - CALLRULE Ger Retornar Nomes das Partes
        - IF Ja tem partes lançadas? => oprOr(isGreater(ebfMapGetObject($Mapa, CONTADOR), 0), isEqual(ebfGetSessionAttribute(s_jalavrado, false), 1))
          - THEN
            - REFERENCE FlowExpression29
          - ELSE
            - CALLRULE GER Ler Minuta Pelo Ato (minuta_ato)
            - CALLRULE GER Ler Parametro
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto, $Campo Chave) ['edtAto - Campo Invisível...' = {Campo Chave}]
            - EXPRESSION Codigo Minuta = toLong(ebfGetElementFromList($Lista de Ato, 1)) [Busca codigo da Minuta da Lista(1)]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCodMinuta, toLong(ebfGetElementFromList($Lista de Ato, 1))) ['edtCodMinuta - Campo Invisível...' = {Campo Lista}]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtMinuta, $Campo Lista) ['edtMinuta - Campo Invisível...' = {Campo Lista}]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtCodLiv, ebfGetElementFromList($Lista de Ato, 5)) ['edtCodLiv- Campo Invisível...' = Lista de Ato [5]]
            - EXPRESSION = ebfSetSessionAttribute(s_codliv, ebfGetElementFromList($Lista de Ato, 5), false) [Cria variavel de sessão s_codliv]
            - EXPRESSION = ebfSetSessionAttribute(s_tiptipo, ebfGetElementFromList($Lista de Ato, 6), false) [Cria variavel de sessão s_tiptipo]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipTipo, ebfGetElementFromList($Lista de Ato, 6)) ['edtTipTipo - Campo Invisível...' = Lista de Ato [6]]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAtoLST, ebfGetElementFromList($Lista de Ato, 7)) ['edtTipAtoLST - Campo.' = Lista de Ato [7]]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipAto, ebfGetElementFromList($Lista de Ato, 7)) ['edtTipAto - Campo Invisível...' = Lista de Ato [7]]
            - EXPRESSION = ebfSetSessionAttribute(s_tipato, ebfGetElementFromList($Lista de Ato, 7), false) [Cria variavel de sessão s_tipato]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNatLST, ebfGetElementFromList($Lista de Ato, 8)) ['edtTipNatLST - Campo' = Lista de Ato [8]]
            - EXPRESSION = ebfSetSessionAttribute(s_tipnat, ebfGetElementFromList($Lista de Ato, 8), false) [Cria variavel de sessão s_tipnat]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtTipNat, ebfGetElementFromList($Lista de Ato, 8)) ['edtTipNat - Campo Invisível...' = Lista de Ato [8]]
            - EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, lblMinuta, ebfConcat($Campo Chave, oprIf(oprNot(isNullOrEmpty($Campo Lista)), ebfConcat(-, $Campo Lista)))) [Label MINUTA = Campo Chave + Campo Lista]
            - EXPRESSION = ebfFormSetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiTabeliao, ebfGetElementFromList($Parametros, 5)) [Alterar valor da Lista Dinamica ldtabeliao]
            - EXPRESSION = ebfFormSetLookupName({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, ldiEscrevente, CALLRULE Usuario Logado) [Alterar valor da Lista Dinamica ldResponsavel]
            - EXPRESSION Texto = ebfGetElementFromList($Lista de Ato, 3) [Texto = Lista de Ato [3]]
            - IF Exsite IniciaAto => oprNot(isNullOrEmpty(ebfSearchSubstring(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto), |IniciaAto)))
              - THEN
                - EXPRESSION Indice_1 = oprAdd(ebfIndexOf(ebfTrim(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto)), |IniciaAto=), 11) [Indice_1 = Posição |IniciaAto= no Texto]
                - EXPRESSION Indice_2 = ebfIndexOf(ebfTrim(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto)), #I|) [Indice_2 |IniciaAto=]
                - EXPRESSION Codigo Minuta = toLong(ebfSubstring(ebfTrim(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto)), $Indice_1, oprSubtract($Indice_2, $Indice_1))) [Código da Minuta = Sub Seq do Texto [indice_1 , Indice_2 - Indice_2]]
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormGetComponentValue` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfFormSetEnabled` [cliente] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - rotulo maker: `Calend\u00e1rio - Habilitar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3827`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3828`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3757`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3758`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `ebfFormSetLookupName` [servidor] camada `-` tipo `-` natureza `-`
  - `ebfGetElementFromList` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4095`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4017`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4095`

```java
protected final Variant ebfGetElementFromList(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            int n = variant2.getLong().intValue() - 1;
            if (n < 0) {
                n = 0;
            } else if (n >= list.size()) {
                n = list.size() - 1;
            }
            variant3 = VariantPool.get(list.get(n));
        }
        return variant3;
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:4017`

```java
protected final Variant ebfGetElementFromList(Variant variant, Variant variant2) throws Exception {
        Variant variant3 = Variant.VARIANT_NULL;
        List list = (List)variant.getObject();
        if (list != null && !list.isEmpty()) {
            int n = variant2.getLong().intValue() - 1;
            if (n < 0) {
                n = 0;
            } else if (n >= list.size()) {
                n = list.size() - 1;
            }
            variant3 = VariantPool.get(list.get(n));
        }
        return variant3;
```
  - `ebfIndexOf` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5158`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5080`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:5158`

```java
protected final Variant ebfIndexOf(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        int n = -1;
        if (!string2.equals("")) {
            n = string.indexOf(string2);
        }
        n = n == -1 ? 0 : ++n;
        return VariantPool.get(Long.valueOf(String.valueOf(n)));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:5080`

```java
protected final Variant ebfIndexOf(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        int n = -1;
        if (!string2.equals("")) {
            n = string.indexOf(string2);
        }
        n = n == -1 ? 0 : ++n;
        return VariantPool.get(Long.valueOf(String.valueOf(n)));
    }
```
  - `ebfReplace` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7162`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7088`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7162`

```java
protected final Variant ebfReplace(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceFirst(string, string2, string3));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7088`

```java
protected final Variant ebfReplace(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceFirst(string, string2, string3));
    }
```
  - `ebfSearchSubstring` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9728`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9485`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9728`

```java
protected final Variant ebfSearchSubstring(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        int n = -1;
        if (!string2.equals("")) {
            n = string.indexOf(string2);
        }
        return VariantPool.get(n != -1);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9485`

```java
protected final Variant ebfSearchSubstring(Variant variant, Variant variant2) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        int n = -1;
        if (!string2.equals("")) {
            n = string.indexOf(string2);
        }
        return VariantPool.get(n != -1);
    }
```
  - `ebfSetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10014`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3423`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9764`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9771`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:131`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
  - `ebfStopRuleExecution` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
  - `oprAdd` [servidor] camada `-` tipo `-` natureza `-`
  - `oprIf` [servidor] camada `-` tipo `-` natureza `-`
  - `oprNot` [servidor] camada `-` tipo `-` natureza `-`
  - `oprOr` [servidor] camada `-` tipo `-` natureza `-`
  - `oprSubtract` [servidor] camada `-` tipo `-` natureza `-`
  - `toLong` [servidor] camada `-` tipo `-` natureza `-`
  - `toString` [servidor] camada `-` tipo `-` natureza `-`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1138:    protected final Variant ebfBase64ToString(Variant variant) throws Exception {`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1203:    protected final Variant ebfBytesToString(Variant variant) throws Exception {`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4540:        return VariantPool.get(this.ebfURLDecoder(this.ebfBase64ToString(this.ebfBytesToBase64(WebrunFunctions.ebfZLIBInflate(this.ebfBase64ToBinary(VariantPool.get(string))))), Variant.VARIANT_NULL));`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4687:        stringBuilder2.append("? parent.ebfExecuteJSFromWindow(parent.mainform,'" + stringBuilder3.toString() + "')\n");`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4688:        stringBuilder2.append(": ebfExecuteJS('" + stringBuilder3.toString() + "');");`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="293" NAME="M003-Editor-Ao Clicar-Ato" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>02/12/2024 12:48:07</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Campo Chave</PARAM>
<PARAM TYPE="Letras" SIZE="50">Campo Lista</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<VAR TYPE="Variante" SIZE="">Lista de Ato</VAR>
<VAR TYPE="Letras" SIZE="99999">Texto</VAR>
<VAR TYPE="Variante" SIZE="">Lista Minuta</VAR>
<VAR TYPE="Inteiro" SIZE="">Codigo...
```

### M003-Editor-Ao Clicar-GRAVAR ATO

- codigo: `144`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `btnGravarAto:Ao Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfGetSessionAttribute`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfStopRuleExecution`, `FUNCTION isGreater`, `FUNCTION ebfFormRefreshComponent`, `FUNCTION l2sFlowRunWithMessage`, `FUNCTION ebfStopRuleExecution`
- fluxo tecnico linearizado:

```text
START
EXPRESSION Existe = ebfGetSessionAttribute(s_existeato, false) [Obter variavel de sessão s_existeato]
IF edtAto = NULO => isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto))
  - THEN
    - EXPRESSION = ebfStopRuleExecution(Minuta não definida para o Ato, caso o Ato ja tenha sido iniciado/editado, copie (CRTL+C), selecione uma minuta e cole (CRTL+V) e grave o texto.) [Erro: Escolha o Elemento]
    - END
  - ELSE
    - IF {edtprotocolo} <> NULO?
{Regravação} => isGreater(toLong(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, lstProtocolo)), 0)
      - THEN
        - EXPRESSION = ebfFormRefreshComponent(GDANOTACOES) [Atualizar GDANOTACOES]
        - EXPRESSION = l2sFlowRunWithMessage(Gravando Ato , Aguarde!, M003-Editor-Ao Clicar-GRAVAR ATO Servidor, ebfListParamsCreate($Protocolo, $Texto, $Folha do Livro, $Traslado, $Existe)) [Atualiza dados no editor]
        - END
      - ELSE
        - EXPRESSION = ebfStopRuleExecution(É necessario informar um PROTOCOLO para gravar ATO!) [Erro: Falta Protocolo!]
        - END
```
- funcoes maker mapeadas:
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfGetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10008`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3418`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3986`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9765`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:136`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:152`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:166`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3488`

```java
protected final Variant ebfFirebaseStopMonitoringData(Variant variant, Variant variant2) throws Exception {
        DatabaseReference databaseReference = (DatabaseReference)variant.getObject();
        TreeMap treeMap = (TreeMap)this.ebfGetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(true)).getObject();
        try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:4064`

```java
protected Variant ebfGetComponentProperty(Variant variant, Variant variant2, Variant variant3) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        Variant variant4 = this.ebfGetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), Variant.VARIANT_FALSE);
        if (variant3 != null && variant3.getString().length() > 0) {
            if (variant4.getObject() != null) {
                return new Variant(variant4.getObject());
            }
            if (wFRForm.getComponentByName(variant2.getString()) != null) {
                return new Variant(wFRForm.getComponentByName(variant2.getString()).g...
```
  - `ebfStopRuleExecution` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
  - `isGreater` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
  - `l2sFlowRunWithMessage` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="144" NAME="M003-Editor-Ao Clicar-GRAVAR ATO" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>22/06/2024 14:29:45</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Variante" SIZE="">Folha do Livro</PARAM>
<PARAM TYPE="Variante" SIZE="">Traslado</PARAM>
<VAR TYPE="Lógico" SIZE="">Existe</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION RETURN="Existe" COMPONENTN...
```

### M003-Editor-Ao Clicar-PARTES

- codigo: `194`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `MakerButton6:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfFormOpenForm`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormOpenForm({0EE5C3E9-873C-4972-A68A-3933054206FF}) [Abrir Formulário {PARTES}]
END
```
- funcoes maker mapeadas:
  - `ebfFormOpenForm` [cliente] camada `client` tipo `navegacao` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3707`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3708`
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }

    protected final void ebfFormOpenFormGroup() throws Exception {
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="194" NAME="M003-Editor-Ao Clicar-PARTES" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Almir Ceccon</AUTHOR>
<DATE>15/03/2020 19:03:43</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Abrir Formulário {PARTES}">
<FUNCTION NAME="Abrir Formulário" REALNAME="ebfFormOpenForm">
<PARAMS>
<CONSTANT TYPE="Formulário" NULL="FALSE"  GUID = "{0EE5C3E9-873C-4972-A68A-3933054206FF}">{0EE5C3E9-873C-4972-A68A-39330542...
```

### M003-Editor-Ao Duplo Clicar-Elemento

- codigo: `34`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `ARVELEMENTOS:Ao Duplo Clicar`
- passos do fluxo decodificado: `DECISION`, `FUNCTION ebfFormChangeComponentValue`, `FUNCTION ebfConcat`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfStopRuleExecution`, `FUNCTION oprAnd`, `FUNCTION isNullOrEmpty`, `FUNCTION ebfFormSetEnabled`, `FUNCTION ebfStopRuleExecution`, `FUNCTION oprOr`, `FUNCTION isEqual`, `FUNCTION ebfStopRuleExecution`, `FUNCTION ebfNewLine`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION isEqual`, `FUNCTION ebfFormOpenForm`, `FUNCTION ebfSetSessionAttribute`, `FUNCTION ebfFormOpenForm`, `FUNCTION ebfStopRuleExecution`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormChangeComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtElemento, $Campo Chave) ['edtElemento - Campo Invisível...' = {Campo Chave}]
CALLRULE GER Remover Variaveis de Sessao
IF Campo Chave = NULO => isNullOrEmpty($Campo Chave)
  - THEN
    - EXPRESSION = ebfStopRuleExecution(Clique no Elemento Abaixo !!!) [Erro: Clique no Elemento Abaixo !!!]
    - CONNECTOR FlowRemark5
    - CONNECTOR FlowRemark4
    - END
  - ELSE
    - IF s_livro > 0 => oprAnd(isEqual(toLong(ebfGetSessionAttribute(s_livro, false)), 0))
      - THEN
        - IF 'Texto' = NULO => isNullOrEmpty(ebfFormGetComponentValue({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto))
          - THEN
            - EXPRESSION = ebfFormSetEnabled(btnGravarAto, false) [Desabilitar 'GRAVAR ATO']
            - EXPRESSION = ebfStopRuleExecution(Não existe nenhum Texto para o ATO) [Erro: Não existe nenhum Texto para o ATO]
            - CONNECTOR FlowRemark1
            - REFERENCE FlowEnd1
          - ELSE
            - IF s_gravou = NULO => oprOr(isNullOrEmpty(ebfGetSessionAttribute(s_gravou, false)), isEqual(ebfGetSessionAttribute(s_gravou, false), N))
              - THEN
                - EXPRESSION = ebfStopRuleExecution(ebfConcat(ATENÇÃO !!!, ebfNewLine(2), É obrigatório clicar no botão "GRAVAR TEXTO" antes de Qualificar para que o rascunho seja criado com os números do Livro e Folha provisórios., ebfNewLine(1))) [Erro:ATENÇÃO !!! É Obrigatório GRAVAR ATO...]
                - END
              - ELSE
                - EXPRESSION = ebfSetSessionAttribute(s_minuta, ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtAto), false) [var ses s_minuta = 'edtAto-Campo Invisivel']
                - EXPRESSION = ebfSetSessionAttribute(s_elemento, $Campo Chave, false) [var ses s_elemento = 'edtElemento-Campo Invisivel']
                - IF Campo Chave = 'Lavrar Ato' => isEqual(ebfToLowerCase(ebfTrim($Campo Chave)), lavrar ato)
                  - THEN
                    - EXPRESSION = ebfFormOpenForm({EC7419EB-EA58-4D34-8D77-EA536A2B64C6}) [Abrir Formulário  M007 - Lavrar Ato]
                    - REFERENCE FlowRemark1
                  - ELSE
                    - EXPRESSION = ebfSetSessionAttribute(s_chamador, M003, false) [Definir Variável da Sessão 's_chamador']
                    - EXPRESSION = ebfFormOpenForm({806CE95E-621B-4975-995B-AF0AF97685BB}) [Abrir Formulário - A004 - Cliente -  Pesquisar - Clientes]
                    - REFERENCE FlowEnd1
      - ELSE
        - EXPRESSION = ebfStopRuleExecution(Ato já foi Lavrado !!!) [Erro: Ato já foi Lavrado !!!]
        - REFERENCE FlowRemark4
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfFormChangeComponentValue` [hibrida] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3642`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3656`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3712`

```java
protected final Variant ebfFormChangeComponentValue(Variant variant, Variant variant2, Variant variant3) throws Exception {
        if (variant.toString().trim().length() == 0) {
            throw new WFRRuleException("Defina um formul\u00e1rio para alterar o valor de um componente!");
        }
        if (this.fields != null && this.fields.getForm().getGuid().equals(variant.toString())) {
            WFRComponent wFRComponent = this.fields.getForm().getComponentByName(variant2.toString());
            if (wFRComponent == null) {
                throw new WFRRuleException("Componente n\u00e3o encontrado para o formul\u00e1rio escolhido!");
            }
            if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3726`

```java
if (wFRComponent.isDBAware()) {
                this.fields.changeValue(wFRComponent.getField(), variant3.toObject());
                return Variant.VARIANT_NULL;
            }
        }
        this.callClientFunction("ebfFormChangeComponentValue", variant, variant2, variant3);
        return Variant.VARIANT_NULL;
    }

    protected final Variant ebfFormChangeComponentValueAndMask(Variant variant, Variant variant2, Variant variant3) throws Exception {
```
  - `ebfFormOpenForm` [cliente] camada `client` tipo `navegacao` natureza `wrapper_client`
    - efeitos: `client_ui`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3707`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3708`
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3777`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }
```
    - trecho `java` `java_client_wrapper` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3778`

```java
protected final Variant ebfFormOpenForm(Variant variant) throws Exception {
        return this.callClientFunction("ebfFormOpenForm", variant);
    }

    protected final void ebfFormOpenFormGroup() throws Exception {
```
  - `ebfFormSetEnabled` [cliente] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - rotulo maker: `Calend\u00e1rio - Habilitar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3827`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3828`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3757`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3758`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `ebfNewLine` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:6683`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:6614`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:6683`

```java
protected final Variant ebfNewLine(Variant variant) throws WFRRuleException {
        String string = "";
        long l = variant.getLong();
        while (l-- > 0L) {
            string = string + "\n";
        }
        return VariantPool.get(string);
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:6614`

```java
protected final Variant ebfNewLine(Variant variant) throws WFRRuleException {
        String string = "";
        long l = variant.getLong();
        while (l-- > 0L) {
            string = string + "\n";
        }
        return VariantPool.get(string);
    }
```
  - `ebfSetSessionAttribute` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10010`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10014`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3423`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9764`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9767`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:9771`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRule.java:131`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3493`

```java
try {
            ChildEventListener childEventListener = (ChildEventListener)this.ebfMapGetObject(new Variant(treeMap), variant2).getObject();
            databaseReference.getRef().child(variant2.getString()).removeEventListener(childEventListener);
            this.ebfMapRemoveObject(new Variant(treeMap), variant2);
            this.ebfSetSessionAttribute(new Variant("monitoringFirebaseNodes"), new Variant(treeMap), new Variant(true));
        }
        catch (Exception exception) {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10007`

```java
protected Variant ebfSetComponentProperty(Variant variant, Variant variant2, Variant variant3, Variant variant4) throws Exception {
        WFRForm wFRForm = null;
        wFRForm = variant == Variant.VARIANT_NULL ? this.getData().getSystem().getForm(Functions.fromISOtoBASE(this.request.getParameter("formID")), this.connection) : this.getSystem().getFormByGuid(variant.getString());
        this.ebfSetSessionAttribute(new Variant(this.fields.getForm().getGuid() + "_" + variant2.getString() + "_" + variant3.getString()), new Variant(variant4.getObject()), Variant.VARIANT_FALSE);
        Variant variant5 = this.ebfGetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Variant(false));
        if (variant5.getObject() == Variant.VARIANT_NULL || variant5.getObject() == null) {
            this.ebfSetSessionAttribute(new Variant(wFRForm + "_componente_propriedade"), new Vari...
```
  - `ebfStopRuleExecution` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:10266`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:10023`

```java
protected final Variant ebfStopRuleExecution(Variant variant) throws Exception {
        throw new WFRRuleStopException(ExceptionMessage.ERROR_RULE_EXECUTION_FAILED, variant.toString());
    }
```
  - `isEqual` [servidor] camada `-` tipo `-` natureza `-`
  - `isNullOrEmpty` [servidor] camada `-` tipo `-` natureza `-`
  - `oprAnd` [servidor] camada `-` tipo `-` natureza `-`
  - `oprOr` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="34" NAME="M003-Editor-Ao Duplo Clicar-Elemento" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>11/10/2022 14:16:04</DATE>
<RANKING>5,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Campo Chave</PARAM>
<PARAM TYPE="Letras" SIZE="50">Campo Lista</PARAM>
<VAR TYPE="Inteiro" SIZE="">Livro</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression10" DESCRIPTION="'edtElemento - Campo Invisível...' = {Campo Chave}">
<FUNCTION NAME="Al...
```

### M003-Editor-Ao Sair

- codigo: `145`
- tipo inferido: `navegacao`
- exige migracao manual: `false`
- eventos que chamam a regra: `745130:Ao Sair`, `btSAIRL:Ao Clicar`, `btSAIR:Ao Clicar`, `btSAIRA:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfConcat`, `FUNCTION ebfCloseForm`
- fluxo tecnico linearizado:

```text
START
CALLRULE GER Remover Variaveis de Sessao
EXPRESSION = ebfCloseForm() [Fechar Formulario]
END
```
- funcoes maker mapeadas:
  - `ebfCloseForm` [cliente] camada `server` tipo `navegacao` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1461`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1418`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1461`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1418`

```java
protected final Variant ebfCloseForm() throws Exception {
        this.client.executeJavascript("$mainform().d.n.actExit();");
        return Variant.VARIANT_NULL;
    }
```
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="145" NAME="M003-Editor-Ao Sair" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>26/09/2024 14:27:00</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="115" NAME="GER Remover Variaveis de Sessao" RETURN="" PARAMSCOUNT="1" COMPONENTNAME="FlowSubRoutine2" DESCRIPTION="GER Remover Variaveis de Sessao">
<PARAMS>
<FUNCTION NAME="Concatenação" REALNAME="ebfConcat">
<PARAMS>
<CONSTANT TYPE="Letras" NULL="FALSE"  >s_editor</CONS...
```

### M003-Editor-Atualizar RELACAO DE ATOS

- codigo: `279`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `MakerButton2:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfFormRefreshComponent`, `FUNCTION ebfFormRefreshComponent`, `FUNCTION oprIf`, `FUNCTION ebfFormSetEnabled`, `FUNCTION ebfFormSetEnabled`, `FUNCTION oprIf`, `FUNCTION ebfFormSetEnabled`, `FUNCTION ebfFormSetEnabled`
- fluxo tecnico linearizado:

```text
START
EXPRESSION = ebfFormRefreshComponent(ARVATOS) [Atualizar 'RELAÇÃO DE ATOS']
EXPRESSION = ebfFormRefreshComponent(ARVELEMENTOS) [Atualizar 'RELAÇÃO DE ELEMENTOS']
EXPRESSION = oprIf(isNullOrEmpty(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edttexto)), ebfFormSetEnabled(btnGravarAto, false), ebfFormSetEnabled(btnGravarAto, true)) [Habilitar/Desabilitar 'GRAVAR ATO']
EXPRESSION = oprIf(isNullOrEmpty(ebfSQLGetFieldFromForm({ECC86353-9F5B-4696-BBDE-EFB5B09466B5}, edtNumeroLivro)), ebfFormSetEnabled(btnQualAtoProv, false), ebfFormSetEnabled(btnQualAtoProv, true)) [Habilitar/Desabilitar LAVRAR ATO']
END
```
- funcoes maker mapeadas:
  - `ebfFormRefreshComponent` [hibrida] camada `server` tipo `ui` natureza `core_logic`
    - efeitos: `server_state`
    - rotulo maker: `Galeria de Imagens - Atualizar Galeria`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3738`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:184`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3808`

```java
protected final Variant ebfFormRefreshComponent(Variant variant) throws Exception {
        this.client.refreshComponent(variant.getString());
        return Variant.VARIANT_NULL;
    }
```
  - `ebfFormSetEnabled` [cliente] camada `client` tipo `ui` natureza `wrapper_client`
    - efeitos: `client_ui`
    - rotulo maker: `Calend\u00e1rio - Habilitar Componente`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3827`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:3828`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3757`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:3758`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:176`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\MakerFlowComponents.java:189`

```java
static {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Novo"), "ebfActionNew");
        jsonObjectBuilder.add(Functions.reduceVariable("A\u00e7\u00e3o - Atualizar Imagem"), "ebfActionSetImage");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir Formul\u00e1rio na Se\u00e7\u00e3o"), "ebfAccordionOpenForm");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Abrir URL na Se\u00e7\u00e3o"), "ebfAccordionOpenURL");
        jsonObjectBuilder.add(Functions.reduceVariable("Accordion - Atualizar Conte\u00fado da Se\u00e7\u00e3o"), "ebfAccordionSetContent");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar Ano"), "ebfCalendarDecYear");
        jsonObjectBuilder.add(Functions.reduceVariable("Calend\u00e1rio - Decrementar...
```
  - `oprIf` [servidor] camada `-` tipo `-` natureza `-`
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="279" NAME="M003-Editor-Atualizar RELACAO DE ATOS" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>26/03/2022 19:27:45</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Atualizar 'RELAÇÃO DE ATOS'">
<FUNCTION NAME="Atualizar Componente" REALNAME="ebfFormRefreshComponent">
<PARAMS>
<CONSTANT TYPE="Componente" NULL="FALSE"  GUID = "{B1C02745-7AAF-4077-B9E7-389E12E5F138}">ARVATOS<...
```

### M003-Editor-botao impressao

- codigo: `1006`
- tipo inferido: `desconhecido`
- exige migracao manual: `true`
- eventos que chamam a regra: `BTIMPRIMIRTRASLADO:Ao Clicar`, `BTIMPRIMIRLIVRO:Ao Clicar`
- passos do fluxo decodificado: `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfSQLExecuteQuery`, `FUNCTION ebfReplaceAll`, `FUNCTION ebfConcat`, `FUNCTION ebfConcat`, `FUNCTION ebfSQLExecuteUpdate`
- fluxo tecnico linearizado:

```text
START
EXPRESSION ConsultaPar = ebfSQLExecuteQuery() [Consulta Tabela "parametros"]
EXPRESSION ConsultaAto = ebfSQLExecuteQuery() [Abrir Consulta Tabela 'ato']
EXPRESSION Texto = ebfReplaceAll(ebfSQLField($ConsultaAto, TEXTO_ATO), ebfConcat(/, ebfSQLField($ConsultaPar, CONTEXTO_PAR), /components/tinymce5/imagens), ebfConcat(ebfFileBase(), components/tinymce5/imagens)) [Trocar Diretório para Exibir no RB]
EXPRESSION = ebfSQLExecuteUpdate() [Inserir Texto no Campo "IMPRIMIR_ATO"]
CALLRULE M003-Editor-Visualizar Impressao
END
```
- funcoes maker mapeadas:
  - `ebfConcat` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:1518`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:1475`

```java
protected final Variant ebfConcat(Variant ... variantArray) throws Exception {
        int n = 0;
        for (Variant variant : variantArray) {
            n += variant.getStringSize();
        }
        StringBuilder stringBuilder = new StringBuilder(n + 1);
        for (Variant variant : variantArray) {
            variant.appendTo(stringBuilder);
        }
        return VariantPool.get(stringBuilder.toString());
    }
```
  - `ebfReplaceAll` [servidor] camada `server` tipo `sistema` natureza `core_logic`
    - efeitos: `server_state`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:7169`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:7095`

```java
protected final Variant ebfReplaceAll(Variant variant, Variant variant2, Variant variant3) throws Exception {
        String string = variant.getString();
        String string2 = variant2.getString();
        String string3 = variant3.getString();
        return VariantPool.get(this.replaceAll(string, string2, string3));
    }
```
  - `ebfSQLExecuteQuery` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9065`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8188`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8755`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8822`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:460`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompiler.java:466`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1067`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1072`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1266`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:1271`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:723`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\rules\WFRRuleCompilerAb.java:729`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8431`

```java
} else {
            int n = variantArray.length;
            variantArray2 = new Variant[n + 1];
            System.arraycopy(variantArray, 0, variantArray2, 0, n);
            variantArray2[n] = variant2;
        }
        return this.ebfSQLExecuteQuery(variant.getString(), variant3.getLong().intValue(), variantArray2);
    }

    protected final Variant ebfSQLDynamicScrollQuery(Variant variant, Variant variant2, Variant variant3, Variant ... variantArray) throws Exception {
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:8998`

```java
protected final Variant ebfSQLExecuteQuery(String string, int n, Variant ... variantArray) throws Exception {
        Object object;
        Functions.checkInvalidCommand(string);
        Long l = System.currentTimeMillis();
        ResultSet resultSet = null;
        DBConnection dBConnection = null;
        if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
            dBConnection = (DBConnection)((Variant)object).getObject();
        }
        if (this.developmentMode) {
            System.out.println("### SELECT BEGIN [" + this.ruleName + "] ###");
            System.out.println(string);
            System.out.println();
```
  - `ebfSQLExecuteUpdate` [servidor] camada `server` tipo `dados` natureza `core_logic`
    - efeitos: `database`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9143`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9234`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9240`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8900`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8991`
    - `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dop\rules\WebrunFunctions.java:8997`
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9143`

```java
protected final Variant ebfSQLExecuteUpdate(String string, int n, Variant ... variantArray) throws Exception {
        Serializable serializable;
        Long l = System.currentTimeMillis();
        PreparedStatement preparedStatement = null;
        long l2 = -1L;
        try {
            Object object;
            serializable = null;
            if (variantArray != null && ((Variant)(object = variantArray[variantArray.length - 1])).getObject() instanceof DBConnection) {
                serializable = (DBConnection)((Variant)object).getObject();
            }
            if (this.developmentMode) {
                System.out.println("### SQL BEGIN [" + this.ruleName + "] ###");
```
    - trecho `java` `java_runtime` em `D:\Fontes\Claude\Refatoracao Telas Maker\webrun\wfr_fontes_java\wfr\com\systems\system_dgs\rules\WebrunFunctions.java:9234`

```java
if (this.developmentMode) {
                    System.out.println("### SQL END [" + this.ruleName + "] ###");
                    System.out.println();
                }
                DBConnection.close(preparedStatement);
                Long l3 = System.currentTimeMillis();
                MonitoringManager.getInstance().addMonitoringDatabase(this.getSystem(), this.ruleName, "ebfSQLExecuteUpdate", l, l3);
                throw throwable;
            }
        }
        DBConnection.close(preparedStatement);
        serializable = Long.valueOf(System.currentTimeMillis());
        MonitoringManager.getInstance().addMonitoringDatabase(this.getSystem(), this.ruleName, "ebfSQLExecuteUpdate", l, (Long)serializable);
```
- trecho representativo:

```text
<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1006" NAME="M003-Editor-botao impressao" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>23/07/2022 21:59:57</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Impressao</PARAM>
<VAR TYPE="Tabela" SIZE="">ConsultaCabecalho</VAR>
<VAR TYPE="Tabela" SIZE="">ConsultaAto</VAR>
<VAR TYPE="Variante" SIZE="">Cabecalho</VAR>
<VAR TYPE="Inteiro" SIZE="">PaginaInicial</VAR>
<VAR TYPE="Inteiro" SIZE="">Pagina...
```

## 7. Fluxos Consolidados

- Nenhuma referencia explicita a fluxo foi capturada no FRZ.

## 8. Comportamentos Inferidos

- `Acao de grade e navegacao auxiliar` [acao_grade] gatilho `Ao Clicar` regra `M003 - Editor - Abrir Form C001 Filtrado`
  observacao: `Abre formularios auxiliares ou drill-down a partir da grade e do contexto atual.`
- `Acao de grade e navegacao auxiliar` [acao_grade] gatilho `Ao Clicar` regra `Geral - Abrir Formulário`
  observacao: `Abre formularios auxiliares ou drill-down a partir da grade e do contexto atual.`

## 9. Orientacao para uma IA reconstruir a tela

- Reproduzir primeiro a estrutura do formulario principal, incluindo modo de janela, navegacao, abas e estrategia de dados.
- Tratar grades, lookups e containers ricos como partes centrais da UX, e nao como componentes secundarios.
- Reimplementar cada evento com base em `evento -> regra -> funcoes maker -> fluxo provavel`.
- Se houver `ebfFlowExecute` ou referencias `fluxo:`, tratar isso como orquestracao operacional critica.
- Quando o FRZ nao expuser toda a semantica de uma regra, complementar com banco/fontes Maker antes de finalizar a migracao.

## 10. Funcoes nao encontradas na base de conhecimento Maker

- `isNullOrEmpty` ocorrencias `10` origem provavel `custom_project_function`
  regras: `M003 - Editor - Ao Modificar Lista Tipo Ato`, `M003 - Editor - Ao Sair Componente Anotacao`, `M003 - Editor - Ao clicar - Botão Editor`, `M003 - Editor - Buscar ultima folha do livro`, `M003 - Editor - Exibir Miniaturas`, `M003 - Editor - Gerar Recibo`, `M003-Editor-Ao Clicar Qualificar Ato Provisório`, `M003-Editor-Ao Clicar-Ato`, `M003-Editor-Ao Clicar-GRAVAR ATO`, `M003-Editor-Ao Duplo Clicar-Elemento`
  contexto: `745130:Ao Entrar`, `ARVATOS:Ao Clicar`, `ARVELEMENTOS:Ao Duplo Clicar`, `BTATUMINI:Ao Clicar`, `EDTTEXTO1:Ao Sair`, `MakerButton4:Ao Clicar`, `MakerButton7:Ao Clicar`, `btCalcCustas:Ao Clicar`, `btEDITORF:Ao Clicar`, `btEDITORT:Ao Clicar`, `btRecibo2:Ao Clicar`, `btnGravarAto:Ao Clicar`, `btnQualAtoProv:Ao Clicar`, `edtFolhaUsa:Ao Sair`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="903" NAME="M003 - Editor - Ao clicar - Botão Editor" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>18/06/2024 13:27:49</DATE>
<RANKING>4,0</RANKING>
</HEADER>
<VAR TYPE="Letras" SIZE="50">m_protocolo</VAR>
<VAR TYPE="Fracionado" SIZE="">Tempo</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression14" DESCRIPTION="Cria variavel de sessão">
<FUNCTION NAME="Definir variável da sessão" REALNAME="ebfSetSessionAttribute">
<PARAMS>
<...`
- `isEqual` ocorrencias `9` origem provavel `custom_project_function`
  regras: `M003 - Editor - Ao Duplo Clicar GRDIMG`, `M003 - Editor - Ao Precionar Tecla - psq_Folha`, `M003 - Editor - Ao clicar - Botão Editor`, `M003 - Editor - Buscar ultima folha do livro`, `M003 - Editor - Importar todas as imagens`, `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, `M003-Editor-Ao Clicar Qualificar Ato Provisório`, `M003-Editor-Ao Clicar-Ato`, `M003-Editor-Ao Duplo Clicar-Elemento`
  contexto: `745130:Ao Entrar`, `ARVATOS:Ao Clicar`, `ARVELEMENTOS:Ao Duplo Clicar`, `BTATUALIZAIMG1:Ao Clicar`, `BTATUALIZAIMG:Ao Clicar`, `GRDIMG:Ao Duplo Clicar`, `MakerButton1:Ao Clicar`, `btEDITORF:Ao Clicar`, `btEDITORT:Ao Clicar`, `btnQualAtoProv:Ao Clicar`, `edtFolhaUsa:Ao Sair`, `edtpsq_Folha:Ao Pressionar Tecla`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="903" NAME="M003 - Editor - Ao clicar - Botão Editor" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>18/06/2024 13:27:49</DATE>
<RANKING>4,0</RANKING>
</HEADER>
<VAR TYPE="Letras" SIZE="50">m_protocolo</VAR>
<VAR TYPE="Fracionado" SIZE="">Tempo</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression14" DESCRIPTION="Cria variavel de sessão">
<FUNCTION NAME="Definir variável da sessão" REALNAME="ebfSetSessionAttribute">
<PARAMS>
<...`
- `oprIf` ocorrencias `5` origem provavel `custom_project_function`
  regras: `M003 - Editor - Ao Duplo Clicar GRDIMG`, `M003 - Editor - Importar todas as imagens`, `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, `M003-Editor-Ao Clicar-Ato`, `M003-Editor-Atualizar RELACAO DE ATOS`
  contexto: `ARVATOS:Ao Clicar`, `BTATUALIZAIMG1:Ao Clicar`, `BTATUALIZAIMG:Ao Clicar`, `GRDIMG:Ao Duplo Clicar`, `MakerButton1:Ao Clicar`, `MakerButton2:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1273" NAME="M003 - Editor - Ao Duplo Clicar GRDIMG" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>master</AUTHOR>
<DATE>14/08/2024 17:13:11</DATE>
<RANKING>6,5</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="50">Linha</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">Tag</VAR>
<VAR TYPE="Variante" SIZE="">ListaPar</VAR>
<VAR TYPE="Letras" SIZE="50">CaminhoTMCE</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="1274" NAME="M003 - Editor - Ao Duplo Clicar...`
- `oprOr` ocorrencias `5` origem provavel `custom_project_function`
  regras: `M003 - Editor - Buscar ultima folha do livro`, `M003 - Editor - Importar todas as imagens`, `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, `M003-Editor-Ao Clicar-Ato`, `M003-Editor-Ao Duplo Clicar-Elemento`
  contexto: `ARVATOS:Ao Clicar`, `ARVELEMENTOS:Ao Duplo Clicar`, `BTATUALIZAIMG1:Ao Clicar`, `BTATUALIZAIMG:Ao Clicar`, `MakerButton1:Ao Clicar`, `edtFolhaUsa:Ao Sair`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1214" NAME="M003 - Editor - Buscar ultima folha do livro" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>14/06/2024 16:17:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">CodLivro</PARAM>
<PARAM TYPE="Inteiro" SIZE="">QtdeFolhas</PARAM>
<PARAM TYPE="Inteiro" SIZE="">FolhaInicial</PARAM>
<PARAM TYPE="Data" SIZE="">DtAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Ato</PARAM>
<PARAM TYPE="Letras" SIZE="...`
- `ebfFormGetComponentValue` ocorrencias `4` origem provavel `unknown`
  regras: `M003 - Editor - Ao Duplo Clicar GRDIMG`, `M003 - Editor - Ao clicar - Botão Gravar Dados`, `M003-Editor-Ao Clicar Qualificar Ato Provisório`, `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`, `GRDIMG:Ao Duplo Clicar`, `btGRAVAR1:Ao Clicar`, `btGRAVAR:Ao Clicar`, `btnQualAtoProv:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="968" NAME="M003 - Editor - Ao clicar - Botão Gravar Dados" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>16/03/2026 23:49:47</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodTab</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodJur</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodEsc</PARAM>
<PARAM TYPE="Letras" SIZE="50">Gratuidade</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="967" NAME...`
- `oprNot` ocorrencias `4` origem provavel `custom_project_function`
  regras: `M003 - Editor - Buscar ultima folha do livro`, `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, `M003-Editor-Ao Clicar Qualificar Ato Provisório`, `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`, `BTATUALIZAIMG1:Ao Clicar`, `BTATUALIZAIMG:Ao Clicar`, `btnQualAtoProv:Ao Clicar`, `edtFolhaUsa:Ao Sair`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1214" NAME="M003 - Editor - Buscar ultima folha do livro" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>14/06/2024 16:17:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">CodLivro</PARAM>
<PARAM TYPE="Inteiro" SIZE="">QtdeFolhas</PARAM>
<PARAM TYPE="Inteiro" SIZE="">FolhaInicial</PARAM>
<PARAM TYPE="Data" SIZE="">DtAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Ato</PARAM>
<PARAM TYPE="Letras" SIZE="...`
- `ebfFormGetLookupName` ocorrencias `3` origem provavel `unknown`
  regras: `M003 - Editor - Ao clicar - Botão Gravar Dados`, `M003 - Editor - Atualiza Rascunho - Cliente`, `M003-Editor-Ao Clicar Qualificar Ato Provisório`
  contexto: `btGRAVAR1:Ao Clicar`, `btGRAVAR:Ao Clicar`, `btQUALIFICAR:Ao Clicar`, `btnQualAtoProv:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="968" NAME="M003 - Editor - Ao clicar - Botão Gravar Dados" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>16/03/2026 23:49:47</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodTab</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodJur</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodEsc</PARAM>
<PARAM TYPE="Letras" SIZE="50">Gratuidade</PARAM>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="967" NAME...`
- `ebfGridGetValue` ocorrencias `3` origem provavel `unknown`
  regras: `M003 - Editor - Ao Duplo Clicar GRDIMG`, `M003 - Editor - Ao Sair Componente Anotacao`, `M003 - Editor - Download Arquivos do Ato`
  contexto: `EDTTEXTO1:Ao Sair`, `GRDIMG:Ao Duplo Clicar`, `GRDPDF:Ao Duplo Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1273" NAME="M003 - Editor - Ao Duplo Clicar GRDIMG" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>master</AUTHOR>
<DATE>14/08/2024 17:13:11</DATE>
<RANKING>6,5</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="50">Linha</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">Tag</VAR>
<VAR TYPE="Variante" SIZE="">ListaPar</VAR>
<VAR TYPE="Letras" SIZE="50">CaminhoTMCE</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="1274" NAME="M003 - Editor - Ao Duplo Clicar...`
- `oprAdd` ocorrencias `3` origem provavel `custom_project_function`
  regras: `M003 - Editor - Buscar ultima folha do livro`, `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`, `BTATUALIZAIMG1:Ao Clicar`, `BTATUALIZAIMG:Ao Clicar`, `edtFolhaUsa:Ao Sair`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1214" NAME="M003 - Editor - Buscar ultima folha do livro" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>14/06/2024 16:17:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">CodLivro</PARAM>
<PARAM TYPE="Inteiro" SIZE="">QtdeFolhas</PARAM>
<PARAM TYPE="Inteiro" SIZE="">FolhaInicial</PARAM>
<PARAM TYPE="Data" SIZE="">DtAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Ato</PARAM>
<PARAM TYPE="Letras" SIZE="...`
- `oprSubtract` ocorrencias `3` origem provavel `custom_project_function`
  regras: `M003 - Editor - Importar todas as imagens`, `M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade`, `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`, `BTATUALIZAIMG1:Ao Clicar`, `BTATUALIZAIMG:Ao Clicar`, `MakerButton1:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1318" NAME="M003 - Editor - Importar todas as imagens" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>05/08/2024 23:01:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Altura</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Largura</PARAM>
<VAR TYPE="Tabela" SIZE="">Consulta</VAR>
<VAR TYPE="Letras" SIZE="50">Tag</VAR>
<VAR TYPE="Tabela" SIZE="">Parametros</VAR>
<VAR TYPE="Letras" SIZE="50">CaminhoTMCE</VAR>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<...`
- `l2sFlowRunWithMessage` ocorrencias `2` origem provavel `custom_project_function`
  regras: `M003 - Editor - Atualiza Rascunho - Cliente`, `M003-Editor-Ao Clicar-GRAVAR ATO`
  contexto: `btQUALIFICAR:Ao Clicar`, `btnGravarAto:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1417" NAME="M003 - Editor - Atualiza Rascunho - Cliente" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>20/03/2026 14:18:40</DATE>
<RANKING>3,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<VAR TYPE="Letras" SIZE="50">Escrevente</VAR>
<VAR TYPE="Letras" SIZE="50">Juramentado</VAR>
<VAR TYPE="Letras" SIZE="50">Tabeliao</VAR>
<VAR TYPE="Letras" SIZE="50">UF</VAR>
<VAR TYPE="Variante" SIZE="">Lista</VAR...`
- `oprAnd` ocorrencias `2` origem provavel `custom_project_function`
  regras: `M003-Editor-Ao Clicar Qualificar Ato Provisório`, `M003-Editor-Ao Duplo Clicar-Elemento`
  contexto: `ARVELEMENTOS:Ao Duplo Clicar`, `btnQualAtoProv:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="826" NAME="M003-Editor-Ao Clicar Qualificar Ato Provisório" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>23/03/2026 12:39:58</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Traslado</PARAM>
<PARAM TYPE="Variante" SIZE="">Fim</PARAM>
<PARAM TYPE="Data" SIZE="">dtato</PARAM>
<PARAM TYPE="Letras" SIZE="50">NUMREC</PARAM>
<PARAM TYPE="Letras" SIZE="50"...`
- `toLong` ocorrencias `2` origem provavel `custom_project_function`
  regras: `M003-Editor-Ao Clicar Qualificar Ato Provisório`, `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`, `btnQualAtoProv:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="826" NAME="M003-Editor-Ao Clicar Qualificar Ato Provisório" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>23/03/2026 12:39:58</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Traslado</PARAM>
<PARAM TYPE="Variante" SIZE="">Fim</PARAM>
<PARAM TYPE="Data" SIZE="">dtato</PARAM>
<PARAM TYPE="Letras" SIZE="50">NUMREC</PARAM>
<PARAM TYPE="Letras" SIZE="50"...`
- `ebfChangeComponentValueOtherForm` ocorrencias `1` origem provavel `unknown`
  regras: `M003 - Editor - Grade Selos gerados`
  contexto: `GRSelos:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1173" NAME="M003 - Editor - Grade Selos gerados" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>03/06/2021 16:41:57</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Obtem o Selo da GRID">
<FUNCTION NAME="Alterar Valor do Componente em Outro Formulário" REALNAME="ebfChangeComponentValueOtherForm">
<PARAMS>
<CONSTANT TYPE="Formulário" NULL="FALSE"  GUID = "{ECC86353-9F5B-4696-...`
- `ebfFormSetLookupName` ocorrencias `1` origem provavel `unknown`
  regras: `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="293" NAME="M003-Editor-Ao Clicar-Ato" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>02/12/2024 12:48:07</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Campo Chave</PARAM>
<PARAM TYPE="Letras" SIZE="50">Campo Lista</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<VAR TYPE="Variante" SIZE="">Lista de Ato</VAR>
<VAR TYPE="Letras" SIZE="99999">Texto</VAR>
<VAR TYPE="Variante" SIZE="">Lista Minuta</VAR>
<VAR TYPE="Inteiro" SIZE="">Codigo...`
- `ebfGetComponentValueFromOtherForm` ocorrencias `1` origem provavel `unknown`
  regras: `M003 - Editor - Download Arquivos do Ato`
  contexto: `GRDPDF:Ao Duplo Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1276" NAME="M003 - Editor - Download Arquivos do Ato" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>11/09/2023 20:12:37</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="50">Linha</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">Nome</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION RETURN="Caminho" COMPONENTNAME="FlowExpression2" DESCRIPTION="Obter Caminho do Arquivo">
<FUNCTION NAME="Grade - Obter Valor...`
- `ebfHtmlRemoveChild` ocorrencias `1` origem provavel `unknown`
  regras: `M003 - Editor - Exibir Miniaturas`
  contexto: `BTATUMINI:Ao Clicar`, `MakerButton4:Ao Clicar`, `MakerButton7:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1324" NAME="M003 - Editor - Exibir Miniaturas" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>06/08/2022 21:18:01</DATE>
<RANKING>5,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Miniatura</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">IFrame</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<DECISION COMPONENTNAME="FlowDecision2" DESC...`
- `ebfRefreshComponentOtherForm` ocorrencias `1` origem provavel `unknown`
  regras: `M003 - Editor - Grade Selos gerados`
  contexto: `GRSelos:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1173" NAME="M003 - Editor - Grade Selos gerados" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurino</AUTHOR>
<DATE>03/06/2021 16:41:57</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION COMPONENTNAME="FlowExpression1" DESCRIPTION="Obtem o Selo da GRID">
<FUNCTION NAME="Alterar Valor do Componente em Outro Formulário" REALNAME="ebfChangeComponentValueOtherForm">
<PARAMS>
<CONSTANT TYPE="Formulário" NULL="FALSE"  GUID = "{ECC86353-9F5B-4696-...`
- `ebfRichTextInsertTextAtPosition` ocorrencias `1` origem provavel `unknown`
  regras: `M003 - Editor - Ao Duplo Clicar GRDIMG`
  contexto: `GRDIMG:Ao Duplo Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1273" NAME="M003 - Editor - Ao Duplo Clicar GRDIMG" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>master</AUTHOR>
<DATE>14/08/2024 17:13:11</DATE>
<RANKING>6,5</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="50">Linha</PARAM>
<VAR TYPE="Letras" SIZE="50">Caminho</VAR>
<VAR TYPE="Letras" SIZE="50">Tag</VAR>
<VAR TYPE="Variante" SIZE="">ListaPar</VAR>
<VAR TYPE="Letras" SIZE="50">CaminhoTMCE</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<CALLRULE ID="1274" NAME="M003 - Editor - Ao Duplo Clicar...`
- `isGreater` ocorrencias `1` origem provavel `custom_project_function`
  regras: `M003-Editor-Ao Clicar-GRAVAR ATO`
  contexto: `btnGravarAto:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="144" NAME="M003-Editor-Ao Clicar-GRAVAR ATO" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>22/06/2024 14:29:45</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<PARAM TYPE="Variante" SIZE="">Texto</PARAM>
<PARAM TYPE="Variante" SIZE="">Folha do Livro</PARAM>
<PARAM TYPE="Variante" SIZE="">Traslado</PARAM>
<VAR TYPE="Lógico" SIZE="">Existe</VAR>
<DECLARERETURN TYPE="" SIZE=""/>
<ACTION>
<EXPRESSION RETURN="Existe" COMPONENTN...`
- `isMinor` ocorrencias `1` origem provavel `custom_project_function`
  regras: `M003 - Editor - Gerar Recibo`
  contexto: `btRecibo2:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="964" NAME="M003 - Editor - Gerar Recibo" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>20/03/2026 22:23:25</DATE>
<RANKING>10,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Protocolo</PARAM>
<PARAM TYPE="Letras" SIZE="50">Livro</PARAM>
<PARAM TYPE="Letras" SIZE="50">Folha</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoCli</PARAM>
<PARAM TYPE="Letras" SIZE="50">CodCli</PARAM>
<PARAM TYPE="Letras" SIZE="50">Servico</PARAM...`
- `isMinorOrEqual` ocorrencias `1` origem provavel `custom_project_function`
  regras: `M003 - Editor - Buscar ultima folha do livro`
  contexto: `edtFolhaUsa:Ao Sair`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1214" NAME="M003 - Editor - Buscar ultima folha do livro" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>14/06/2024 16:17:50</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Inteiro" SIZE="">CodLivro</PARAM>
<PARAM TYPE="Inteiro" SIZE="">QtdeFolhas</PARAM>
<PARAM TYPE="Inteiro" SIZE="">FolhaInicial</PARAM>
<PARAM TYPE="Data" SIZE="">DtAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">TipoAto</PARAM>
<PARAM TYPE="Letras" SIZE="50">Ato</PARAM>
<PARAM TYPE="Letras" SIZE="...`
- `toDouble` ocorrencias `1` origem provavel `custom_project_function`
  regras: `M003 - Editor - Atualiza Rascunho - Cliente`
  contexto: `btQUALIFICAR:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="1417" NAME="M003 - Editor - Atualiza Rascunho - Cliente" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>20/03/2026 14:18:40</DATE>
<RANKING>3,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Tipo</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<VAR TYPE="Letras" SIZE="50">Escrevente</VAR>
<VAR TYPE="Letras" SIZE="50">Juramentado</VAR>
<VAR TYPE="Letras" SIZE="50">Tabeliao</VAR>
<VAR TYPE="Letras" SIZE="50">UF</VAR>
<VAR TYPE="Variante" SIZE="">Lista</VAR...`
- `toString` ocorrencias `1` origem provavel `custom_project_function`
  regras: `M003-Editor-Ao Clicar-Ato`
  contexto: `ARVATOS:Ao Clicar`
  motivo: `Funcao encontrada nas regras/fluxos, mas ausente do catalogo EBF e sem evidencia consolidada no runtime Webrun`
  trecho: `<?xml version="1.0" encoding="ISO-8859-1"?>
<RULE ID="293" NAME="M003-Editor-Ao Clicar-Ato" COMPONENTNAME="FlowStart1" DESCRIPTION="Inicio" DEBUG="FALSE">
<HEADER>
<AUTHOR>Paulo F Laurin</AUTHOR>
<DATE>02/12/2024 12:48:07</DATE>
<RANKING>0,0</RANKING>
</HEADER>
<PARAM TYPE="Letras" SIZE="50">Campo Chave</PARAM>
<PARAM TYPE="Letras" SIZE="50">Campo Lista</PARAM>
<PARAM TYPE="Inteiro" SIZE="">Protocolo</PARAM>
<VAR TYPE="Variante" SIZE="">Lista de Ato</VAR>
<VAR TYPE="Letras" SIZE="99999">Texto</VAR>
<VAR TYPE="Variante" SIZE="">Lista Minuta</VAR>
<VAR TYPE="Inteiro" SIZE="">Codigo...`

## 11. Metricas e Estimativa Preliminar

- formularios: `5`
- componentes: `162`
- grades/submodulos: `0`
- eventos: `47`
- regras: `31`
- fluxos: `31`
- queries: `20`
- tabelas: `9`
- funcoes conhecidas: `39`
- funcoes desconhecidas: `24`
- estimativa preliminar de pontos de funcao: `148.5`
- faixa sugerida: `126.2` a `178.2`
- observacao: `Estimativa heuristica baseada em formularios, fluxos, queries, tabelas, submodulos e funcoes desconhecidas. Revisao manual recomendada antes de uso comercial.`

## 12. Riscos e Avisos

- risco: `regra nao encontrada: M003-Lista Protocolo-Altera valor do protocolo Cliente`
- risco: `regra nao encontrada: M003 - Ato - Alterar Texto Auxiliar`
- risco: `regra nao encontrada: Geral - Abrir Formulário`
- risco: `regra nao encontrada: F001 - Recibo - Abrir Relatorio Recibo`
- risco: `metadata do banco indisponivel; usando chaves detectadas no FRZ`

## Mapa de Dependências de Fluxos

- **Abrir Formulario Filtrado - Generico**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Clicar Botao Abrir Protocolo
- **C001 - Inserir Dados Automaticamente - fin_protocolo**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Modificar Lista Tipo Ato
- **C001 - Protocolo - Ao Modificar Grade - Calcular Base FRJ**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Modificar Grade Custas
- **C001 - Protocolo - Calcular Emolumentos**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Modificar Grade Custas, M003 - Editor - Ao Modificar Lista Tipo Ato
- **C001 - Protocolo - Calcular Selos Grade Custas**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Modificar Grade Custas
- **GER - Gerar GUID - Generico**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Exibir Miniaturas
- **GER Data para Extenso**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Atualiza Rascunho - Cliente, M003-Editor-Ao Clicar Qualificar Ato Provisório
- **GER Ler Ato (ato)**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Clicar Qualificar Ato Provisório
- **GER Ler Minuta Pelo Ato (minuta_ato)**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Clicar-Ato
- **GER Ler Parametro**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Clicar-Ato
- **GER Regravar Not1**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao clicar - Botão Gravar Dados
- **GER Regravar Protocolo Minuta**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao clicar - Botão Gravar Dados, M003-Editor-Ao Clicar Qualificar Ato Provisório
- **GER Remover Variaveis de Sessao**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Duplo Clicar-Elemento, M003-Editor-Ao Sair
- **Ger Retornar Nomes das Partes**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Clicar-Ato
- **M003 - Editor - Ao clicar - Botão Gravar Dados**
  - chama: GER Regravar Not1, GER Regravar Protocolo Minuta
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Clicar Botao Abrir Protocolo**
  - chama: Abrir Formulario Filtrado - Generico
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Duplo Clicar GRDIMG**
  - chama: M003 - Editor - Ao Duplo Clicar GRDIMG - Subfluxo
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Duplo Clicar GRDIMG - Subfluxo**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Duplo Clicar GRDIMG
- **M003 - Editor - Ao Entrar - Grade Imagens do Ato**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade
- **M003 - Editor - Ao Modificar - psq_folha**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Precionar Tecla - psq_Folha
- **M003 - Editor - Ao Modificar Grade Custas**
  - chama: C001 - Protocolo - Ao Modificar Grade - Calcular Base FRJ, C001 - Protocolo - Calcular Emolumentos, C001 - Protocolo - Calcular Selos Grade Custas
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Modificar Lista Tipo Ato**
  - chama: C001 - Inserir Dados Automaticamente - fin_protocolo, C001 - Protocolo - Calcular Emolumentos
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Precionar Tecla - psq_Folha**
  - chama: M003 - Editor - Ao Modificar - psq_folha
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Sair Componente Anotacao**
  - chama: M003 - Editor - Ao Sair Componente Anotacao - Subfluxo
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Ao Sair Componente Anotacao - Subfluxo**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Ao Sair Componente Anotacao
- **M003 - Editor - Atualiza Rascunho - Cliente**
  - chama: GER Data para Extenso
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Download Arquivos do Ato**
  - chama: M003 - Editor - Download Arquivos do Ato - Subfluxo
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Download Arquivos do Ato - Subfluxo**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Download Arquivos do Ato
- **M003 - Editor - Exibir Miniaturas**
  - chama: GER - Gerar GUID - Generico, M003 - Editor - Miniatura do PDF - Gerar PDF, M003 - Miniatura do PDF - Exibir Miniaturas - Subfluxo
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Gerar Recibo**
  - chama: M003 - Editor - Gerar Recibo - Inserir Dados Reccab e Recitem, SweetAlert - Notificar - Botão Executar
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Gerar Recibo - Inserir Dados Reccab e Recitem**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Gerar Recibo
- **M003 - Editor - Importar todas as imagens**
  - chama: M003 - Editor - Importar todas as imagens - inserir no texto
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Importar todas as imagens - inserir no texto**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Importar todas as imagens
- **M003 - Editor - Miniatura do PDF - Gerar PDF**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Exibir Miniaturas
- **M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade**
  - chama: M003 - Editor - Ao Entrar - Grade Imagens do Ato, M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade - Subfluxo
  - chamado por: Não detectado — revisão manual recomendada
- **M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade - Subfluxo**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Percorrer Diretorio de Imagens e Exbir na Grade
- **M003 - Miniatura do PDF - Exibir Miniaturas - Subfluxo**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Exibir Miniaturas
- **M003-Editor-Ao Clicar Qualificar Ato Provisório**
  - chama: GER Data para Extenso, GER Ler Ato (ato), GER Regravar Protocolo Minuta, M003-Editor-Ao Clicar Qualificar Ato Provisório validações, SweetAlert - Notificar - Botão Executar
  - chamado por: Não detectado — revisão manual recomendada
- **M003-Editor-Ao Clicar Qualificar Ato Provisório validações**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Clicar Qualificar Ato Provisório
- **M003-Editor-Ao Clicar-Ato**
  - chama: GER Ler Minuta Pelo Ato (minuta_ato), GER Ler Parametro, Ger Retornar Nomes das Partes, Usuario Logado) [Alterar valor da Lista Dinamica ldResponsavel]
  - chamado por: Não detectado — revisão manual recomendada
- **M003-Editor-Ao Duplo Clicar-Elemento**
  - chama: GER Remover Variaveis de Sessao
  - chamado por: Não detectado — revisão manual recomendada
- **M003-Editor-Ao Sair**
  - chama: GER Remover Variaveis de Sessao
  - chamado por: Não detectado — revisão manual recomendada
- **M003-Editor-botao impressao**
  - chama: M003-Editor-Visualizar Impressao
  - chamado por: Não detectado — revisão manual recomendada
- **M003-Editor-Visualizar Impressao**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-botao impressao
- **SweetAlert - Notificar - Botão Executar**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003 - Editor - Gerar Recibo, M003-Editor-Ao Clicar Qualificar Ato Provisório
- **Usuario Logado) [Alterar valor da Lista Dinamica ldResponsavel]**
  - chama: Não detectado — revisão manual recomendada
  - chamado por: M003-Editor-Ao Clicar-Ato

## Tipagem Sugerida dos Campos

Não detectado — revisão manual recomendada.

## Prompt para Geração de Código via IA

```prompt
Você é uma IA senior especializada em migração de sistemas Maker 5/WebRun 5 para Go e TypeScript.

Gere uma implementação inicial em Go para o módulo abaixo, usando arquitetura Handler → Service → Repository.
O sistema deve ser multi-tenant e todas as queries devem filtrar por codi_empre quando esse campo existir.
A autenticação deve usar JWT e o tenant deve ser obtido do contexto autenticado.

Dados reais extraídos do FRZ:
- Nome do módulo: M003 - Editor
- Tabela principal: Não detectado — revisão manual recomendada
- Chave primária detectada: Não detectado — revisão manual recomendada
- Formulários detectados: 5
- Fluxos detectados: 31
- Regras detectadas: 31
- Funções desconhecidas: 24
- Pontos de função estimados: 148.5
- Score de migração: 0

Subformulários e grades detectados:
- Não detectado — revisão manual recomendada

Funções desconhecidas detectadas no FRZ:
- l2sFlowRunWithMessage

Integrações críticas identificadas:
- l2sFlowRunWithMessage

Requisitos de implementação:
- Criar handlers HTTP para listar, consultar, criar, atualizar e excluir registros quando fizer sentido.
- Criar service com validações de negócio e pontos claros para reimplementar os fluxos Maker.
- Criar repository com SQL parametrizado, transações quando necessário e filtro obrigatório por codi_empre.
- Modelar a chave primária composta quando houver mais de um campo na chave.
- Tratar subformulários como agregados ou endpoints filhos do módulo principal.
- Isolar integrações fiscais, boletos, impressão, marketplace e chamadas externas atrás de interfaces.
- Para funções desconhecidas, gerar stubs explícitos com TODO e comentário de revisão manual.
- Não inventar regra fiscal ou financeira ausente no FRZ; criar pontos de extensão documentados.
- Gerar código claro, compilável e organizado por pacotes.

Entregue primeiro a estrutura de arquivos proposta e depois o código principal.
```

## Cobertura do Catálogo de Funções

- funções distintas no FRZ: `63`
- funções catalogadas: `62`
- funções inferidas: `0`
- funções ainda desconhecidas: `1`
- integrações críticas identificadas: `0`

### Por origem

- `native`: `62`

### Por categoria

- `collection`: `4`
- `conversion`: `3`
- `database`: `7`
- `date`: `1`
- `flow`: `9`
- `grid`: `1`
- `math`: `2`
- `operator`: `11`
- `string`: `6`
- `system`: `2`
- `ui`: `11`
- `unknown`: `5`

### Funções ainda não catalogadas

- `l2sFlowRunWithMessage`
