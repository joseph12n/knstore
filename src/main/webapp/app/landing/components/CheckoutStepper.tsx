import React from 'react';
import { CHECKOUT_STEPS } from 'app/landing/utils/constants';

interface CheckoutStepperProps {
  currentStep: number;
}

export const CheckoutStepper = ({ currentStep }: CheckoutStepperProps) => (
  <div className="d-flex justify-content-between align-items-center mb-4 position-relative">
    {CHECKOUT_STEPS.map((step, index) => {
      const isActive = index === currentStep;
      const isCompleted = index < currentStep;
      return (
        <div key={step.key} className="d-flex flex-column align-items-center flex-fill position-relative">
          <div
            className="rounded-circle d-flex align-items-center justify-content-center fw-bold"
            style={{
              width: '2.5rem',
              height: '2.5rem',
              backgroundColor: isActive || isCompleted ? 'var(--kn-color-primary)' : 'var(--kn-color-surface)',
              color: isActive || isCompleted ? 'var(--kn-color-text-inverse)' : 'var(--kn-color-text-muted)',
              border: `2px solid ${isActive || isCompleted ? 'var(--kn-color-primary)' : 'var(--kn-color-border-strong)'}`,
              transition: 'all var(--kn-transition-base)',
            }}
          >
            {isCompleted ? '✓' : index + 1}
          </div>
          <span
            className="small mt-2 text-center d-none d-md-block"
            style={{
              color: isActive ? 'var(--kn-color-text)' : 'var(--kn-color-text-secondary)',
              fontWeight: isActive ? 600 : 400,
            }}
          >
            {step.label}
          </span>
          {index < CHECKOUT_STEPS.length - 1 && (
            <div
              className="position-absolute"
              style={{
                top: '1.25rem',
                left: 'calc(50% + 1.5rem)',
                right: 'calc(-50% + 1.5rem)',
                height: '2px',
                backgroundColor: isCompleted ? 'var(--kn-color-primary)' : 'var(--kn-color-border-strong)',
                transition: 'background-color var(--kn-transition-base)',
              }}
            />
          )}
        </div>
      );
    })}
  </div>
);

export default CheckoutStepper;
