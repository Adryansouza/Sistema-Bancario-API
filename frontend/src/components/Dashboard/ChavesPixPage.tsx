import { FormEvent, ReactNode, useEffect, useState } from 'react';
import { cadastrarChavePix, listarChavesPix } from '../../services/chavePix/chavePixService';
import type { ChavePixResponse, TipoChavePix } from '../../services/chavePix/chavePixTypes';
import type { RequestStatus } from '../../types/statusTypes';

type ChavesPixPageProps = {
  contaId?: number;
  onBack: () => void;
};

const keyOptions = [
  {
    type: 'CPF',
    title: 'Chave de CPF',
    description: 'Use seu CPF para receber transferências PIX.',
    formTitle: 'Cadastrar CPF',
    label: 'CPF',
    placeholder: 'Digite seu CPF',
    helper: 'Informe apenas os números do CPF.',
    icon: <CpfIcon />,
  },
  {
    type: 'TELEFONE',
    title: 'Chave de celular',
    description: 'Use seu número de celular para receber transferências PIX.',
    formTitle: 'Cadastrar celular',
    label: 'Celular',
    placeholder: 'Digite seu número de celular',
    helper: 'Informe o celular com DDD.',
    icon: <PhoneIcon />,
  },
  {
    type: 'EMAIL',
    title: 'Chave de e-mail',
    description: 'Use seu endereço de e-mail para receber transferências PIX.',
    formTitle: 'Cadastrar e-mail',
    label: 'E-mail',
    placeholder: 'Digite seu e-mail',
    helper: 'Use um e-mail válido para receber transferências.',
    icon: <EmailIcon />,
  },
] satisfies KeyOption[];

type KeyOption = {
  type: TipoChavePix;
  title: string;
  description: string;
  formTitle: string;
  label: string;
  placeholder: string;
  helper: string;
  icon: ReactNode;
};

export function ChavesPixPage({ contaId, onBack }: ChavesPixPageProps) {
  const [selectedOption, setSelectedOption] = useState<KeyOption | null>(null);
  const [keyValue, setKeyValue] = useState('');
  const [status, setStatus] = useState<RequestStatus>('idle');
  const [message, setMessage] = useState('');
  const [chaves, setChaves] = useState<ChavePixResponse[]>([]);
  const [mostrarCadastro, setMostrarCadastro] = useState(false);

  useEffect(() => {
    if (!contaId) return;
    listarChavesPix(contaId)
      .then(setChaves)
      .catch((error) => {
        setStatus('error');
        setMessage(error instanceof Error ? error.message : 'Não foi possível carregar suas chaves PIX.');
      });
  }, [contaId]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus('loading');
    setMessage('');

    if (!contaId) {
      setStatus('error');
      setMessage('Não foi possível identificar a conta para cadastrar a chave.');
      return;
    }

    if (!selectedOption || !keyValue.trim()) {
      setStatus('error');
      setMessage('Informe a chave que deseja cadastrar.');
      return;
    }

    try {
      const chaveCadastrada = await cadastrarChavePix({
        conta_id: contaId,
        tipo_chave: selectedOption.type,
        valor_chave: keyValue.trim(),
      });

      setStatus('success');
      setMessage('Chave cadastrada com sucesso.');
      setKeyValue('');
      setChaves((atuais) => [chaveCadastrada, ...atuais]);
      setSelectedOption(null);
      setMostrarCadastro(false);
    } catch (error) {
      setStatus('error');
      setMessage(error instanceof Error ? error.message : 'Erro inesperado ao cadastrar chave.');
    }
  }

  function voltar() {
    if (selectedOption) {
      setSelectedOption(null);
      setKeyValue('');
      setStatus('idle');
      setMessage('');
      return;
    }

    if (mostrarCadastro) {
      setMostrarCadastro(false);
      setStatus('idle');
      setMessage('');
      return;
    }

    onBack();
  }

  if (selectedOption) {
    return (
      <div className="pix-keys-page">
        <section className="pix-keys-card pix-key-register-card" aria-labelledby="pix-key-register-title">
          <button className="pix-back-button" type="button" onClick={voltar}>
            &lt; Voltar
          </button>

          <header className="pix-key-register-header">
            <span className="pix-key-icon pix-key-register-icon" aria-hidden="true">
              {selectedOption.icon}
            </span>
            <div>
              <h1 id="pix-key-register-title">{selectedOption.formTitle}</h1>
              <p>{selectedOption.description}</p>
            </div>
          </header>

          <form className="pix-key-form" onSubmit={handleSubmit}>
            <label htmlFor="pix-key-value">{selectedOption.label}</label>
            <input
              id="pix-key-value"
              type={selectedOption.type === 'EMAIL' ? 'email' : 'text'}
              inputMode={selectedOption.type === 'EMAIL' ? 'email' : 'numeric'}
              placeholder={selectedOption.placeholder}
              value={keyValue}
              onChange={(event) => setKeyValue(event.target.value)}
            />
            <span>{selectedOption.helper}</span>

            <button className="pix-continue-button" type="submit" disabled={status === 'loading'}>
              {status === 'loading' ? 'Cadastrando...' : 'Cadastrar chave'}
            </button>

            {message && (
              <p className={`pix-key-message pix-key-message-${status}`} role="status">
                {message}
              </p>
            )}
          </form>
        </section>
      </div>
    );
  }

  return (
    <div className="pix-keys-page">
      <section className="pix-keys-card" aria-labelledby="pix-keys-title">
        <button className="pix-back-button" type="button" onClick={voltar}>
          &lt; Voltar
        </button>

        <header className="pix-keys-header">
          <h1 id="pix-keys-title">Minhas chaves PIX</h1>
          <p>Consulte as chaves cadastradas nesta conta.</p>
        </header>

        <button className="pix-continue-button pix-new-key-button" type="button" onClick={() => setMostrarCadastro((mostrar) => !mostrar)}>
          {mostrarCadastro ? 'Cancelar cadastro' : 'Cadastrar outra chave'}
        </button>

        {mostrarCadastro && (
          <div className="pix-key-options">
            {keyOptions.map((option) => (
              <button className="pix-key-option" type="button" key={option.title} onClick={() => setSelectedOption(option)}>
                <span className="pix-key-icon" aria-hidden="true">{option.icon}</span>
                <span><strong>{option.title}</strong><small>{option.description}</small></span>
                <b aria-hidden="true">&gt;</b>
              </button>
            ))}
          </div>
        )}

        <section className="registered-keys" aria-labelledby="registered-keys-title">
          <h2 id="registered-keys-title">Chaves cadastradas</h2>
          {chaves.length === 0 ? (
            <p className="registered-keys-empty">Nenhuma chave cadastrada nesta conta.</p>
          ) : chaves.map((chave) => (
            <article className="registered-key-row" key={chave.id}>
              <span className="pix-key-icon" aria-hidden="true">{keyOptions.find((option) => option.type === chave.tipoChave)?.icon}</span>
              <span><strong>{labelTipoChave(chave.tipoChave)}</strong><small>{chave.valorChave}</small></span>
            </article>
          ))}
        </section>

        {message && <p className={`pix-key-message pix-key-message-${status}`} role="status">{message}</p>}
      </section>
    </div>
  );
}

function labelTipoChave(tipo: TipoChavePix) {
  return { CPF: 'CPF', TELEFONE: 'Celular', EMAIL: 'E-mail' }[tipo];
}

function CpfIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-hidden="true">
      <rect x="3.5" y="5" width="17" height="14" rx="2" />
      <circle cx="9" cy="10" r="1.6" />
      <path d="M6.8 15c.5-1.3 1.2-2 2.2-2s1.7.7 2.2 2" />
      <path d="M13.5 9h4" />
      <path d="M13.5 12h4" />
      <path d="M13.5 15h4" />
    </svg>
  );
}

function PhoneIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-hidden="true">
      <rect x="7" y="3.5" width="10" height="17" rx="2" />
      <path d="M10 6.5h4" />
      <path d="M11 17.5h2" />
    </svg>
  );
}

function EmailIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-hidden="true">
      <rect x="3.5" y="6" width="17" height="12" rx="2" />
      <path d="m5 8 7 5 7-5" />
    </svg>
  );
}
