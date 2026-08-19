type ApiErrorResponse = {
  status?: number;
  erro?: string;
  mensagem?: string;
  timestamp?: string;
};

export async function readErrorMessage(response: Response) {
  const contentType = response.headers.get('content-type');

  if (contentType?.includes('application/json')) {
    const errorBody = (await response.json()) as ApiErrorResponse;
    return errorBody.mensagem ?? 'Não foi possível concluir a requisição.';
  }

  return (await response.text()) || 'Não foi possível concluir a requisição.';
}
