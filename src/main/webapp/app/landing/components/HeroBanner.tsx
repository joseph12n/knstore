import React from 'react';
import { Container } from 'react-bootstrap';
import { Link } from 'react-router';

interface HeroBannerProps {
  title: string;
  subtitle?: string;
  ctaText?: string;
  ctaLink?: string;
  backgroundImage?: string;
}

export const HeroBanner = ({ title, subtitle, ctaText = 'Ver colección', ctaLink = '/buscar', backgroundImage }: HeroBannerProps) => (
  <section
    className="position-relative d-flex align-items-center text-white overflow-hidden"
    style={{
      minHeight: 'clamp(280px, 50vh, 520px)',
      backgroundColor: 'var(--kn-color-primary)',
      backgroundImage: backgroundImage ? `linear-gradient(rgba(0,0,0,0.35), rgba(0,0,0,0.45)), url(${backgroundImage})` : undefined,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
    }}
  >
    <Container className="position-relative z-1 py-5">
      <div className="col-lg-6">
        <h1 className="display-4 fw-bold mb-3" style={{ color: 'var(--kn-color-text-inverse)' }}>
          {title}
        </h1>
        {subtitle && (
          <p className="lead mb-4" style={{ color: 'rgba(255,255,255,0.9)' }}>
            {subtitle}
          </p>
        )}
        <Link to={ctaLink} className="btn btn-accent btn-lg">
          {ctaText}
        </Link>
      </div>
    </Container>
  </section>
);

export default HeroBanner;
