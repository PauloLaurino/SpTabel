import React, { useState, useEffect, useMemo } from 'react';
/* eslint-disable react-hooks/exhaustive-deps */
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import './RelatorioCrud.css';

// Opções fixas
const clientesFornecedores = [
  { value: 'clientes', label: 'Clientes' },
  { value: 'fornecedores', label: 'Fornecedores' },
];
const faixasAtraso = [
  { value: '', label: 'Todas' },
  { value: '30', label: '30 dias' },
  { value: '60', label: '60 dias' },
  { value: '90', label: '90 dias' },
  { value: '120', label: '120 dias' },
  { value: '150', label: '150 dias' },
  { value: '190', label: '190 dias' },
];

const RelatorioCrud: React.FC = () => {
  // Arquivo antigo movido para Depreciados
  return (
    <div className="deprecated-note">
      <h3>RelatorioCrud (OLD) - Arquivo movido para Depreciados</h3>
      <p>Este arquivo foi movido para evitar conflitos de build. Consulte a versão atual em <strong>RelatorioCrud.tsx</strong>.</p>
    </div>
  );
};

export default RelatorioCrud;
