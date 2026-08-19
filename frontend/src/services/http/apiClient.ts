import { readErrorMessage } from './apiError';

const API_BASE_URL = import.meta.env.VITE_API_URL ?? '/api';

type HttpMethod = 'GET' | 'POST' | 'PATCH' | 'PUT' | 'DELETE';

async function request<TResponse, TBody = unknown>(path: string, method: HttpMethod, body?: TBody) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response));
  }

  return response.json() as Promise<TResponse>;
}

export function apiPost<TResponse, TBody = unknown>(path: string, body: TBody) {
  return request<TResponse, TBody>(path, 'POST', body);
}

export function apiGet<TResponse>(path: string) {
  return request<TResponse>(path, 'GET');
}

export function apiPatch<TResponse, TBody = unknown>(path: string, body: TBody) {
  return request<TResponse, TBody>(path, 'PATCH', body);
}
