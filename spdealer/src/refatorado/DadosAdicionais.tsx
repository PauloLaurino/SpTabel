import React from 'react';
import styled from 'styled-components';

export interface DadosAdicionaisData {
  trib_cli?: string;
  precsub_cli?: string;
  codativ1_cli?: string;
  codativ2_cli?: string;
  codativ3_cli?: string;
  codativ4_cli?: string;
}

interface DadosAdicionaisProps {
  value: DadosAdicionaisData;
  onChange: (next: DadosAdicionaisData) => void;
}

const Container = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;

const Label = styled.label`
  font-size: 13px;
  font-weight: 600;
  color: #475569;
`;

const Input = styled.input`
  padding: 10px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 14px;
  
  &:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
`;

const Select = styled.select`
  padding: 10px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
`;

const DadosAdicionais: React.FC<DadosAdicionaisProps> = ({ value, onChange }) => {
  const handleChange = (field: keyof DadosAdicionaisData, val: string) => {
    onChange({ ...value, [field]: val });
  };

  return (
    <Container>
      <FormGroup>
        <Label>Tributação</Label>
        <Select 
          value={value.trib_cli || ''} 
          onChange={(e) => handleChange('trib_cli', e.target.value)}
        >
          <option value="">Selecione</option>
          <option value="T">Tributado</option>
          <option value="I">Isento</option>
          <option value="F">Subst. Tributária</option>
        </Select>
      </FormGroup>

      <FormGroup>
        <Label>Preço Subst. Tributária (R$)</Label>
        <Input 
          type="number" 
          step="0.01"
          value={value.precsub_cli || ''} 
          onChange={(e) => handleChange('precsub_cli', e.target.value)}
          placeholder="0,00"
        />
      </FormGroup>

      <FormGroup>
        <Label>Atividade Principal</Label>
        <Input 
          value={value.codativ1_cli || ''} 
          onChange={(e) => handleChange('codativ1_cli', e.target.value)}
          placeholder="Cód. Atividade 1"
        />
      </FormGroup>

      <FormGroup>
        <Label>Atividade Secundária 1</Label>
        <Input 
          value={value.codativ2_cli || ''} 
          onChange={(e) => handleChange('codativ2_cli', e.target.value)}
        />
      </FormGroup>

      <FormGroup>
        <Label>Atividade Secundária 2</Label>
        <Input 
          value={value.codativ3_cli || ''} 
          onChange={(e) => handleChange('codativ3_cli', e.target.value)}
        />
      </FormGroup>

      <FormGroup>
        <Label>Atividade Secundária 3</Label>
        <Input 
          value={value.codativ4_cli || ''} 
          onChange={(e) => handleChange('codativ4_cli', e.target.value)}
        />
      </FormGroup>
    </Container>
  );
};

export default DadosAdicionais;
