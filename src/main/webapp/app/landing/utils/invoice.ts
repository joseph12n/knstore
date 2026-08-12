import axios from 'axios';

/**
 * Descarga el PDF de una factura usando el navegador (blob + enlace temporal).
 */
export const downloadFacturaPdf = async (facturaId: string, prefijo?: string | null): Promise<void> => {
  const response = await axios.get<Blob>(`api/facturas/${facturaId}/pdf`, { responseType: 'blob' });
  const url = globalThis.URL.createObjectURL(response.data);
  const link = document.createElement('a');
  const filename = `${prefijo || 'FAC'}-${facturaId}.pdf`;
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  globalThis.URL.revokeObjectURL(url);
};
