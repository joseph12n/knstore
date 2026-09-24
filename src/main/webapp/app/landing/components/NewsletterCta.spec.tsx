import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { toast } from 'react-toastify';

import NewsletterCta from './NewsletterCta';

vi.mock('react-toastify', () => ({ toast: { error: vi.fn(), success: vi.fn() } }));

describe('NewsletterCta', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  it('muestra un error cuando el correo es inválido', () => {
    const { container } = render(<NewsletterCta />);

    fireEvent.change(screen.getByPlaceholderText('Tu correo electrónico'), { target: { value: 'correo-invalido' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(toast.error).toHaveBeenCalledWith('Ingresa un correo válido.');
    expect(toast.success).not.toHaveBeenCalled();
  });

  it('muestra un mensaje de éxito y limpia el input cuando el correo es válido', () => {
    const { container } = render(<NewsletterCta />);
    const input = screen.getByPlaceholderText('Tu correo electrónico') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'ana@example.com' } });
    fireEvent.submit(container.querySelector('form')!);

    expect(toast.success).toHaveBeenCalledWith('¡Gracias! Te contactaremos pronto.');
    expect(toast.error).not.toHaveBeenCalled();
    expect(input.value).toBe('');
  });
});
