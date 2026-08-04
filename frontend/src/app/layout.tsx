import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Combustify — Preços de Combustível em Tempo Real',
  description: 'Encontre os melhores preços de combustível em Cuiabá',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}
