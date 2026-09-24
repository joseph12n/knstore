import axios from 'axios';

interface ApiErrorBody {
  detail?: string;
  message?: string;
  title?: string;
}

/**
 * Extrae el mensaje de error de una respuesta Axios (Problem Details de JHipster)
 * con type guard, sin recurrir a `any`.
 */
export const getApiErrorMessage = (error: unknown, fallback = 'Error desconocido'): string => {
  if (axios.isAxiosError<ApiErrorBody>(error)) {
    const detail = error.response?.data?.detail;
    if (detail) {
      return detail;
    }
    const message = error.response?.data?.message;
    if (message) {
      return message;
    }
    if (error.message) {
      return error.message;
    }
  }
  if (error instanceof Error && error.message) {
    return error.message;
  }
  return fallback;
};
