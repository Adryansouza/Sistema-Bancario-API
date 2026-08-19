import { FormEvent, useState } from 'react';
import { cadastrarPessoaFisica, cadastrarPessoaJuridica } from '../../services/cadastro/cadastroService';
import type { CadastroPessoaFisicaRequest, CadastroPessoaJuridicaRequest } from '../../services/cadastro/cadastroTypes';
import type { RequestStatus } from '../../types/statusTypes';
import { cadastroInicial, CadastroForm, CadastroTipo } from './cadastroFormTypes';
import { estadosBrasileiros } from './estadosBrasileiros';
import { PasswordField } from './PasswordField';
import { isSenhaValida } from './passwordUtils';

type CadastroModalProps = {
  onClose: () => void;
  onCadastroSuccess: (documento: string, senha: string) => void;
};

export function CadastroModal({ onClose, onCadastroSuccess }: CadastroModalProps) {
  const [tipoCadastro, setTipoCadastro] = useState<CadastroTipo>('CPF');
  const [cadastroForm, setCadastroForm] = useState<CadastroForm>(cadastroInicial);
  const [cadastroStatus, setCadastroStatus] = useState<RequestStatus>('idle');
  const [cadastroMessage, setCadastroMessage] = useState('');
  const [mostrarSenhaCadastro, setMostrarSenhaCadastro] = useState(false);
  const [mostrarConfirmarSenhaCadastro, setMostrarConfirmarSenhaCadastro] = useState(false);

  const senhaCadastroValida = isSenhaValida(cadastroForm.senha);
  const confirmarSenhaCadastroValida =
    isSenhaValida(cadastroForm.confirmarSenha) && cadastroForm.confirmarSenha === cadastroForm.senha;

  async function handleCadastro(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setCadastroStatus('loading');
    setCadastroMessage('');

    if (!senhaCadastroValida) {
      setCadastroStatus('error');
      setCadastroMessage('A senha deve conter exatamente 8 números.');
      return;
    }

    if (!isSenhaValida(cadastroForm.confirmarSenha)) {
      setCadastroStatus('error');
      setCadastroMessage('Confirme a senha digitando os mesmos 8 números.');
      return;
    }

    if (cadastroForm.senha !== cadastroForm.confirmarSenha) {
      setCadastroStatus('error');
      setCadastroMessage('As senhas digitadas não conferem.');
      return;
    }

    try {
      if (tipoCadastro === 'CPF') {
        const request: CadastroPessoaFisicaRequest = {
          nome: cadastroForm.nome,
          cpf: cadastroForm.cpf,
          telefone: cadastroForm.telefone,
          cep: cadastroForm.cep,
          endereco: cadastroForm.endereco,
          numero: cadastroForm.numero,
          uf: cadastroForm.uf,
          senha: cadastroForm.senha,
        };

        await cadastrarPessoaFisica(request);
        onCadastroSuccess(cadastroForm.cpf, cadastroForm.senha);
      } else {
        const request: CadastroPessoaJuridicaRequest = {
          nome: cadastroForm.nome,
          cnpj: cadastroForm.cnpj,
          razaoSocial: cadastroForm.razaoSocial,
          telefone: cadastroForm.telefone,
          cep: cadastroForm.cep,
          endereco: cadastroForm.endereco,
          numero: cadastroForm.numero,
          uf: cadastroForm.uf,
          senha: cadastroForm.senha,
        };

        await cadastrarPessoaJuridica(request);
        onCadastroSuccess(cadastroForm.cnpj, cadastroForm.senha);
      }
    } catch (error) {
      setCadastroStatus('error');
      setCadastroMessage(error instanceof Error ? error.message : 'Erro inesperado ao criar conta.');
    }
  }

  function atualizarCadastro(campo: keyof CadastroForm, valor: string) {
    setCadastroForm((formAtual) => ({
      ...formAtual,
      [campo]: valor,
    }));
  }

  return (
    <div className="modal-backdrop" role="presentation">
      <section className="cadastro-modal" aria-modal="true" aria-labelledby="cadastro-title" role="dialog">
        <div className="modal-header">
          <div>
            <span>Criar conta</span>
            <h2 id="cadastro-title">Cadastro</h2>
          </div>
          <button className="modal-close" type="button" aria-label="Fechar cadastro" onClick={onClose}>
            x
          </button>
        </div>

        <div className="cadastro-toggle" aria-label="Tipo de cadastro">
          <button
            className={tipoCadastro === 'CPF' ? 'cadastro-toggle-active' : ''}
            type="button"
            onClick={() => setTipoCadastro('CPF')}
          >
            CPF
          </button>
          <button
            className={tipoCadastro === 'CNPJ' ? 'cadastro-toggle-active' : ''}
            type="button"
            onClick={() => setTipoCadastro('CNPJ')}
          >
            CNPJ
          </button>
        </div>

        <form className="cadastro-form" onSubmit={handleCadastro}>
          <div className="identificacao-grid">
            <div className="field-group">
              <label htmlFor="cadastroNome">{tipoCadastro === 'CPF' ? 'Nome completo' : 'Nome da empresa'}</label>
              <input
                id="cadastroNome"
                type="text"
                value={cadastroForm.nome}
                onChange={(event) => atualizarCadastro('nome', event.target.value)}
              />
            </div>

            {tipoCadastro === 'CPF' ? (
              <div className="field-group">
                <label htmlFor="cadastroCpf">CPF</label>
                <input
                  id="cadastroCpf"
                  type="text"
                  inputMode="numeric"
                  value={cadastroForm.cpf}
                  onChange={(event) => atualizarCadastro('cpf', event.target.value)}
                />
              </div>
            ) : (
              <div className="field-group">
                <label htmlFor="cadastroCnpj">CNPJ</label>
                <input
                  id="cadastroCnpj"
                  type="text"
                  inputMode="numeric"
                  value={cadastroForm.cnpj}
                  onChange={(event) => atualizarCadastro('cnpj', event.target.value)}
                />
              </div>
            )}
          </div>

          {tipoCadastro === 'CNPJ' && (
              <div className="field-group">
                <label htmlFor="cadastroRazaoSocial">Razão social</label>
                <input
                  id="cadastroRazaoSocial"
                  type="text"
                  value={cadastroForm.razaoSocial}
                  onChange={(event) => atualizarCadastro('razaoSocial', event.target.value)}
                />
              </div>
          )}

          <div className="contato-grid">
            <div className="field-group">
              <label htmlFor="cadastroTelefone">Telefone</label>
              <input
                id="cadastroTelefone"
                type="text"
                inputMode="numeric"
                value={cadastroForm.telefone}
                onChange={(event) => atualizarCadastro('telefone', event.target.value)}
              />
            </div>

            <div className="field-group">
              <label htmlFor="cadastroCep">CEP</label>
              <input
                id="cadastroCep"
                type="text"
                inputMode="numeric"
                value={cadastroForm.cep}
                onChange={(event) => atualizarCadastro('cep', event.target.value)}
              />
            </div>
          </div>

          <div className="endereco-grid">
            <div className="field-group">
              <label htmlFor="cadastroEndereco">Endereço</label>
              <input
                id="cadastroEndereco"
                type="text"
                value={cadastroForm.endereco}
                onChange={(event) => atualizarCadastro('endereco', event.target.value)}
              />
            </div>

            <div className="field-group">
              <label htmlFor="cadastroNumero">Número</label>
              <input
                id="cadastroNumero"
                type="text"
                value={cadastroForm.numero}
                onChange={(event) => atualizarCadastro('numero', event.target.value)}
              />
            </div>

            <div className="field-group">
              <label htmlFor="cadastroUf">UF</label>
              <select id="cadastroUf" value={cadastroForm.uf} onChange={(event) => atualizarCadastro('uf', event.target.value)}>
                <option value="">Selecione</option>
                {estadosBrasileiros.map((estado) => (
                  <option key={estado.sigla} value={estado.sigla}>
                    {estado.sigla} - {estado.nome}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="senha-grid">
            <div className="field-group">
              <label htmlFor="cadastroSenha">Senha</label>
              <PasswordField
                id="cadastroSenha"
                value={cadastroForm.senha}
                visible={mostrarSenhaCadastro}
                toggleLabel={mostrarSenhaCadastro ? 'Esconder senha' : 'Mostrar senha'}
                onChange={(value) => atualizarCadastro('senha', value)}
                onToggle={() => setMostrarSenhaCadastro((mostrar) => !mostrar)}
              />
              <span className={`password-hint ${senhaCadastroValida ? 'password-hint-valid' : ''}`}>Apenas 8 números</span>
            </div>

            <div className="field-group">
              <label htmlFor="cadastroConfirmarSenha">Confirmar senha</label>
              <PasswordField
                id="cadastroConfirmarSenha"
                value={cadastroForm.confirmarSenha}
                visible={mostrarConfirmarSenhaCadastro}
                toggleLabel={mostrarConfirmarSenhaCadastro ? 'Esconder senha' : 'Mostrar senha'}
                onChange={(value) => atualizarCadastro('confirmarSenha', value)}
                onToggle={() => setMostrarConfirmarSenhaCadastro((mostrar) => !mostrar)}
              />
              <span className={`password-hint ${confirmarSenhaCadastroValida ? 'password-hint-valid' : ''}`}>
                Repita os mesmos 8 números
              </span>
            </div>
          </div>

          <button className="login-button" type="submit" disabled={cadastroStatus === 'loading'}>
            {cadastroStatus === 'loading' ? 'Criando...' : 'Criar conta'}
          </button>

          {cadastroMessage && (
            <p className={`login-message login-message-${cadastroStatus}`} role="status">
              {cadastroMessage}
            </p>
          )}
        </form>
      </section>
    </div>
  );
}
